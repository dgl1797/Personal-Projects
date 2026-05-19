import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { SessionStorage } from '@App/app.storage';
import { UserService } from '@Services/user.service';
import { ProjectService } from '@Services/project.service';
import { UserCore, ParticipationProjection } from '@App/DTOs/UserDTO';
import { FullProjectDTO, TaskCoreData, ParticipantCoreData } from '@App/DTOs/ProjectDTO';
import { CreateProjectModal } from '@Components/create-project-modal.component';

@Component({
  selector: 'projects-page',
  standalone: true,
  imports: [CreateProjectModal, FormsModule],
  templateUrl: '../../templates/projects/projects.view.html',
})
export class ProjectsPage implements OnInit {
  @ViewChild(CreateProjectModal) createProjectModal!: CreateProjectModal;
  userInfo: UserCore | undefined;
  isPremium: boolean = false;
  isLoading: boolean = true;

  // Project lists
  ownedProjects: { id: number; name: string }[] = [];
  participatedProjects: ParticipationProjection[] = [];

  // Selected project detail
  selectedProjectId: number | undefined;
  selectedProject: FullProjectDTO | undefined = undefined;
  isLoadingDetail: boolean = false;

  // Task management
  taskErrorMessage: string = '';
  taskSuccessMessage: string = '';
  newTaskName: string = '';
  newTaskDescription: string = '';
  newTaskAssignees: string[] = [];

  // Available participants to assign to tasks (all project members except owner)
  get availableAssignees(): string[] {
    if (!this.selectedProject) return [];
    return this.selectedProject.project.participants
      .filter(p => p.username !== this.selectedProject?.owner)
      .map(p => p.username);
  }

  toggleAssignee(username: string) {
    const idx = this.newTaskAssignees.indexOf(username);
    if (idx >= 0) {
      this.newTaskAssignees.splice(idx, 1);
    } else {
      this.newTaskAssignees.push(username);
    }
  }

  // Task counts
  get todoCount(): number {
    return this.selectedProject?.project.tasks.filter(t => t.state === 'todo').length ?? 0;
  }

  get ongoingCount(): number {
    return this.selectedProject?.project.tasks.filter(t => t.state === 'ongoing').length ?? 0;
  }

  get doneCount(): number {
    return this.selectedProject?.project.tasks.filter(t => t.state === 'done').length ?? 0;
  }

  constructor(
    private session: SessionStorage,
    private userService: UserService,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    const stored = this.session.userInfo;
    if (stored) {
      this.userInfo = stored.info;
      this.isPremium = stored.info?.type === 'premium';
      // Convert ownedProjects map to array
      const ownedMap: { [key: number]: string } = stored.ownedProjects ?? {};
      this.ownedProjects = Object.entries(ownedMap).map(([id, name]) => ({
        id: Number(id),
        name,
      }));
      this.participatedProjects = stored.participationSet ?? [];
      this.isLoading = false;
    } else {
      // Fetch from API if not cached
      this.userService.getUserInfo().subscribe({
        next: (data) => {
          this.userInfo = data.userInfo;
          this.isPremium = data.userInfo?.type === 'premium';
          const ownedMap: { [key: number]: string } = data.ownedProjects ?? {};
          this.ownedProjects = Object.entries(ownedMap).map(([id, name]) => ({
            id: Number(id),
            name,
          }));
          this.participatedProjects = data.participationSet ?? [];
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        },
      });
    }
  }

  selectProject(projectId: number) {
    this.selectedProjectId = projectId;
    this.isLoadingDetail = true;
    this.selectedProject = undefined;
    this.projectService.getProject(projectId).subscribe({
      next: (data) => {
        this.selectedProject = data;
        this.isLoadingDetail = false;
        this.taskErrorMessage = '';
        this.taskSuccessMessage = '';
      },
      error: (err: HttpErrorResponse) => {
        this.isLoadingDetail = false;
        this.taskErrorMessage = typeof err.error === 'string' ? err.error : 'Failed to load project details';
      },
    });
  }

