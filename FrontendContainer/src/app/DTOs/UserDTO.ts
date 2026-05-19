export interface TaskPresentation {
  id: number;
  name: string;
  pjid: number;
  project: string;
}

export interface ParticipationProjection {
  id: number;
  name: string;
  role: string;
}

export interface UserCore {
  username: string;
  email: string;
  type: string;
}

export interface UserDTO {
  ownedProjects: { [key: number]: string };
  participationSet: ParticipationProjection[];
  uncompleteTasks: TaskPresentation[];
  userInfo: UserCore;
}