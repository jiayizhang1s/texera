import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbdModalFileAddComponent } from './ngbd-modal-file-add.component';
import { UserAccountService } from '../../../../service/user-account/user-account.service';
import { UserFileUploadService } from '../../../../service/user-file/user-file-upload.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

describe('NgbdModalFileAddComponent', () => {
  let component: NgbdModalFileAddComponent;
  let fixture: ComponentFixture<NgbdModalFileAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NgbdModalFileAddComponent ],
      providers: [
        UserAccountService,
        UserFileUploadService,
        NgbActiveModal
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NgbdModalFileAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
