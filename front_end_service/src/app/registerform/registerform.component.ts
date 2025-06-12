import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
@Component({
  selector: 'app-registerform',
  standalone:true,
  imports: [FormsModule, HttpClientModule],
  templateUrl: './registerform.component.html',
  styleUrl: './registerform.component.css'
})
export class RegisterformComponent {
  email: string = '';
  password: string = '';

  private apiUrl = 'http://localhost:8080/user/register';

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    console.log('Formulaire soumis avec:', this.email, this.password);
    this.http.post<{ message: string, token: string }>(this.apiUrl, { email: this.email, password: this.password })
      .subscribe({
        next: (response) => {
          console.log('Chevalier adoubé', response);
          localStorage.setItem('jwtToken', response.token);
          this.router.navigate(['/home']);
        },
        error: (error) => {
          console.error('Erreur login', error);
          // Afficher message d’erreur utilisateur
        }
      });
    }
  }
