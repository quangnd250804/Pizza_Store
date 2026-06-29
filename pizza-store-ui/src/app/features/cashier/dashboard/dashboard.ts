import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { OrderService } from '../../../core/services/order/order';
import { OrderResponse, PageResponse } from '../../../core/models/order';

@Component({
  selector: 'app-cashier-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html'
})
export class CashierDashboardComponent implements OnInit, OnDestroy {
  pendingOrders: OrderResponse[] = [];
  codOrders: OrderResponse[] = [];
  
  loadingPending = false;
  loadingCod = false;
  
  // Detail Modal
  showDetailModal = false;
  loadingDetail = false;
  selectedOrder: OrderResponse | null = null;
  
  refreshInterval: ReturnType<typeof setInterval> | undefined;

  constructor(private orderService: OrderService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.fetchData();
    // Auto refresh every 15 seconds
    this.refreshInterval = setInterval(() => {
      this.fetchData();
    }, 15000);
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  fetchData(): void {
    this.loadPendingOrders();
    this.loadCodOrders();
  }

  loadPendingOrders(): void {
    this.loadingPending = true;
    this.cdr.detectChanges();
    this.orderService.getAllOrdersAdmin(undefined, 'PENDING', undefined, undefined, undefined, undefined, 'createdAt', 'ASC', 1, 50)
      .subscribe({
        next: (res: PageResponse<OrderResponse>) => {
          this.pendingOrders = res.content;
          this.loadingPending = false;
          this.cdr.detectChanges();
        },
        error: (err: HttpErrorResponse) => {
          console.error('Error loading pending orders', err);
          this.loadingPending = false;
          this.cdr.detectChanges();
        }
      });
  }

  loadCodOrders(): void {
    this.loadingCod = true;
    this.cdr.detectChanges();
    this.orderService.getAllOrdersAdmin(undefined, 'DELIVERED', 'COD', 'UNPAID', undefined, undefined, 'createdAt', 'ASC', 1, 50)
      .subscribe({
        next: (res: PageResponse<OrderResponse>) => {
          this.codOrders = res.content;
          this.loadingCod = false;
          this.cdr.detectChanges();
        },
        error: (err: HttpErrorResponse) => {
          console.error('Error loading COD orders', err);
          this.loadingCod = false;
          this.cdr.detectChanges();
        }
      });
  }

  confirmOrder(orderId: number): void {
    if (confirm('Xác nhận đưa đơn hàng này vào bếp?')) {
      this.orderService.updateOrderStatusAdmin(orderId, 'CONFIRMED').subscribe({
        next: () => {
          this.loadPendingOrders();
        },
        error: (err: HttpErrorResponse) => {
          alert('Lỗi: ' + (err.error?.message || err.message));
        }
      });
    }
  }

  cancelOrder(orderId: number): void {
    if (confirm('Bạn có chắc chắn muốn hủy đơn hàng này không?')) {
      this.orderService.updateOrderStatusAdmin(orderId, 'CANCELLED').subscribe({
        next: () => {
          this.loadPendingOrders();
        },
        error: (err: HttpErrorResponse) => {
          alert('Lỗi: ' + (err.error?.message || err.message));
        }
      });
    }
  }

  confirmPayment(orderId: number): void {
    if (confirm('Xác nhận đã nhận đủ tiền COD cho đơn hàng này?')) {
      this.orderService.updatePaymentStatusAdmin(orderId, 'PAID').subscribe({
        next: () => {
          this.loadCodOrders();
        },
        error: (err: HttpErrorResponse) => {
          alert('Lỗi: ' + (err.error?.message || err.message));
        }
      });
    }
  }

  viewDetail(orderId: number): void {
    this.showDetailModal = true;
    this.loadingDetail = true;
    this.cdr.detectChanges();
    this.orderService.getOrderByIdAdmin(orderId).subscribe({
      next: (res: OrderResponse) => {
        this.selectedOrder = res;
        this.loadingDetail = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        console.error('Lỗi tải chi tiết đơn hàng', err);
        this.loadingDetail = false;
        this.showDetailModal = false;
        this.cdr.detectChanges();
        alert('Không thể tải chi tiết đơn hàng!');
      }
    });
  }

  closeDetailModal(): void {
    this.showDetailModal = false;
    this.selectedOrder = null;
    this.cdr.detectChanges();
  }
}
