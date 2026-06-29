import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse, UserCreateRequest } from '../../models/user';
import { Role } from '../../models/role';
import { ApiResponse, PageResponse } from '../../models/common';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/v1/users';

  constructor(private http: HttpClient) {}

  getUsers(page: number = 1, limit: number = 10): Observable<ApiResponse<PageResponse<UserResponse>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());
    return this.http.get<ApiResponse<PageResponse<UserResponse>>>(this.apiUrl, { params });
  }

  createStaff(request: UserCreateRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/staff`, request);
  }

  updateUserStatus(id: number, isActive: boolean): Observable<ApiResponse<string>> {
    return this.http.put<ApiResponse<string>>(`${this.apiUrl}/${id}/status`, { active: isActive });
  }

  updateUserRoles(id: number, roleIds: number[]): Observable<ApiResponse<string>> {
    return this.http.put<ApiResponse<string>>(`${this.apiUrl}/${id}/roles`, { roleIds });
  }

  getAllRoles(): Observable<ApiResponse<Role[]>> {
    return this.http.get<ApiResponse<Role[]>>(`${this.apiUrl}/roles`);
  }
}
