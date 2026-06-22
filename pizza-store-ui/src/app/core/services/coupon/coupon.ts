import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Coupon } from '../../models/coupon';

@Injectable({
  providedIn: 'root'
})
export class CouponService {
  private apiUrl = 'http://localhost:8080/api/v1/coupons';

  constructor(private http: HttpClient) { }

  getActiveCoupons(): Observable<Coupon[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => response.result)
    );
  }

  validateCoupon(code: string, total: number): Observable<Coupon> {
    return this.http.get<any>(`${this.apiUrl}/validate?code=${code}&total=${total}`).pipe(
      map(response => response.result)
    );
  }
}
