import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbdModalFileAddComponent } from './ngbd-modal-file-add.component';

describe('NgbdModalFileAddComponent', () => {
  let component: NgbdModalFileAddComponent;
  let fixture: ComponentFixture<NgbdModalFileAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NgbdModalFileAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NgbdModalFileAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
