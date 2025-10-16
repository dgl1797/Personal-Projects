import { ChatService } from '@Services/chat.service';
import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'user-homepage',
  standalone: true,
  templateUrl: '../../templates/user/home.view.html',
})
export class HomePage implements OnInit {
  onlineList: string[] = [];

  constructor(private chatService: ChatService) {}
  ngOnInit(): void {
    this.chatService.onlineList$.subscribe((val) => (this.onlineList = val));
    this.chatService.requestOnlineList();
  }

  handleOnlineUserClick(target: string) {
    //prettier-ignore
    if (!this.chatService.chatOpened(target)) this.chatService.requestChat(target);
    this.chatService.switchToTarget(target);
  }
}
