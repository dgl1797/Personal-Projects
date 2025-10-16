import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class SessionStorage {
  private accessToken: string = 'access token';
  private storage: Storage = sessionStorage;

  /** TOKEN */
  set token(token: string) {
    this.storage.setItem(this.accessToken, token);
  }
  get token(): string {
    return this.storage.getItem(this.accessToken) ?? '';
  }
  deleteToken(): void {
    this.storage.removeItem(this.accessToken);
  }

  /** USERNAME */
  set username(username: string) {
    this.storage.setItem('username', username);
  }
  get username(): string {
    return this.storage.getItem('username') ?? '';
  }
  deleteUsername(): void {
    this.storage.removeItem('username');
  }

  /** USERINFO */
  set userInfo(userInfo) {
    this.storage.setItem('user info', JSON.stringify(userInfo));
  }
  get userInfo() {
    return JSON.parse(this.storage.getItem('user info') ?? '');
  }
  deleteUserInfo(): void {
    this.storage.removeItem('user info');
  }

  /** NOTIFICATIONS */
  set notifications(notifications: string[]) {
    this.storage.setItem('notifications', JSON.stringify(notifications));
  }
  get notifications(): string[] {
    return JSON.parse(this.storage?.getItem('notifications') ?? '[]');
  }
  deleteNotifications(): void {
    this.storage.removeItem('notifications');
  }

  /** Cached Chats: only usernames will be cached so when app reboots the chats will be automatically reloaded */
  set chats(channels: string[]) {
    this.storage.setItem('chats', JSON.stringify(channels));
  }
  get chats(): string[] {
    return JSON.parse(this.storage.getItem('chats') ?? '[]');
  }
  deleteChats(): void {
    this.storage.removeItem('chats');
  }
}
