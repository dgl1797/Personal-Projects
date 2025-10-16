import { UserCore, UserDTO } from '@App/DTOs/UserDTO';
import { SessionStorage } from '@App/app.storage';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable()
export class UserService {
  private baseUrl = environment.javaHost;
  private serviceUri = 'users';

  private userBehaviorSubject = new BehaviorSubject<UserCore | undefined>(
    undefined
  );
  userInfo$ = this.userBehaviorSubject.asObservable();

  set userInfo(uinfo: UserCore) {
    this.userBehaviorSubject.next(uinfo);
  }

  private username: string = '';
  private token: string = '';

  constructor(private http: HttpClient, private session: SessionStorage) {
    this.username = this.session.username;
    this.token = this.session.token;
  }

  getUserInfo() {
    return this.http.get<UserDTO>(
      `${this.baseUrl}/${this.serviceUri}/${this.username}`,
      {
        headers: {
          Authorization: `Bearer ${this.token}`,
        },
        responseType: 'json',
      }
    );
  }
}
