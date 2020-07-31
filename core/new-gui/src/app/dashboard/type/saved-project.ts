
export interface SavedProject {
  id: string;
  name: string;
  creationTime: string;
  lastModifiedTime: string;
}


export interface SavedProjectList {
  projectList: SavedProject[];
}
