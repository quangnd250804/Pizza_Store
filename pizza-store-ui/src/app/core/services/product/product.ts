import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse, PageResponse} from '../../models/auth';
import {Product, ProductRequest} from '../../models/product';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private baseURL = 'http://localhost:8080/api/v1/products';
  constructor(private http: HttpClient) {}

  getMenu(categoryCode?: string, page: number = 1, limit: number = 8):Observable<ApiResponse<PageResponse<Product>>>{
    let params = new HttpParams().set('page', page).set('limit', limit);
    if(categoryCode){
      params = params.set('category', categoryCode);
    }
    return this.http.get<ApiResponse<PageResponse<Product>>>(`${this.baseURL}/menu`, {params});
  }

  getAllProducts(categoryCode?: string, page: number = 1, limit: number = 8): Observable<ApiResponse<PageResponse<Product>>>{
    let params = new HttpParams().set('page', page).set('limit', limit);
    if(categoryCode){
      params = params.set('category', categoryCode);
    }
    return this.http.get<ApiResponse<PageResponse<Product>>>(`${this.baseURL}`, {params});
  }

  addProduct(request: ProductRequest): Observable<ApiResponse<string>>{
    return this.http.post<ApiResponse<string>>(`${this.baseURL}`, request);
  }

  updateProduct(id: number, request: ProductRequest): Observable<ApiResponse<string>>{
    return this.http.put<ApiResponse<string>>(`${this.baseURL}/${id}`, request);
  }

  deleteProduct(id: number): Observable<ApiResponse<string>>{
    return this.http.delete<ApiResponse<string>>(`${this.baseURL}/${id}`);
  }
}
