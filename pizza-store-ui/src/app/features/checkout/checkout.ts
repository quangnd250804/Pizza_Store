import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CartService, CartItem } from '../../core/services/cart/cart';
import { OrderService } from '../../core/services/order/order';
import { OrderRequest, OrderDetailRequest } from '../../core/models/order';
import { Auth } from '../../core/services/auth';
import { ToastService } from '../../core/services/toast';
import { HeaderComponent } from '../../shared/header/header';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HeaderComponent],
  templateUrl: './checkout.html',
  styleUrls: ['./checkout.css']
})
export class CheckoutComponent implements OnInit {
  cartItems: CartItem[] = [];
  subTotal: number = 0;
  discountAmount: number = 0;
  finalTotal: number = 0;
  appliedCouponCode: string | undefined;
  
  // Form fields
  customerName: string = '';
  customerPhone: string = '';
  shippingAddress: string = '';
  note: string = '';
  paymentMethod: string = 'COD';

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private auth: Auth,
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit() {
    if (!this.auth.getToken()) {
      this.toast.showError('Vui lòng đăng nhập để đặt hàng!');
      this.router.navigate(['/login']);
      return;
    }

    this.cartService.cartItems$.subscribe(items => {
      this.cartItems = items;
      this.subTotal = this.cartService.getCartTotal();
      this.discountAmount = this.cartService.getDiscountAmount();
      this.finalTotal = this.cartService.getFinalTotal();
      
      if (this.cartItems.length === 0) {
        this.router.navigate(['/']);
      }
    });

    this.cartService.appliedCoupon$.subscribe(coupon => {
      this.appliedCouponCode = coupon?.code;
      this.discountAmount = this.cartService.getDiscountAmount();
      this.finalTotal = this.cartService.getFinalTotal();
    });
  }

  getUserIdFromToken(): number | undefined {
    const token = this.auth.getToken();
    if (!token) return undefined;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.userId || payload.id;
    } catch (e) {
      return undefined;
    }
  }

  placeOrder() {
    if (!this.customerName || !this.customerPhone || !this.shippingAddress) {
      this.toast.showError('Vui lòng điền đầy đủ thông tin giao hàng!');
      return;
    }

    const orderDetails: OrderDetailRequest[] = this.cartItems.map(item => {
      if (item.product) {
        return {
          productId: item.product.id,
          sizeId: item.sizeId,
          quantity: item.quantity,
          toppingIds: item.toppings ? item.toppings.map(t => t.id) : []
        };
      } else {
        return {
          comboId: item.combo!.id,
          quantity: item.quantity
        };
      }
    });

    const orderRequest: OrderRequest = {
      userId: this.getUserIdFromToken(),
      customerName: this.customerName,
      customerPhone: this.customerPhone,
      shippingAddress: this.shippingAddress,
      note: this.note,
      paymentMethod: this.paymentMethod,
      couponCode: this.appliedCouponCode,
      orderDetails: orderDetails
    };

    this.orderService.createOrder(orderRequest).subscribe({
      next: (response) => {
        if (response.code === 200) {
          this.toast.showSuccess('Đặt hàng thành công!');
          if (this.paymentMethod === 'VNPAY') {
            const orderId = response.result;
            const amount = this.finalTotal; // Lấy giá trị trước khi clearCart
            this.cartService.clearCart();
            window.location.href = `http://localhost:8080/api/v1/payment/create_payment?amount=${amount}&orderId=${orderId}`;
          } else {
            this.cartService.clearCart();
            this.router.navigate(['/']);
          }
        } else {
          this.toast.showError('Có lỗi xảy ra: ' + response.message);
        }
      },
      error: (err) => {
        this.toast.showError('Có lỗi xảy ra khi đặt hàng!');
        console.error(err);
      }
    });
  }
}
