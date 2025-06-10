import { Routes } from '@angular/router';
import { HeroComponent } from './shared/hero/hero.component';
import { LoginformComponent } from './loginform/loginform.component';
import { RegisterformComponent } from './registerform/registerform.component';
import { HomeComponent } from './home/home.component';

export const routes: Routes = [
    {path: '', component: HeroComponent},
    {path: 'connexion', component: LoginformComponent},
    {path: 'inscription', component: RegisterformComponent},
    {path: 'home', component: HomeComponent}
];
