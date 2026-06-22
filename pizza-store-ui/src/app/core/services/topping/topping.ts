import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from '../../models/auth';
import {Topping, ToppingRequest} from '../../models/topping';

@Injectable({
  providedIn: 'root',
})
export class ToppingService {
  private baseURL = 'http://localhost:8080/api/v1/toppings';
  constructor(private http: HttpClient) {}

  getActiveToppings(): Observable<ApiResponse<Topping[]>>{
    return this.http.get<ApiResponse<Topping[]>>(`${this.baseURL}/active-toppings`);
  }

  getToppings(): Observable<ApiResponse<Topping[]>>{
    return this.http.get<ApiResponse<Topping[]>>(`${this.baseURL}`);
  }

  createTopping(request: ToppingRequest): Observable<ApiResponse<string>>{
    return this.http.post<ApiResponse<string>>(`${this.baseURL}`, request);
  }

  updateTopping(id: number, request: ToppingRequest): Observable<ApiResponse<string>>{
    return this.http.put<ApiResponse<string>>(`${this.baseURL}/${id}`, request);
  }

  deleteTopping(id: number): Observable<ApiResponse<string>>{
    return this.http.delete<ApiResponse<string>>(`${this.baseURL}/${id}`);
  }
}
