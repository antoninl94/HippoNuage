import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router} from '@angular/router';
import { EmailVerificationPopupComponent } from '../email-verification-popup/email-verification-popup.component';
import { FileService } from '../services/file.service';
import { FileInfo } from '../services/file.service';
import { UploadButtonComponent } from '../components/upload-button/upload-button.component';
import { Subscription } from 'rxjs';
import { SharedService } from '../services/shared.service';


@Component({
  selector: 'app-home',
  standalone:true,
  imports: [CommonModule, FormsModule, HttpClientModule, EmailVerificationPopupComponent, UploadButtonComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [HttpClient]
})


export class HomeComponent implements OnInit, OnDestroy {
  showEmailPopup = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  openSidebar = window.innerWidth >= 768;
  activeSection = 'dashboard';
  windowWidth: number = window.innerWidth;
  showPassword: boolean = false;
  profile = {
    email: '',
    password: '',
  };

  files: FileInfo[] = [];
  private refreshSub!: Subscription;

  constructor(private http: HttpClient, private router: Router, private fileService: FileService, private sharedService: SharedService) {
    window.addEventListener('resize', () => {
      this.windowWidth = window.innerWidth;
    });
  }

  toggleSidebar() {
    this.openSidebar = !this.openSidebar;
  }

  setSection(section: string, event: Event) {
    this.activeSection = section;
    event.preventDefault();
    if (innerWidth < 768) {
      this.openSidebar = false;
    }
  }

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        if (!payload.ValidatedEmail) {
          this.showEmailPopup = true;
        }
        this.fileService.getUserFiles().subscribe({
          next: (data) => (this.files = data),
          error: (err) => console.error('Erreur lors de la récupération des fichiers', err)
        });
        this.refreshSub = this.sharedService.refreshDashboard$.subscribe(() => {
          this.loadDashboard();
        })
      } catch (e) {
        console.error('Erreur de décodage du token', e);
      }
    }
  }
  
  onPopupClosed(): void {
    this.showEmailPopup = false;
  }

  onSubmitProfile() {
    if (!this.profile.password || !this.profile.email) {
      return;
    }
    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };
    const payload = {
    newEmail: this.profile.email,
    newPassword: this.profile.password,
    };
    this.http.put<{ message: string; token?: string }>('http://localhost:8080/user/update', payload, {headers})
      .subscribe({
        next: (response) => {
          console.log('Profil mis à jour :', response.message);
          if (response.token) {
            localStorage.setItem('token', response.token);
            this.successMessage = "Profil mis à jour avec succès";
          }
        },
        error: (err) => {
          console.error('Erreur lors de la mise à jour du profil :', err);
          this.errorMessage = "Echec de la mise à jour des informations";
          this.router.navigate(['/home']);
        },
    });
  }

  onResendEmail() {
    console.log("Email de confirmation renvoyé");
  }

  previewFile(fileName: string): void {
    this.fileService.previewFile(fileName).subscribe({
      next: (response: Blob) => {
        console.log('Type MIME reçu : ', response.type);
        const mimeType = response.type || 'application/octet-stream';
        const blob = new Blob([response], { type: mimeType });
        const url = window.URL.createObjectURL(blob);
        window.open(url);
      },
      error: (err) => {
        console.error("Erreur lors de la prévisualisation du fichier :", err);
      }
    });
  }

  downloadFile(fileName: string): void {
    this.fileService.previewFile(fileName).subscribe({
      next: (response: Blob) => {
      const url = window.URL.createObjectURL(response);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      a.click();
      window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error("Erreur lors de la prévisualisation du fichier :", err);
      }
    });
  }

  ngOnDestroy(): void {
    this.refreshSub.unsubscribe();
  }

  loadDashboard(): void {
    this.fileService.getUserFiles().subscribe({
      next: (data) => (this.files = data),
      error: (err) => console.error('Erreur lors de la récupération des fichiers', err)
    });
  }
}

