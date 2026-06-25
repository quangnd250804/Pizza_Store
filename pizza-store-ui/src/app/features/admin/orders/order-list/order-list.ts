import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { OrderService } from '../../../../core/services/order/order';
import { OrderResponse } from '../../../../core/models/order';

@Component({
  selector: 'app-admin-order-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './order-list.html'
})
export class OrderListComponent implements OnInit {
  orders: OrderResponse[] = [];
  currentPage = 1;
  totalPages = 1;
  limit = 10;

  // Filters
  searchName: string = '';
  statusFilter: string = '';
  paymentMethodFilter: string = '';
  paymentStatusFilter: string = '';
  startDateFilter: string = '';
  endDateFilter: string = '';

  // Sorting
  sortBy: string = 'createdAt'; // 'createdAt' or 'totalPrice'
  sortDirection: string = 'DESC';

  // Status mapping for UI
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

  constructor(private orderService: OrderService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getAllOrdersAdmin(
      this.searchName,
      this.statusFilter,
      this.paymentMethodFilter,
      this.paymentStatusFilter,
      this.startDateFilter,
      this.endDateFilter,
      this.sortBy,
      this.sortDirection,
      this.currentPage,
      this.limit
    ).subscribe(res => {
      this.orders = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.currentPage;
      this.cdr.detectChanges();
    });
  }

  applyFilters(): void {
    this.currentPage = 1;
    this.cdr.detectChanges();
    this.loadOrders();
  }

  resetFilters(): void {
    this.searchName = '';
    this.statusFilter = '';
    this.paymentMethodFilter = '';
    this.paymentStatusFilter = '';
    this.startDateFilter = '';
    this.endDateFilter = '';
    this.sortBy = 'createdAt';
    this.sortDirection = 'DESC';
    this.currentPage = 1;
    this.cdr.detectChanges();
    this.loadOrders();
  }

  changeSort(column: string): void {
    if (this.sortBy === column) {
      this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    } else {
      this.sortBy = column;
      this.sortDirection = 'DESC';
    }
    this.cdr.detectChanges();
    this.loadOrders();
  }

  updateStatus(orderId: number, newStatus: string): void {
    if (confirm(`Bạn có chắc chắn muốn chuyển trạng thái sang ${this.statusMap[newStatus] || newStatus}?`)) {
      this.orderService.updateOrderStatusAdmin(orderId, newStatus).subscribe({
        next: (msg) => {
          alert('Cập nhật trạng thái thành công!');
          this.loadOrders();
        },
        error: (err) => {
          alert('Lỗi: ' + (err.error?.message || 'Không thể cập nhật trạng thái'));
          this.cdr.detectChanges();
        }
      });
    } else {
      // Revert the select element to its original value if user cancels
      this.loadOrders();
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.cdr.detectChanges();
      this.loadOrders();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.cdr.detectChanges();
      this.loadOrders();
    }
  }

  getEventValue(event: Event): string {
    return (event.target as HTMLSelectElement).value;
  }
}
