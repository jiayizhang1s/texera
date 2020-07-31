import { TestBed, inject } from '@angular/core/testing';

import { SavedProjectService } from './saved-project.service';

import { HttpClient } from '@angular/common/http';

import { marbles} from 'rxjs-marbles';
import { Observable } from 'rxjs/Observable';
import { StubSavedProjectService } from './stub-saved-project.service';

class StubHttpClient {
  constructor() { }
}

describe('SavedProjectService', () => {

  let service: SavedProjectService;
  const USER = 'Tom';
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: SavedProjectService, useClass: StubSavedProjectService},
        { provide: HttpClient, useClass: StubHttpClient }
      ]
    });

    service = TestBed.get(SavedProjectService);
  });

  it('should be created', inject([SavedProjectService], (injectedService: SavedProjectService) => {
    expect(injectedService).toBeTruthy();
  }));


  it('should return the same observable of array as expected if getSavedProjectData is called ', () => {
    const saveDataObservable = service.getSavedProjectData(USER);

    // the current service test is in hard-coded style since there is no service with can give feedback

    saveDataObservable.subscribe(data => {
      expect(data).toEqual([]);
    });

  });
});
