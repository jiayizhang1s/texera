import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppSettings } from '../../../common/app-setting';
import { Observable } from 'rxjs/Observable';
import { SavedProject } from '../../type/saved-project';

import { MOCK_SAVED_PROJECT_LIST } from './mock-saved-project.data';

@Injectable()
export class StubSavedProjectService {

  constructor(private http: HttpClient) { }

  public getSavedProjectData(username: String): Observable<SavedProject[]> {
    return Observable.of([]);
  }

  public deleteSavedProjectData(deleteProject: SavedProject) {
    return null;
  }
}
