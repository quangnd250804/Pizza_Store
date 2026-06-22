import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from '../../models/auth';
import {Combo, ComboRequest} from '../../models/combo';

@Injectable({
  providedIn: 'root',
})
export class ComboService {
  private baseURL = 'http://localhost:8080/api/v1/combos';
  constructor(private http: HttpClient) {}

  getActiveCombos(): Observable<ApiResponse<Combo[]>>{
    return this.http.get<ApiResponse<Combo[]>>(`${this.baseURL}/active-combos`);
  }

  getCombos():Observable<ApiResponse<Combo[]>>{
    return this.http.get<ApiResponse<Combo[]>>(`${this.baseURL}`);
  }

  createCombo(request: ComboRequest): Observable<ApiResponse<string>>{
    return this.http.post<ApiResponse<string>>(`${this.baseURL}`, request);
  }

  updateCombo(id: number, request: ComboRequest): Observable<ApiResponse<string>>{
    return this.http.put<ApiResponse<string>>(`${this.baseURL}/${id}`, request);
  }

  deleteCombo(id: number): Observable<ApiResponse<string>>{
    return this.http.delete<ApiResponse<string>>(`${this.baseURL}/${id}`);
  }
}
