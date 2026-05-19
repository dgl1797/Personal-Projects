import { ReceivedMessageDTO } from '@App/DTOs/ReceivedMessageDTO';
import { UserCore } from '@App/DTOs/UserDTO';
import { SessionStorage } from '@App/app.storage';
import { TimePipe } from '@App/pipes/time.pipe';
import { AppAlert } from '@Components/alert.component';
import { CreateProjectModal } from '@Components/create-project-modal.component';
import { AuthService } from '@Services/auth.service';
import { ChatService } from '@Services/chat.service';
import { UserService } from '@Services/user.service';
import { ProjectService } from '@Services/project.service';
import { NgOptimizedImage } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';

const actionType = ['info', 'error', 'warning', 'success'] as const;

interface Action {
  type: (typeof actionType)[number];
  message: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterOutlet, AppAlert, NgOptimizedImage, TimePipe, CreateProjectModal],
  templateUrl: '../templates/dash.view.html',
})
export class Dashboard implements OnInit, OnDestroy {
  @ViewChild('notificationSound') audioPlayerRef!: ElementRef;
  @ViewChild('messageInput') messageInput!: ElementRef;
  @ViewChild(CreateProjectModal) createProjectModal!: CreateProjectModal;

  private clearingInterval: any = undefined;
  private token: string = '';
  private currentSubscription: Subscription | undefined = undefined;

  username: string = '';
  userInfo: UserCore | undefined = undefined;
  isLoading: boolean = false;

  actionQueue: Action[] = [];
  infoMessage: string = '';
  errorMessage: string = '';
  warningMessage: string = '';
  successMessage: string = '';

  /** LISTENERS */
  notifications: string[] = [];
  channels: string[] = []; // opened chats listener
  selectedTarget: string | undefined = undefined; // currently selected target
  currentChat: ReceivedMessageDTO[] | undefined = undefined; // currently displayed chat

  constructor(
    private router: Router,
    private session: SessionStorage,
    private authService: AuthService,
    private userService: UserService,
    private chatService: ChatService
  ) {}

  ngOnDestroy(): void {}
  ngOnInit(): void {
    /** Initializations */
    this.username = this.session.username ?? '';
    this.token = this.session.token;
    if (!this.username || !this.token) {
      this.router.navigateByUrl('#expired');
      return;
    }
    if (!this.chatService.connected)
      this.chatService.connect('users', this.username);

    /** Subscriptions */

    // user info
    this.userService.userInfo$.subscribe(
      (newInfo) => (this.userInfo = newInfo)
    );

    // notifications
    this.chatService.notifications$.subscribe((notificationList) => {
      if (this.notifications.length < notificationList.length) {
        this.playNotification();
      }
      this.notifications = notificationList;
    });

    // open channels
    this.chatService.channels$.subscribe((newChannels) => {
      this.channels = newChannels;
    });

    // initial chat setup
    if (this.channels?.[0]) {
      this.chatService.switchToTarget(this.channels[0]);
    }

    this.chatService.currentTarget$.subscribe(async (newtarget) => {
      this.selectedTarget = newtarget;
      if (this.selectedTarget) {
        if (this.currentSubscription) this.currentSubscription.unsubscribe();
        await this.chatService.waitTarget(this.selectedTarget);
        //prettier-ignore
        this.chatService.chats$[this.selectedTarget].subscribe((messages) => this.currentChat = messages)
      }
    });

    /** Storage setup */
    const accessToken = this.session.token;
    if (!accessToken) this.router.navigate(['']);

    const nroutes = this.router.url.split('/').length;
    if (nroutes === 2) this.router.navigate(['dashboard', 'home']);

    /** API Call */
    this.isLoading = true;
    this.userService.getUserInfo().subscribe({
      next: (data) => {
        this.userService.userInfo = data.userInfo;
        this.session.userInfo = {
          info: data.userInfo,
          ownedProjects: data.ownedProjects,
          participationSet: data.participationSet,
          incompleteTasks: data.uncompleteTasks,
        };
      },
      error: (error: HttpErrorResponse) => {
        if (error.error === 'Internal Encription Error') {
          this.session.deleteToken();
          this.session.deleteUsername();
          this.router.navigateByUrl('#expired');
        }
        this.pushAction({
          type: 'error',
          message:
            typeof error.error === 'string' ? error.error : error.message,
        });
        this.isLoading = false;
      },
      complete: () => (this.isLoading = false),
    });
  }

