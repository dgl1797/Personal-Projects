import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function forbiddenEmail(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const regex: RegExp = /^[\w\d.]+@[\w.]+\.[a-z]{2,4}$/gi;
    return regex.test(control.value) ? null : { invalidEmail: true };
  };
}

export function passwordsMatch(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const password = control.get('password')?.value ?? '';
    const confirm = control.get('confirmPassword')?.value ?? '';
    return password === confirm ? null : { invalidConfirm: true };
  };
}
