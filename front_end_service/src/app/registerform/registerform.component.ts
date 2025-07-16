import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { environment } from '../environments/environment';
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

  private apiUrl = `${environment.userServiceUrl}/user/register`;

  constructor(private http: HttpClient, private router: Router, private authService: AuthService) {}

  onSubmit() {
    console.log('Formulaire soumis avec:', this.email, this.password);
    this.http.post<{ message: string, token: string }>(this.apiUrl, { email: this.email, password: this.password })
      .subscribe({
        next: (response) => {
          console.log('Chevalier adoubé', response);
          this.authService.login(response.token);
          this.router.navigate(['/home']);
        },
        error: (error) => {
          console.error('Erreur login', error);

        }
      });
    }
  }
