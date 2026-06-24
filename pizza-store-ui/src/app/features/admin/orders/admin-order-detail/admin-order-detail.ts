import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { OrderService } from '../../../../core/services/order/order';
import { OrderResponse } from '../../../../core/models/order';

@Component({
  selector: 'app-admin-order-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-order-detail.html'
})
export class AdminOrderDetailComponent implements OnInit {
  order: OrderResponse | null = null;
  loading = true;
  error = '';

  statusMap: Record<string, string> = {
    'PENDING': 'Chờ duyệt',
    'CONFIRMED': 'Đã xác nhận',
    'PREPARING': 'Đang chuẩn bị',
    'SHIPPING': 'Đang giao',
    'DELIVERED': 'Đã giao',
    'CANCELLED': 'Đã hủy'
  };

  statusColors: Record<string, string> = {
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'CONFIRMED': 'bg-blue-100 text-blue-800',
    'PREPARING': 'bg-purple-100 text-purple-800',
    'SHIPPING': 'bg-indigo-100 text-indigo-800',
    'DELIVERED': 'bg-green-100 text-green-800',
    'CANCELLED': 'bg-red-100 text-red-800'
  };

  paymentMethodMap: Record<string, string> = {
    'COD': 'Tiền mặt',
    'VNPAY': 'VNPay'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadOrder(Number(id));
    } else {
      this.error = 'Không tìm thấy mã đơn hàng';
      this.loading = false;
    }
  }

  loadOrder(id: number): void {
    this.orderService.getOrderByIdAdmin(id).subscribe({
      next: (res) => {
        this.order = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Không thể tải thông tin đơn hàng';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
