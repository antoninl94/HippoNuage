import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';


export interface FileInfo {
  name: string;
  size: number;
  uploadedAt: string;
}


@Injectable({
  providedIn: 'root'
})
export class FileService {
  private apiUrl = `${environment.fileAccessServiceUrl}`;

  constructor(private http: HttpClient) {}

  getUserFiles(): Observable<FileInfo[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    });

    return this.http.get<FileInfo[]>(`${this.apiUrl}/file_access/getFilesByUser`, { 
      headers: headers,
      withCredentials: true
    });
  }

  previewFile(fileName: string, preview: boolean = true): Observable<any>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    });

    return this.http.get(`${this.apiUrl}/file_access/getFile`, {
      headers: headers,
      withCredentials: true,
      responseType: 'blob',
      params: {
        filename: fileName,
        preview: preview.toString()
      }
    });
  }

  downloadFile(fileName: string, preview: boolean = false): Observable<any>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    });

    return this.http.get(`${this.apiUrl}/file_access/getFile`, {
      headers: headers,
      withCredentials: true,
      responseType: 'blob',
      params: {
        filename: fileName,
        preview: preview.toString()
      }
    });
  }

  uploadFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    });

    return this.http.post(`${environment.fileServiceUrl}/file/upload`, formData, {
      headers: headers,
      withCredentials: true
    });
  }

  supressFile(fileName: string): Observable<any>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    });
    return this.http.delete(`${this.apiUrl}/file_access/delete`, {
      headers: headers, 
      withCredentials: true,
      responseType: 'blob',
      params:{
        filename:fileName}
      });
    }
}
