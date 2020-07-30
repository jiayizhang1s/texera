import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppSettings } from '../../../common/app-setting';
import { Observable } from 'rxjs/Observable';
import { SavedProject, SavedProjectList } from '../../type/saved-project';

import { MOCK_SAVED_PROJECT_LIST } from './mock-saved-project.data';

@Injectable()
export class StubSavedProjectService {

  constructor(private http: HttpClient) { }

  public getSavedProjectData(username: String): Observable<SavedProjectList> {
    const body = {username: username};
    return this.http.post<SavedProjectList>(
      `${AppSettings.getApiEndpoint()}/workflow/workflow-list`,
        JSON.stringify(body),
        { headers: {'Content-Type' : 'application/json'}});
  }

  public deleteSavedProjectData(deleteProject: SavedProject) {
    return null;
  }
}
