import { Injectable } from '@angular/core';
import {HttpClient, HttpRequest} from '@angular/common/http';
import {ApiResponse, AuthResponse, LoginRequest, RegisterRequest} from '../models/auth';
import {Observable, tap} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  //Đường dẫn API của Spring boot
  private baseURL = 'http://localhost:8080/api/v1/auth';

  constructor(private http: HttpClient) {}

  //Gọi API Đăng ký
  register(request: RegisterRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.baseURL}/register`, request);
  }

  //Goi API Đăng nhập
  login(request: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseURL}/login`, request).pipe(
      tap(response => {
        if (response.code === 200 && response.result) {
          this.saveToken(response.result.accessToken, response.result.refreshToken);
        }
      })
    );
  }

  // Gọi API đổi Access Token mới bằng Refresh Token
  refreshToken(refreshToken: string): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseURL}/refresh`, {refreshToken});
  }

  // Gọi API thông báo logout lên Backend
  logoutServer(refreshToken: string): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseURL}/logout`, {refreshToken});
  }

  //Luu token vào localStorage
  saveToken(token: string, refreshToken: string): void {
    localStorage.setItem('authToken', token);
    localStorage.setItem('refreshToken', refreshToken);
  }

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  getReFreshToken(): string | null{
    return localStorage.getItem('refreshToken');
  }

  clearTokens(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('refreshToken');
  }
}