  /** SERVICES */
  logout(): void {
    this.authService.deleteAuth(this.username, this.session.token).subscribe({
      next: (data) => {
        this.session.deleteToken();
        this.session.deleteUsername();
        this.chatService.close();
      },
      error: (error: HttpErrorResponse) => {
        this.pushAction({
          type: 'error',
          message:
            typeof error.error === 'string' ? error.error : error.message,
        });
      },
      complete: () => {
        this.router.navigateByUrl('#login');
      },
    });
  }

  clearNotification(target: string) {
    this.chatService.unmarkNotification(target);
    //prettier-ignore
    if(!this.chatService.chatOpened(target)) this.chatService.requestChat(target);
    this.chatService.switchToTarget(target);
  }

  switchToOpenTarget(newTarget: string) {
    this.chatService.switchToTarget(newTarget);
  }

  sendMessage() {
    const content = this.messageInput.nativeElement.value;
    if (this.selectedTarget && content)
      this.chatService.newMessage(
        this.selectedTarget,
        this.messageInput.nativeElement.value
      );
    this.messageInput.nativeElement.value = '';
  }
  removeTargetedChat() {
    if (this.selectedTarget) this.chatService.closeChat(this.selectedTarget);
    this.currentChat = undefined;
  }

  /** DASHBOARD */
  goto(destination: string): void {
    this.router.navigate(['dashboard', destination]);
  }

  openCreateProjectModal() {
    this.createProjectModal.open();
  }

  onProjectCreated() {
    this.pushAction({ type: 'success', message: 'Project created successfully!' });
    // Refresh user info
    this.userService.getUserInfo().subscribe({
      next: (data) => {
        this.userService.userInfo = data.userInfo;
        this.session.userInfo = {
          info: data.userInfo,
          ownedProjects: data.ownedProjects,
          participationSet: data.participationSet,
          incompleteTasks: data.uncompleteTasks,
        };
      },
      error: (error: HttpErrorResponse) => {
        this.pushAction({
          type: 'error',
          message: typeof error.error === 'string' ? error.error : error.message,
        });
      },
    });
  }

  private pushAction(action: Action) {
    this.actionQueue.push(action);
    if (!this.clearingInterval) this.clearActionList();
  }

  private clearActionList() {
    const processAction = () => {
      const nextAction = this.actionQueue.shift();
      if (!nextAction) {
        //prettier-ignore
        this.errorMessage = this.infoMessage = this.warningMessage = this.successMessage = '';
        clearInterval(this.clearingInterval);
        this.clearingInterval = undefined;
      }
      switch (nextAction?.type) {
        case 'error':
          this.errorMessage = nextAction.message;
          setTimeout(() => {
            this.errorMessage = '';
          }, 5000);
          break;
        case 'info':
          this.infoMessage = nextAction.message;
          setTimeout(() => {
            this.errorMessage = '';
          }, 5000);
          break;
        case 'success':
          this.successMessage = nextAction.message;
          setTimeout(() => {
            this.successMessage = '';
          }, 5000);
          break;
        case 'warning':
          this.warningMessage = nextAction.message;
          setTimeout(() => {
            this.warningMessage = '';
          }, 5000);
          break;
      }
    };
    processAction();
    this.clearingInterval = setInterval(processAction, 5000);
  }

  playNotification() {
    this.audioPlayerRef && this.audioPlayerRef.nativeElement.play();
  }
}
