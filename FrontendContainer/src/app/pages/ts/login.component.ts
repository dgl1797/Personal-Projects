import { forbiddenEmail, passwordsMatch } from '@Validators/login.validator';
import { NgOptimizedImage } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AppAlert } from '@Components/alert.component';
import { Router } from '@angular/router';
import { AuthService } from '@Services/auth.service';
import { SessionStorage } from '@App/app.storage';

@Component({
  selector: 'login-page',
  standalone: true,
  providers: [AuthService],
  templateUrl: '../templates/login.view.html',
  imports: [ReactiveFormsModule, NgOptimizedImage, AppAlert],
})
export class LoginPage {
  isLoading: boolean = false;
  isSignup: boolean = true;
  httpError: string = '';
  private gotoLogin = false;

  signupFormGroup = new FormGroup(
    {
      username: new FormControl<string>('', Validators.required),
      email: new FormControl<string>('', [
        Validators.required,
        forbiddenEmail(),
      ]),
      password: new FormControl<string>('', Validators.required),
      confirmPassword: new FormControl<string | undefined>(
        undefined,
        Validators.required
      ),
      subPlan: new FormControl<'standard' | 'premium' | undefined>(
        undefined,
        Validators.required
      ),
    },
    { validators: passwordsMatch() }
  );
  loginFormGroup = new FormGroup({
    username: new FormControl<string>('', Validators.required),
    password: new FormControl<string>('', Validators.required),
  });

  constructor(
    private router: Router,
    private authService: AuthService,
    private session: SessionStorage
  ) {
    const action = router.url.split('#')[1];
    this.gotoLogin = action === 'expired' || action === 'login';
    if (this.gotoLogin) {
      this.signupFormGroup.reset();
      this.loginFormGroup.reset();
      this.isSignup = false;
      if (action === 'expired') this.popupError('Session Expired');
    }
  }

  swapSignUp(mode: 'login' | 'signup') {
    this.signupFormGroup.reset();
    this.loginFormGroup.reset();
    this.isSignup = mode === 'login' ? false : true;
  }

  private popupError(message: string) {
    this.httpError = message;
    setTimeout(() => {
      this.httpError = '';
    }, 5000);
  }

  formSubmit() {
    const username =
      this.signupFormGroup.get('username')?.value ??
      this.loginFormGroup.get('username')?.value ??
      '';
    const request = this.isSignup
      ? this.authService.postAuth(
          username,
          this.signupFormGroup.get('email')?.value ?? '',
          this.signupFormGroup.get('password')?.value ?? '',
          this.signupFormGroup.get('subPlan')?.value ?? 'standard'
        )
      : this.authService.getAuth(
          username,
          this.loginFormGroup.get('password')?.value ?? ''
        );

    this.isLoading = true;

    request.subscribe({
      next: (data) => {
        this.session.token = data;
        this.session.username = username;
      },
      error: (error: HttpErrorResponse) => {
        this.popupError(
          typeof error.error === 'string' ? error.error : error.message
        );
        this.session.deleteToken();
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
        this.router.navigate(['dashboard']);
      },
    });
  }
}
