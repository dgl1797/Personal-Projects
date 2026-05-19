import { Component, ElementRef, EventEmitter, Output, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ProjectService } from '@Services/project.service';
import { SessionStorage } from '@App/app.storage';

interface ParticipantForm {
  username: string;
  role: string;
}

@Component({
  selector: 'app-create-project-modal',
  standalone: true,
  imports: [FormsModule],
  templateUrl: '../templates/create-project.modal.html',
})
export class CreateProjectModal {
  @ViewChild('modalDialog') modalDialog!: ElementRef<HTMLDialogElement>;
  @ViewChild('projectNameInput') projectNameInput!: ElementRef<HTMLInputElement>;
  @Output() projectCreated = new EventEmitter<void>();

  participants: ParticipantForm[] = [];
  errorMessage: string = '';
  isSubmitting: boolean = false;

  constructor(
    private projectService: ProjectService,
    private session: SessionStorage
  ) {}

  open() {
    this.resetForm();
    this.modalDialog.nativeElement.showModal();
    setTimeout(() => this.projectNameInput?.nativeElement?.focus(), 100);
  }

  close() {
    this.modalDialog.nativeElement.close();
    this.resetForm();
  }

  private resetForm() {
    this.participants = [];
    this.errorMessage = '';
    this.isSubmitting = false;
  }

  addParticipant() {
    this.participants.push({ username: '', role: 'member' });
  }

  removeParticipant(index: number) {
    this.participants.splice(index, 1);
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).tagName === 'DIALOG') {
      this.close();
    }
  }

  submit() {
    const projectName = this.projectNameInput.nativeElement.value.trim();
    if (!projectName) {
      this.errorMessage = 'Project name is required';
      return;
    }

    const participantsMap: { [key: string]: string } = {};
    for (const p of this.participants) {
      const uname = p.username.trim();
      if (uname) {
        participantsMap[uname] = p.role;
      }
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    this.projectService.createProject(projectName, participantsMap).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.close();
        this.projectCreated.emit();
      },
      error: (err: HttpErrorResponse) => {
        this.isSubmitting = false;
        this.errorMessage = typeof err.error === 'string' ? err.error : 'Failed to create project';
      },
    });
  }
}