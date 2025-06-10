import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-loginform',
  standalone: true,
  imports: [FormsModule, HttpClientModule],  // <-- ajoute HttpClientModule ici
  templateUrl: './loginform.component.html',
  styleUrls: ['./loginform.component.css']  // attention à 'styleUrls' (au pluriel)
})
export class LoginformComponent {
  email: string = '';
  password: string = '';

  private apiUrl = 'http://localhost:8080/user/login';

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    console.log('Formulaire soumis avec:', this.email, this.password);
    this.http.post<{ message: string, token: string }>(this.apiUrl, { email: this.email, password: this.password })
      .subscribe({
        next: (response) => {
          console.log('Login réussi', response);
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
