import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterModule, Router } from '@angular/router';
import { OrderService } from '../../../core/services/order/order';
import { OrderResponse, OrderStatus, PageResponse } from '../../../core/models/order';
import { ToastService } from '../../../core/services/toast';
import { HeaderComponent } from '../../../shared/header/header';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './order-history.html',
  styleUrls: ['./order-history.css']
})
export class OrderHistoryComponent implements OnInit {
  orders: OrderResponse[] = [];
  pageResponse: PageResponse<OrderResponse> | null = null;
  currentPage: number = 1;
  pageSize: number = 10;
  selectedStatus: string = '';
  isLoading: boolean = false;

  statuses = [
    { value: '', label: 'Tất cả' },
    { value: OrderStatus.PENDING, label: 'Chờ duyệt' },
    { value: OrderStatus.CONFIRMED, label: 'Đã xác nhận' },
    { value: OrderStatus.PREPARING, label: 'Đang chuẩn bị' },
    { value: OrderStatus.READY, label: 'Đã sẵn sàng' },
    { value: OrderStatus.SHIPPING, label: 'Đang giao' },
    { value: OrderStatus.DELIVERED, label: 'Đã giao' },
    { value: OrderStatus.CANCELLED, label: 'Đã hủy' }
  ];

  constructor(
    private orderService: OrderService,
    private toastService: ToastService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.orderService.getOrderHistory(this.selectedStatus, this.currentPage, this.pageSize)
      .subscribe({
        next: (response: PageResponse<OrderResponse>) => {
          this.pageResponse = response;
          this.orders = response.content;
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err: HttpErrorResponse) => {
          console.error(err);
          this.toastService.showError('Không thể tải lịch sử đơn hàng');
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  onStatusChange(status: string): void {
    this.selectedStatus = status;
    this.currentPage = 1;
    this.loadOrders();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadOrders();
  }

  viewDetails(orderId: number): void {
    this.router.navigate(['/orders', orderId]);
  }

  canCancel(status: OrderStatus): boolean {
    return status === OrderStatus.PENDING || 
           status === OrderStatus.CONFIRMED || 
           status === OrderStatus.PREPARING;
  }

  cancelOrder(event: Event, orderId: number): void {
    event.stopPropagation(); // Prevent row click from navigating to details
    if (confirm('Bạn có chắc chắn muốn hủy đơn hàng này không?')) {
      this.orderService.cancelOrder(orderId).subscribe({
        next: () => {
          this.toastService.showSuccess('Hủy đơn hàng thành công');
          this.loadOrders();
        },
        error: (err: HttpErrorResponse) => {
          console.error(err);
          this.toastService.showError('Lỗi khi hủy đơn hàng');
          this.cdr.detectChanges();
        }
      });
    }
  }

  getStatusBadgeClass(status: OrderStatus): string {
    switch(status) {
      case OrderStatus.PENDING: return 'bg-yellow-500/20 text-yellow-500 border border-yellow-500/50';
      case OrderStatus.CONFIRMED: return 'bg-blue-500/20 text-blue-500 border border-blue-500/50';
      case OrderStatus.PREPARING: return 'bg-orange-500/20 text-orange-500 border border-orange-500/50';
      case OrderStatus.READY: return 'bg-teal-500/20 text-teal-500 border border-teal-500/50';
      case OrderStatus.SHIPPING: return 'bg-purple-500/20 text-purple-500 border border-purple-500/50';
      case OrderStatus.DELIVERED: return 'bg-green-500/20 text-green-500 border border-green-500/50';
      case OrderStatus.CANCELLED: return 'bg-red-500/20 text-red-500 border border-red-500/50';
      default: return 'bg-slate-500/20 text-slate-500 border border-slate-500/50';
    }
  }

  getStatusText(status: OrderStatus): string {
    const found = this.statuses.find(s => s.value === status);
    return found ? found.label : status;
  }

  getPaymentStatusText(status: string): string {
    return status === 'PAID' ? 'Đã thanh toán' : 'Chưa thanh toán';
  }
}
