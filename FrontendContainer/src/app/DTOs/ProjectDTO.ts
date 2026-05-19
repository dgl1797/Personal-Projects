export interface ParticipantCoreData {
  id: number;
  username: string;
  email: string;
  role: string;
  added: number[];
}

export interface TaskCoreData {
  id: number;
  name: string;
  state: 'todo' | 'ongoing' | 'done';
  description: string;
}

export interface FullProjectDTO {
  project: {
    id: number;
    name: string;
    owner: {
      id: number;
      email: string;
      username: string;
    };
    participants: ParticipantCoreData[];
    tasks: TaskCoreData[];
  };
  owner: string;
}