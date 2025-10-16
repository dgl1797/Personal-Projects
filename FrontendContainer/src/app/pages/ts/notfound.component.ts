import { SessionStorage } from '@App/app.storage';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  template: `
    <div
      class="flex flex-col gap-12 w-full h-screen justify-center items-center"
    >
      <h1 class="text-8xl text-pink-700">404: Not Found!</h1>
      <p class="text-3xl">
        Looks like you are trying to access a page that doesn't exist!
      </p>
      <button
        class="btn btn-accent border-pink-700 bg-pink-700 hover:bg-pink-500 hover:border-pink-500 text-white"
        (click)="getHome()"
      >
        Home
      </button>
    </div>
  `,
})
export class NotFound {
  constructor(private router: Router, private session: SessionStorage) {}

  getHome() {
    const accessToken = this.session.token;
    this.router.navigate(accessToken ? ['dashboard', 'projects'] : ['']);
  }
}
