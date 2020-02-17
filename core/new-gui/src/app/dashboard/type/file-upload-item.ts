import {OnInit } from '@angular/core';
import { UserFileService } from '../service/user-file/user-file.service';

export class FileUploadItem {
  public description: string = '';

  private uploadProgress: number = 0;
  private isUploadingFlag: boolean = false;

  constructor(private file: File) {}

  public getFile(): File {
    return this.file;
  }

  public getName(): string {
    return this.file.name;
  }

  public getSize(): number {
    return this.file.size;
  }

  public getType(): string {
    return this.file.type;
  }

  public isUploading(): boolean {
    return this.isUploadingFlag;
  }

  public setUploading(flag: boolean): void {
    this.isUploadingFlag = flag;
  }

}
