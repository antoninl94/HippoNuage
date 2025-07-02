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
import { FooterComponent } from './shared/footer/footer.component';
import { EmailVerificationPopupComponent } from './email-verification-popup/email-verification-popup.component';
import { UploadButtonComponent } from './components/upload-button/upload-button.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, HeroComponent, RegisterformComponent, LoginformComponent, RouterModule, HomeComponent, MailSendingComponent, FormsModule, FooterComponent, EmailVerificationPopupComponent, UploadButtonComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'front_end_service';
}
