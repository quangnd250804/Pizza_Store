import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { OrderRequest, OrderResponse, PageResponse } from '../../models/order';
import { ApiResponse } from '../../models/auth';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private baseURL = 'http://localhost:8080/api/v1/orders';

  constructor(private http: HttpClient) {}

  createOrder(request: OrderRequest): Observable<ApiResponse<number>> {
    return this.http.post<ApiResponse<number>>(this.baseURL, request);
  }

  getOrderHistory(status?: string, page: number = 1, limit: number = 10): Observable<PageResponse<OrderResponse>> {
    let url = `${this.baseURL}/history?page=${page}&limit=${limit}`;
    if (status) {
      url += `&status=${status}`;
    }
    return this.http.get<ApiResponse<PageResponse<OrderResponse>>>(url).pipe(
      map(response => response.result)
    );
  }

  getOrderById(id: number): Observable<OrderResponse> {
    return this.http.get<ApiResponse<OrderResponse>>(`${this.baseURL}/${id}`).pipe(
      map(response => response.result)
    );
  }

  cancelOrder(id: number): Observable<string> {
    return this.http.put<ApiResponse<string>>(`${this.baseURL}/${id}/cancel`, {}).pipe(
      map(response => response.result)
    );
  }

  getAllOrdersAdmin(
    search?: string,
    status?: string,
    paymentMethod?: string,
    paymentStatus?: string,
    startDate?: string,
    endDate?: string,
    sortBy: string = 'createdAt',
    sortDirection: string = 'DESC',
    page: number = 1,
    limit: number = 10
  ): Observable<PageResponse<OrderResponse>> {
    let url = `${this.baseURL}/admin?page=${page}&limit=${limit}&sortBy=${sortBy}&sortDirection=${sortDirection}`;
    if (search) url += `&search=${encodeURIComponent(search)}`;
    if (status) url += `&status=${status}`;
    if (paymentMethod) url += `&paymentMethod=${paymentMethod}`;
    if (paymentStatus) url += `&paymentStatus=${paymentStatus}`;
    if (startDate) url += `&startDate=${startDate}`;
    if (endDate) url += `&endDate=${endDate}`;
    
    return this.http.get<ApiResponse<PageResponse<OrderResponse>>>(url).pipe(
      map(response => response.result)
    );
  }

  getOrderByIdAdmin(id: number): Observable<OrderResponse> {
    return this.http.get<ApiResponse<OrderResponse>>(`${this.baseURL}/admin/${id}`).pipe(
      map(response => response.result)
    );
  }

  updateOrderStatusAdmin(id: number, status: string): Observable<string> {
    return this.http.put<ApiResponse<string>>(`${this.baseURL}/admin/${id}/status?status=${status}`, {}).pipe(
      map(response => response.result)
    );
  }

  updatePaymentStatusAdmin(id: number, status: string): Observable<string> {
    return this.http.put<ApiResponse<string>>(`${this.baseURL}/admin/${id}/payment-status?status=${status}`, {}).pipe(
      map(response => response.result)
    );
  }
}
