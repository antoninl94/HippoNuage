import { Component } from '@angular/core';
import { FileService } from '../../services/file.service';
import { CommonModule } from '@angular/common';
import { SharedService } from '../../services/shared.service';

@Component({
  selector: 'app-upload-button',
  imports: [CommonModule],
  templateUrl: './upload-button.component.html',
  styleUrls: ['./upload-button.component.css']
})

export class UploadButtonComponent {
  selectedFiles: File[] | null = null;
  uploadStatus: string = '';
  isDraggingOver: boolean = false;
  isDropped: boolean = false;
  uploadSuccess: boolean = false;
  needFile: boolean = false;
  uploadFail: boolean = false;

  constructor(private fileService: FileService, private sharedService: SharedService) {}

  onFileSelected(event: any) {
    this.selectedFiles = Array.from(event.target.files);
    this.uploadStatus = '';
    this.isDropped = true;
  }

  upload() {
    if (!this.selectedFiles) {
      this.uploadStatus = "Veuillez sélectionner un fichier avant d'upload";
      this.needFile = true;
      this.resetMessageAfterDelay();
      return;
    }
    this.selectedFiles.forEach(file => {
      this.fileService.uploadFile(file).subscribe({
        next: () => {
          this.needFile = false;
          this.uploadStatus = 'Upload réussi';
          this.sharedService.triggerDAshboardRefresh();
          this.uploadSuccess = true;
          this.selectedFiles = null;
          this.resetMessageAfterDelay();
        },
        error: (err) => {
          console.error('Erreur lors de l\'upload', err);
          this.uploadStatus= 'Erreur lors de l\'upload';
          this.uploadFail = true;
          this.selectedFiles = null;
          this.resetMessageAfterDelay();
        }
      });
      this.isDropped = false;
    });
    }

    onDragOver(event: DragEvent) {
      console.log('Dragover déclenché');
      event.preventDefault();
      event.stopPropagation();
      this.isDraggingOver = true;
    }

    onDragLeave(event: DragEvent) {
      console.log('Drag leave déclenché');
      event.preventDefault();
      this.isDraggingOver = false;
    }

    onDrop(event: DragEvent) {
    console.log('Drop déclenché');
    event.preventDefault();
    event.stopPropagation();
    this.isDraggingOver = false;
    this.isDropped = true;

    if (event.dataTransfer && event.dataTransfer.files.length > 0) {
      this.selectedFiles = Array.from(event.dataTransfer.files);
      this.uploadStatus = '';
      event.dataTransfer.clearData();
    }
  }

resetMessageAfterDelay() {
  setTimeout(() => {
    this.uploadStatus = '';
    this.uploadSuccess = false;
    this.needFile = false;
    this.uploadFail = false;
  }, 5000);
}
}