  backToList() {
    this.selectedProjectId = undefined;
    this.selectedProject = undefined;
    this.taskErrorMessage = '';
    this.taskSuccessMessage = '';
  }

  isProjectOwner(): boolean {
    return this.selectedProject?.owner === this.userInfo?.username;
  }

  canManageTasks(): boolean {
    return this.isPremium && this.isProjectOwner();
  }

  onProjectCreated() {
    // Refresh the project list from user info
    this.userService.getUserInfo().subscribe({
      next: (data) => {
        this.userService.userInfo = data.userInfo;
        this.session.userInfo = {
          info: data.userInfo,
          ownedProjects: data.ownedProjects,
          participationSet: data.participationSet,
          incompleteTasks: data.uncompleteTasks,
        };
        const ownedMap: { [key: number]: string } = data.ownedProjects ?? {};
        this.ownedProjects = Object.entries(ownedMap).map(([id, name]) => ({
          id: Number(id),
          name,
        }));
        this.participatedProjects = data.participationSet ?? [];
      },
    });
  }

  addTask() {
    if (!this.selectedProjectId || !this.newTaskName.trim()) return;
    this.taskErrorMessage = '';
    this.projectService.addTask(
      this.selectedProjectId,
      this.newTaskName.trim(),
      this.newTaskDescription.trim(),
      this.newTaskAssignees
    ).subscribe({
      next: () => {
        this.newTaskName = '';
        this.newTaskDescription = '';
        this.newTaskAssignees = [];
        this.taskSuccessMessage = 'Task added successfully!';
        setTimeout(() => (this.taskSuccessMessage = ''), 3000);
        // Refresh project detail
        this.selectProject(this.selectedProjectId!);
      },
      error: (err: HttpErrorResponse) => {
        this.taskErrorMessage = typeof err.error === 'string' ? err.error : 'Failed to add task';
      },
    });
  }

  deleteTask(taskId: number) {
    if (!this.selectedProjectId) return;
    this.taskErrorMessage = '';
    this.projectService.deleteTask(this.selectedProjectId, taskId).subscribe({
      next: () => {
        this.taskSuccessMessage = 'Task deleted!';
        setTimeout(() => (this.taskSuccessMessage = ''), 3000);
        this.selectProject(this.selectedProjectId!);
      },
      error: (err: HttpErrorResponse) => {
        this.taskErrorMessage = typeof err.error === 'string' ? err.error : 'Failed to delete task';
      },
    });
  }

  assignTask(taskId: number) {
    if (!this.selectedProjectId) return;
    this.taskErrorMessage = '';
    this.projectService.updateTask(this.selectedProjectId, taskId, { newState: 'ongoing' }).subscribe({
      next: () => {
        this.selectProject(this.selectedProjectId!);
      },
      error: (err: HttpErrorResponse) => {
        this.taskErrorMessage = typeof err.error === 'string' ? err.error : 'Failed to update task';
      },
    });
  }

  completeTask(taskId: number) {
    if (!this.selectedProjectId) return;
    this.taskErrorMessage = '';
    this.projectService.updateTask(this.selectedProjectId, taskId, { newState: 'done' }).subscribe({
      next: () => {
        this.selectProject(this.selectedProjectId!);
      },
      error: (err: HttpErrorResponse) => {
        this.taskErrorMessage = typeof err.error === 'string' ? err.error : 'Failed to complete task';
      },
    });
  }

  openCreateModal() {
    this.createProjectModal.open();
  }

  deleteProject() {
    if (!this.selectedProjectId || !confirm('Are you sure you want to delete this project?')) return;
    this.projectService.deleteProject(this.selectedProjectId).subscribe({
      next: () => {
        this.onProjectCreated();
        this.backToList();
      },
      error: (err: HttpErrorResponse) => {
        this.taskErrorMessage = typeof err.error === 'string' ? err.error : 'Failed to delete project';
      },
    });
  }
}
