import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';


export interface FileInfo {
  name: string;
  size: number;
  created_at: string;
}


@Injectable({
  providedIn: 'root'
})
export class FileService {
  private apiUrl = 'http://localhost:8082';

  constructor(private http: HttpClient) {}

  getUserFiles(): Observable<FileInfo[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    });

    return this.http.get<FileInfo[]>(`${this.apiUrl}/file/getFiles`, { 
      headers: headers,
      withCredentials: true
    });
  }

  uploadFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    });

    return this.http.post(`${this.apiUrl}/file/upload`, formData, {
      headers: headers,
      withCredentials: true
    });
  }
}
