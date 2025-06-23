import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router} from '@angular/router';
import { EmailVerificationPopupComponent } from '../email-verification-popup/email-verification-popup.component';
@Component({
  selector: 'app-home',
  standalone:true,
  imports: [CommonModule, FormsModule, HttpClientModule, EmailVerificationPopupComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})

export class HomeComponent {
  showEmailPopup = false;
  errorMessage: string | null = null;
  openSidebar = window.innerWidth >= 768;
  activeSection = 'dashboard';
  windowWidth: number = window.innerWidth;
  showPassword: boolean = false;
  profile = {
    email: '',
    password: '',
  };

  constructor(private http: HttpClient, private router: Router) {
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
        if (!payload.validatedEmail) {
          this.showEmailPopup = true;
        }
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
    const payload = {
    email: this.profile.email,
    password: this.profile.password,
    };
    this.http.post<{ message: string; token?: string }>('http://localhost:8080/user/update', payload)
      .subscribe({
        next: (response) => {
          console.log('Profil mis à jour :', response.message);
          if (response.token) {
            localStorage.setItem('token', response.token);
          }
        },
        error: (err) => {
          console.error('Erreur lors de la mise à jour du profil :', err);
          this.router.navigate(['/home']);
        },
    });
  }

  onResendEmail() {
    console.log("Email de confirmation renvoyé");
  }
}
