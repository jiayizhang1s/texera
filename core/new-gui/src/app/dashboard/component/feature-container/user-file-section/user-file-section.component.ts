import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgbdModalFileAddComponent } from './ngbd-modal-file-add/ngbd-modal-file-add.component';
import { UserFileService } from 'src/app/dashboard/service/user-file/user-file.service';
import { UserFile } from 'src/app/dashboard/type/user-file';

@Component({
  selector: 'texera-user-file-section',
  templateUrl: './user-file-section.component.html',
  styleUrls: ['./user-file-section.component.scss']
})
export class UserFileSectionComponent implements OnInit {

  constructor(
    private modalService: NgbModal,
    private userFileService: UserFileService
    ) { }

  ngOnInit() {
  }

  public openFileAddComponent() {
    const modalRef = this.modalService.open(NgbdModalFileAddComponent);
  }

  public getFileField<Field extends keyof UserFile>(index: number, field: Field): UserFile[Field] {
    return this.userFileService.getFileField(index, field);
  }

  public getFileName(index: number): string {
    return this.getFileField(index, 'name');
  }

  public getFileArrayLength(): number {
    return this.userFileService.getFileArrayLength();
  }

  public deleteFile(index: number): void {
    this.userFileService.deleteFile(this.userFileService.getFile(index));
  }

}
