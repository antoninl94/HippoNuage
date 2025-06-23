import { Component, Output, EventEmitter } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-email-verification-popup',
  standalone:true,
  imports: [HttpClientModule, CommonModule],
  templateUrl: './email-verification-popup.component.html',
  styleUrl: './email-verification-popup.component.css'
})


export class EmailVerificationPopupComponent {
    
    @Output() closed = new EventEmitter<void>();
    @Output() resend = new EventEmitter<void>();
    emailSent = false;

     constructor(private http: HttpClient, private router: Router) {}

    close() {
    this.closed.emit();
  }

  onResend() {
   const token = localStorage.getItem('token');
   const headers = new HttpHeaders({
  'Authorization': `Bearer ${token}`
  });

    this.http.post('http://localhost:8080/user/resend-email', {}, { headers, responseType: 'text' })
      .subscribe({
        next: () => {
          this.emailSent = true;
          setTimeout (() => {
          this.resend.emit();
          this.close();
        }, 2000);
      },
        error: (err) => {
          console.error('Erreur lors du renvoi du mail', err);
          this.router.navigate(['/connexion']);
        }
      });
  }


}
