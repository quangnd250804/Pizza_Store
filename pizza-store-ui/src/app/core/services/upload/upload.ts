import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from '../../models/auth';

@Injectable({
  providedIn: 'root',
})
export class UploadService {
  private baseURL = 'http://localhost:8080/api/v1/upload';

  constructor(private http: HttpClient) {}

  uploadImage(file: File): Observable<ApiResponse<string>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ApiResponse<string>>(`${this.baseURL}/image`, formData);
  }
}
