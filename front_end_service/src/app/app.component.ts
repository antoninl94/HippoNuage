import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { HeaderComponent } from './shared/header/header.component';
import { HeroComponent } from './shared/hero/hero.component';
import { RegisterformComponent } from './registerform/registerform.component';
import { LoginformComponent } from './loginform/loginform.component';
import { RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { MailSendingComponent } from './mail-sending/mail-sending.component';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, HeroComponent, RegisterformComponent, LoginformComponent, RouterModule, HomeComponent, MailSendingComponent, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'front_end_service';
}
