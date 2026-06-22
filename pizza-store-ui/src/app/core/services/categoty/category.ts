import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from '../../models/auth';
import {Category, CategoryRequest} from '../../models/category';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private baseURL = 'http://localhost:8080/api/v1/categories';

  constructor(private http: HttpClient) {}

  getActiveCategories():Observable<ApiResponse<Category[]>> {
    return this.http.get<ApiResponse<Category[]>>(`${this.baseURL}/active-categories`);
  }

  getAllCategories():Observable<ApiResponse<Category[]>> {
    return this.http.get<ApiResponse<Category[]>>(`${this.baseURL}`);
  }

  createCategory(request: CategoryRequest): Observable<ApiResponse<String>> {
    return this.http.post<ApiResponse<String>>(`${this.baseURL}`, request);
  }

  updateCategory(id: number, request: CategoryRequest): Observable<ApiResponse<String>> {
    return this.http.put<ApiResponse<String>>(`${this.baseURL}/${id}`, request);
  }

  deleteCategory(id: number): Observable<ApiResponse<String>> {
    return this.http.delete<ApiResponse<String>>(`${this.baseURL}/${id}`);
  }
}
