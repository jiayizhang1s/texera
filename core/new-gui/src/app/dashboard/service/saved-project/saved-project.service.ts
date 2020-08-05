import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppSettings } from '../../../common/app-setting';
import { Observable } from 'rxjs/Observable';
import { SavedProject } from '../../type/saved-project';
import { UserService } from '../../../common/service/user/user.service';
/**
 * Saved Project service should be able to get all the
 * saved-project data from the back end for a specific user.
 * Users can also add a new project or delete an existing project
 * by calling methods in service.
 * Currently using a StubSavedProjectService to upload the mock
 * data to the dashboard.
 *
 * @author Zhaomin Li
 */
@Injectable()
export class SavedProjectService {

  public static WORKFLOWLIST_ENDPOINT = 'workflow/workflow-list';
  public static ADD_NEW_WORKFLOW_ENDPOINT = 'workflow/add';
  constructor(private http: HttpClient,
    private userService: UserService) { }

  public addNewProject(workflowID: string): Observable<Number> {
    const user = this.userService.getUser();
    if (user) {
      const body = {username: user.userName,
                    workflowID: workflowID,
                    workflowBody: JSON.stringify({
                      operators: [],
                      operatorPositions: {},
                      links: []
                    })};
      return this.http.post<Number>(
        `${AppSettings.getApiEndpoint()}/${SavedProjectService.ADD_NEW_WORKFLOW_ENDPOINT}`,
          JSON.stringify(body),
          { headers: {'Content-Type' : 'application/json'}});
    }

    return Observable.of(-1);
  }
  public getSavedProjectData(): Observable<SavedProject[]> {
      const user = this.userService.getUser();
      if (user) {
        const body = {username: user.userName};
        return this.http.post<SavedProject[]>(
        `${AppSettings.getApiEndpoint()}/${SavedProjectService.WORKFLOWLIST_ENDPOINT}`,
          JSON.stringify(body),
          { headers: {'Content-Type' : 'application/json'}});
      }
      return Observable.of([]);
  }

  public deleteSavedProjectData(deleteProject: SavedProject) {
    return null;

  }
}
