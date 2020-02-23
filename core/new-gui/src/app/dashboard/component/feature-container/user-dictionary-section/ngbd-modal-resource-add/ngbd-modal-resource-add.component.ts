import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { UserDictionaryUploadService } from '../../../../service/user-dictionary/user-dictionary-upload.service';

import { FileUploader} from 'ng2-file-upload';
import { MatTabChangeEvent } from '@angular/material';

import { ErrorStateMatcher } from '@angular/material';
import { FormControl, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { DictionaryUploadItem } from '../../../../type/user-dictionary';


class DictionaryErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}


/**
 * NgbdModalResourceAddComponent is the pop-up component to let
 * user upload dictionary. User can either input the dictionary
 * name and items or drag and drop the dictionary file from
 * local computer.
 *
 * @author Zhaomin Li
 * @author Adam
 *
 */
@Component({
  selector: 'texera-resource-section-add-dict-modal',
  templateUrl: 'ngbd-modal-resource-add.component.html',
  styleUrls: ['./ngbd-modal-resource-add.component.scss', '../../../dashboard.component.scss'],
})
export class NgbdModalResourceAddComponent {

  // This checks whether the user has hover a file over the file upload area
  public haveDropZoneOver: boolean = false;

  // uploader is a data type introduced in ng2-uploader library, which can be used to capture files and store them
  //  inside the uploader queue.
  public uploader: FileUploader = new FileUploader({url: ''});

  public isInUploadFileTab: boolean = true;

  // These are used to create custom form control validators.
  public matcher = new DictionaryErrorStateMatcher();
  public nameValidator: FormControl =  new FormControl('', [Validators.required]);
  public contentValidator: FormControl =  new FormControl('', [Validators.required]);
  public descriptionValidator: FormControl =  new FormControl('', [Validators.required]);

  constructor(
    public activeModal: NgbActiveModal,
    public userDictionaryUploadService: UserDictionaryUploadService
  ) { }

  /**
   * Handles the tab change event in the modal popup
   *
   * @param event
   */
  public onTabChangeEvent(event: MatTabChangeEvent) {
    this.isInUploadFileTab = (event.tab.textLabel === 'Upload');
  }

  public getDictionaryArray(): DictionaryUploadItem[] {
    return this.userDictionaryUploadService.getDictionaryArray();
  }

  public getDictionaryArrayLength(): number {
    return this.userDictionaryUploadService.getDictionaryArrayLength();
  }

  public deleteDictionary(dictionaryUploadItem: DictionaryUploadItem): void {
    this.userDictionaryUploadService.deleteDictionary(dictionaryUploadItem);
  }

  /**
   * This method will check if the current form is valid to submit to
   *  the backend. This will be used to disable the submit button
   *  on the upload dictionary panel.
   *
   */
  public isManualDictionaryValid(): boolean {
    return this.userDictionaryUploadService.isManualDictionaryValid();
  }

  public isItemValid(dictionaryUploadItem: DictionaryUploadItem): boolean {
    return this.userDictionaryUploadService.isItemValid(dictionaryUploadItem);
  }

  public isUploadEnable(): boolean {
    return this.userDictionaryUploadService.isUploadEnable();
  }

  public handleClickUploadFile(clickUploadEvent: {target: HTMLInputElement}): void {
    const fileList: FileList | null = clickUploadEvent.target.files;
    if (fileList === null) {
      throw new Error(`browser upload does not work as intended`);
    }

    for (let i = 0; i < fileList.length; i++) {
      this.userDictionaryUploadService.insertNewDictionary(fileList[i]);
    }
  }

  public clickUploadDictionaryButton(): void {
    this.userDictionaryUploadService.uploadAllDictionary();
  }

  public clickUploadManualDictioanryButton(): void {
    this.userDictionaryUploadService.uploadManualDictionary();
  }

  /**
   * When user drag file over the area, this function will be called with a boolean variable
   *  indicating whether there is a file over the uploader view.
   *
   * @param fileOverEvent
   */
  public haveFileOver(fileOverEvent: boolean): void {
    this.haveDropZoneOver = fileOverEvent;
  }

  /**
   * This method will detect the file drop events happening in the uploader. It will call
   *  `checkCurrentFilesValid()` to check if the files uploaded to the UI are valid.
   *
   * @param fileDropEvent
   */
  public getFileDropped(fileDropEvent: FileList): void {
    for (let i = 0; i < fileDropEvent.length; i++) {
      const fileOrNull: File | null = fileDropEvent.item(i);
      if (this.isFile(fileOrNull) ) {
        this.userDictionaryUploadService.insertNewDictionary(fileOrNull);
      }
    }

    this.uploader.clearQueue();
  }

  private isFile(file: File | null): file is File {
    return file != null;
  }
}
