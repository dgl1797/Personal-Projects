import { Dashboard } from '@Pages/dash.component';
import { LoginPage } from '@Pages/login.component';
import { NotFound } from '@Pages/notfound.component';
import { ProjectsPage } from '@Pages/projects/projects.component';
import { HomePage } from '@Pages/user/home.component';
import { AuthService } from '@Services/auth.service';
import { ChatService } from '@Services/chat.service';
import { UserService } from '@Services/user.service';
import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', component: LoginPage },
  {
    path: 'dashboard',
    component: Dashboard,
    // injects the services to the dashboard and all the children routes so that all shares the same service instance
    providers: [AuthService, ChatService, UserService],
    children: [
      { path: 'projects', component: ProjectsPage },
      { path: 'home', component: HomePage },
    ],
  },
  { path: '**', component: NotFound },
];
