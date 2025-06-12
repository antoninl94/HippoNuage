import { Component, Output, EventEmitter } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';


@Component({
  selector: 'app-email-verification-popup',
  standalone:true,
  imports: [HttpClientModule],
  templateUrl: './email-verification-popup.component.html',
  styleUrl: './email-verification-popup.component.css'
})


export class EmailVerificationPopupComponent {
    token = localStorage.getItem('jwtToken');
    headers = new HttpHeaders({
  ' Authorization': `Bearer ${this.token}`
  });

    @Output() closed = new EventEmitter<void>();
    @Output() resend = new EventEmitter<void>();

     constructor(private http: HttpClient, private router: Router) {}

    close() {
    this.closed.emit();
  }

  onResend() {
    this.http.post('http://localhost:8080/user/resend-email', {}, { headers: this.headers })
      .subscribe({
        next: () => {
          console.log('Email de confirmation renvoyé');
          this.resend.emit(); // Tu peux aussi émettre ici si tu veux notifier le parent
        },
        error: () => {
          console.error('Erreur lors du renvoi du mail');
        }
      });
  }


}
