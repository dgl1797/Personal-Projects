import { ReceivedMessageDTO } from './ReceivedMessageDTO';

export interface ChatDTO {
  type: 'user:state' | 'chat:messages' | 'online:list' | 'receive';
  payload: any; // JSON.stringified object
  online?: boolean;
}

export interface CurrentChatType {
  selectedTarget: string;
  chat: ReceivedMessageDTO[];
}
