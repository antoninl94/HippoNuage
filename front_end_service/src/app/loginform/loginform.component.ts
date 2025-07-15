import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-loginform',
  standalone: true,
  imports: [FormsModule, HttpClientModule, CommonModule],
  templateUrl: './loginform.component.html',
  styleUrls: ['./loginform.component.css'] 
})
export class LoginformComponent {
  email: string = '';
  password: string = '';
  errorMessage: string | null = null;

  private apiUrl = `${environment.userServiceUrl}/user/login`;

  constructor(private http: HttpClient, private router: Router, private authService: AuthService) {}

  onSubmit() {
    console.log('Formulaire soumis avec:', this.email, this.password);
    this.http.post<{ message: string, token: string }>(this.apiUrl, { email: this.email, password: this.password })
      .subscribe({
        next: (response) => {
          console.log('Login réussi', response);
          this.authService.login(response.token);
          console.log('test 1')
          this.router.navigate(['/home']);
        },
        error: (error) => {
          console.error('Erreur login', error);
          this.errorMessage = "Email ou mot de passe incorrect. Veuillez réessayer."
          
        }
      });
  }
}
