import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  isMenuOpen: boolean = false;
  isLoggedIn = false;

constructor (private router: Router) {}

toggleMenu() {
  this.isMenuOpen = !this.isMenuOpen;
}
ngOnInit() {
  const token = localStorage.getItem('token');
  this.isLoggedIn = !!token;
}
  
logout(): void {
  localStorage.removeItem('token');
  this.isLoggedIn = false;
  this.isMenuOpen = false;
  this.router.navigate(['/']);
}
}
