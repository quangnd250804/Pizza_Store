import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../core/services/order/order';
import { OrderResponse, OrderStatus } from '../../../core/models/order';
import { ToastService } from '../../../core/services/toast';
import { HeaderComponent } from '../../../shared/header/header';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './order-detail.html',
  styleUrls: ['./order-detail.css']
})
export class OrderDetailComponent implements OnInit {
  order: OrderResponse | null = null;
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.loadOrderDetail(Number(idParam));
    } else {
      this.router.navigate(['/orders']);
    }
  }

  loadOrderDetail(id: number): void {
    this.isLoading = true;
    this.orderService.getOrderById(id).subscribe({
      next: (res) => {
        this.order = res;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.toastService.showError('Không tìm thấy đơn hàng');
        this.router.navigate(['/orders']);
      }
    });
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
    const statuses: Record<OrderStatus, string> = {
      [OrderStatus.PENDING]: 'Chờ duyệt',
      [OrderStatus.CONFIRMED]: 'Đã xác nhận',
      [OrderStatus.PREPARING]: 'Đang chuẩn bị',
      [OrderStatus.READY]: 'Đã sẵn sàng',
      [OrderStatus.SHIPPING]: 'Đang giao',
      [OrderStatus.DELIVERED]: 'Đã giao',
      [OrderStatus.CANCELLED]: 'Đã hủy'
    };
    return statuses[status] || status;
  }

  getPaymentStatusText(status: string): string {
    return status === 'PAID' ? 'Đã thanh toán' : 'Chưa thanh toán';
  }
}
