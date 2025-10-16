import { ChatDTO, CurrentChatType } from '@App/DTOs/ChatDTO';
import { ReceivedMessageDTO } from '@App/DTOs/ReceivedMessageDTO';
import { SessionStorage } from '@App/app.storage';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject, timestamp } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable()
export class ChatService {
  private baseUrl = environment.chatHost;
  private ws: WebSocket | undefined = undefined;
  private username: string = '';

  /** Subjects */
  private onlineListSubject = new BehaviorSubject<string[]>([]);
  private channels = new BehaviorSubject<string[]>([]);
  private chats: { [key: string]: BehaviorSubject<ReceivedMessageDTO[]> } = {};
  private errorListener = new Subject<string>();
  private notificationListener: BehaviorSubject<string[]>;
  private currentTarget = new BehaviorSubject<string>('');

  /** Observers */
  errors$ = this.errorListener.asObservable();
  onlineList$ = this.onlineListSubject.asObservable();
  notifications$: Observable<string[]>;
  channels$ = this.channels.asObservable();
  chats$: { [key: string]: Observable<ReceivedMessageDTO[]> } = {};
  currentTarget$ = this.currentTarget.asObservable();

  openChat(target: string, initialContent: ReceivedMessageDTO[]) {
    if (!this.connected) return;
    this.channels.next(this.channels.getValue().concat(target));
    this.chats[target] = new BehaviorSubject<ReceivedMessageDTO[]>(
      initialContent
    );
    this.chats$[target] = this.chats[target].asObservable();
  }

  constructor(private session: SessionStorage) {
    //prettier-ignore
    this.notificationListener = new BehaviorSubject<string[]>(this.session.notifications);
    this.notifications$ = this.notificationListener.asObservable();

    const cachedChannels = this.session.chats;
    cachedChannels.forEach((cc) => this.requestChat(cc));
  }

  /** Handler */
  private processMessage(data: ChatDTO) {
    switch (data?.type ?? '') {
      case 'online:list':
        this.onlineListSubject.next(data?.payload ?? []);
        break;
      case 'chat:messages':
        const chatTarget = data.payload?.['target'] ?? '';
        if (!chatTarget) this.errorListener.next('invalid target');
        this.openChat(chatTarget, data.payload?.['messages'] ?? []);
        break;
      case 'user:state':
        //prettier-ignore
        this.onlineListSubject.next(data.online
          ? [...this.onlineListSubject.getValue().concat(data.payload)]
          : [...this.onlineListSubject.getValue().filter(el => el !== data.payload)]
        );
        break;
      case 'receive':
        //prettier-ignore
        const sender = data.payload?.['sender'];
        const receiver = data.payload?.['receiver'];
        if (!sender || !receiver) return; // discarded
        const target = sender === this.username ? receiver : sender;
        if (this.currentTarget.getValue() === target) {
          // message arrived on current chat so it gets updated
          //prettier-ignore
          this.chats[target].next(this.chats[target].getValue().concat(data.payload));
        } else {
          if (this.chatOpened(target))
            //prettier-ignore
            this.chats[target].next(this.chats[target].getValue().concat(data.payload));

          const newValue = this.notificationListener.getValue().concat(target);
          this.notificationListener.next(newValue);
          this.session.notifications = newValue;
        }
        break;
    }
  }

  waitTarget(target: string): Promise<void> {
    return new Promise((resolve, reject) => {
      let tick = 0;
      const interval = setInterval(() => {
        if (this.chatOpened(target)) {
          clearInterval(interval);
          resolve();
        }
        if (tick === 50) {
          reject('timed out');
        }
        tick++;
      }, 100);
    });
  }

  chatOpened(target: string) {
    return !!this.chats[target];
  }

  close() {
    this.onlineListSubject.complete();
    Object.keys(this.chats).forEach((k) => this.chats[k].complete());
    this.channels.complete();
    this.errorListener.complete();
    this.notificationListener.complete();
    this.ws?.close();
  }

  connect(service: string, user: string) {
    this.username = user;
    this.ws = new WebSocket(`${this.baseUrl}/${service}/${user}`);
    //prettier-ignore
    this.ws.onmessage = (event: MessageEvent<string>) => this.processMessage(JSON.parse(event.data) as ChatDTO);
    //prettier-ignore
    this.ws.onerror = () => this.errorListener.next(`unexpected error on socket`);
    this.ws.onclose = () => this.close();
  }

  clearNotifications() {
    this.notificationListener.next([]);
    this.session.deleteNotifications();
  }
  unmarkNotification(target: string) {
    //prettier-ignore
    const newValue = this.notificationListener.getValue().filter(u => u !== target);
    this.notificationListener.next(newValue);
    this.session.notifications = newValue;
  }
  requestChat(target: string) {
    //prettier-ignore
    this.ws && this.ws.OPEN && this.ws?.send(JSON.stringify({
      type: 'get:chat',
      target
    }));
  }
  requestOnlineList() {
    //prettier-ignore
    this.ws &&this.ws.OPEN &&this.ws?.send(JSON.stringify({ type: 'get:online' }));
  }
  switchToTarget(target: string) {
    this.currentTarget.next(target);
  }
  newMessage(target: string, content: string) {
    const message = {
      type: 'send',
      receiver: target,
      content: content,
      timestamp: Date.now(),
    };
    this.ws && this.ws.OPEN && this.ws.send(JSON.stringify(message));
    this.chats[target].next(
      this.chats[target].getValue().concat({
        sender: this.username,
        receiver: target,
        content: content,
        timestamp: message.timestamp.toString(),
      })
    );
  }
  closeChat(target: string) {
    if (this.chatOpened(target)) {
      this.chats[target].complete();
      delete this.chats[target];
      delete this.chats$[target];
      this.channels.next(
        this.channels.getValue().filter((el) => el !== target)
      );
    }
  }

  get connected() {
    return !!this.ws && this.ws.OPEN;
  }
}
