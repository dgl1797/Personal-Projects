import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { SessionStorage } from '@App/app.storage';
import { UserService } from '@Services/user.service';
import { UserCore, ParticipationProjection, TaskPresentation } from '@App/DTOs/UserDTO';

@Component({
  selector: 'profile-page',
  standalone: true,
  imports: [FormsModule],
  templateUrl: '../../templates/user/profile.view.html',
})
export class ProfilePage implements OnInit {
  userInfo: UserCore | undefined;
  isPremium: boolean = false;
  isLoading: boolean = true;

  ownedProjects: { id: number; name: string }[] = [];
  participatedProjects: ParticipationProjection[] = [];
  incompleteTasks: TaskPresentation[] = [];

  // Edit form fields
  isEditing: boolean = false;
  editEmail: string = '';
  editCurrentPassword: string = '';
  editNewPassword: string = '';
  editConfirmPassword: string = '';

  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private session: SessionStorage,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
  }

  private loadUserData() {
    this.isLoading = true;
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
        this.incompleteTasks = data.uncompleteTasks ?? [];
        this.editEmail = data.userInfo?.email ?? '';
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  toggleEdit() {
    this.isEditing = !this.isEditing;
    this.errorMessage = '';
    this.successMessage = '';
    if (!this.isEditing) {
      this.editCurrentPassword = '';
      this.editNewPassword = '';
      this.editConfirmPassword = '';
      this.editEmail = this.userInfo?.email ?? '';
    }
  }

  saveProfile() {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.editNewPassword && this.editNewPassword !== this.editConfirmPassword) {
      this.errorMessage = 'New passwords do not match';
      return;
    }

    const body: any = {};
    if (this.editEmail !== this.userInfo?.email) {
      body.email = this.editEmail;
    }
    if (this.editNewPassword) {
      body.currentPassword = this.editCurrentPassword;
      body.newPassword = this.editNewPassword;
    }

    if (Object.keys(body).length === 0) {
      this.toggleEdit();
      return;
    }

    this.userService.updateProfile(body).subscribe({
      next: (response: string) => {
        this.successMessage = response || 'Profile updated successfully!';
        this.toggleEdit();
        this.loadUserData();
        // Update session storage
        if (this.session.userInfo) {
          const stored = this.session.userInfo;
          stored.info.email = this.editEmail;
          this.session.userInfo = stored;
        }
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = typeof err.error === 'string' ? err.error : 'Failed to update profile';
      },
    });
  }

  gotoProjects() {
    this.router.navigate(['dashboard', 'projects']);
  }
}