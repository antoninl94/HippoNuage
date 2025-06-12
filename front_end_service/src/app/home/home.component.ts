import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-home',
  standalone:true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  openSidebar = true;
  activeSection = 'dashboard';
  windowWidth: number = window.innerWidth;
  showPassword: boolean = false;
  profile = {
    email: '',
    password: '',
  };

  constructor() {
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
    if (this.openSidebar) {
    this.openSidebar = false;
  }
  }

  onSubmitProfile() {
    if (!this.profile.password || !this.profile.email) {
      // Mettre la logique avec l'appel API backend
      return;
    }

    // Logique de sauvegarde, appel API, etc.
    console.log('Profile saved:', this.profile);
    alert('Profile saved successfully!');
  }
}
