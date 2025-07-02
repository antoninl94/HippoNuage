import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor() { }

  isLoggedIn(): boolean {
  const token = localStorage.getItem('token');
  if (!token) return false;

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const exp = payload.exp;
    const now = Math.floor(Date.now() / 1000);
    return exp && exp > now;
  } catch (e) {
    return false;
  }
}

  login(token:string):void {
    localStorage.setItem('token', token);
  }

  logout(): void {
    localStorage.removeItem('token');
  }

  }
