import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable()
export class AuthService {
  private baseServer: string = environment.javaHost;
  private uri: string = 'auth';

  /** HttpClient injection from injectable module HttpClientModule imported in app.ts */
  constructor(private http: HttpClient) {}

  getAuth(username: string, password: string) {
    return this.http.get(`${this.baseServer}/${this.uri}`, {
      responseType: 'text',
      headers: {
        Authorization: `${username}:${password}`,
      },
    });
  }

  postAuth(username: string, email: string, password: string, type: string) {
    return this.http.post(
      `${this.baseServer}/${this.uri}`,
      {
        userInfo: {
          username,
          email,
          type,
        },
        password,
      },
      { responseType: 'text' }
    );
  }

  deleteAuth(username: string, token: string) {
    return this.http.delete(`${this.baseServer}/${this.uri}/${username}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      responseType: 'text',
    });
  }
}
