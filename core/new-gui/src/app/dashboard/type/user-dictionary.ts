
export interface UserDictionary {
  id: number;
  name: string;
  items: string[];
  description: string;
}

export interface DictionaryUploadItem {
  file: File;
  name: string;
  description: string;
}

export interface ManualDictionary {
  name: string;
  content: string;
  separator: string;
  description: string;
}
