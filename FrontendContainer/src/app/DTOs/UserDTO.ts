interface TaskPresentation {
  id: number;
  name: string;
  pjid: number;
  project: string;
}

export interface UserCore {
  username: string;
  email: string;
  type: string;
}

export interface UserDTO {
  ownedProjects: { [key: number]: string };
  participationSet: { [key: number]: string };
  uncompleteTasks: TaskPresentation[];
  userInfo: UserCore;
}
