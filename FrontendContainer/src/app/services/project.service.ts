import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { SessionStorage } from '@App/app.storage';
import { FullProjectDTO } from '@App/DTOs/ProjectDTO';

@Injectable()
export class ProjectService {
  private baseUrl = environment.javaHost;
  private serviceUri = 'projects';

  private username: string = '';
  private token: string = '';

  constructor(private http: HttpClient, private session: SessionStorage) {
    this.username = this.session.username;
    this.token = this.session.token;
  }

  createProject(projectName: string, participants: { [key: string]: string }) {
    return this.http.post(
      `${this.baseUrl}/${this.serviceUri}`,
      {
        projectName,
        participants,
      },
      {
        headers: {
          Authorization: `Bearer ${this.token}`,
        },
        responseType: 'text',
      }
    );
  }

  getProject(projectId: number) {
    return this.http.get<FullProjectDTO>(
      `${this.baseUrl}/${this.serviceUri}/${projectId}`,
      {
        headers: {
          Authorization: `Bearer ${this.token}`,
        },
      }
    );
  }

  deleteProject(projectId: number) {
    return this.http.delete(
      `${this.baseUrl}/${this.serviceUri}/${projectId}`,
      {
        headers: {
          Authorization: `Bearer ${this.token}`,
        },
        responseType: 'text',
      }
    );
  }

  addTask(projectId: number, name: string, description: string, assignees: string[] = []) {
    return this.http.post(
      `${this.baseUrl}/${this.serviceUri}/${projectId}/tasks`,
      {
        taskName: name,
        description,
        assignees,
      },
      {
        headers: {
          Authorization: `Bearer ${this.token}`,
        },
        responseType: 'text',
      }
    );
  }

  updateTask(projectId: number, taskId: number, updates: {
    newState?: string;
    newDescription?: string;
    newAssignees?: number[];
  }) {
    const body: any = {};
    if (updates.newState !== undefined) body.newState = updates.newState;
    if (updates.newDescription !== undefined) body.newDescription = updates.newDescription;
    if (updates.newAssignees !== undefined) body.newAssignees = updates.newAssignees;

    return this.http.put(
      `${this.baseUrl}/${this.serviceUri}/${projectId}/tasks/${taskId}`,
      body,
      {
        headers: {
          Authorization: `Bearer ${this.token}`,
        },
        responseType: 'text',
      }
    );
  }

  deleteTask(projectId: number, taskId: number) {
    return this.http.delete(
      `${this.baseUrl}/${this.serviceUri}/${projectId}/tasks/${taskId}`,
      {
        headers: {
          Authorization: `Bearer ${this.token}`,
        },
        responseType: 'text',
      }
    );
  }
}