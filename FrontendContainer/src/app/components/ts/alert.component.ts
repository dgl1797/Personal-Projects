import { NgSwitch, NgSwitchCase } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-alert',
  standalone: true,
  templateUrl: '../templates/alert.template.html',
  imports: [NgSwitch, NgSwitchCase],
})
export class AppAlert {
  @Input() message: string = '';
  @Input() type: 'info' | 'success' | 'error' | 'warning' = 'info';
}
