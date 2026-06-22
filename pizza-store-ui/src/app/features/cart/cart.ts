import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService, CartItem } from '../../core/services/cart/cart';
import { CouponService } from '../../core/services/coupon/coupon';
import { Coupon } from '../../core/models/coupon';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.html',
  styleUrls: ['./cart.css']
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  subTotal: number = 0;
  discountAmount: number = 0;
  finalTotal: number = 0;

  availableCoupons: Coupon[] = [];
  couponInput: string = '';
  appliedCoupon: Coupon | null = null;
  couponError: string = '';

  constructor(
    private cartService: CartService,
    private couponService: CouponService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) { }

  ngOnInit() {
    this.cartService.cartItems$.subscribe(items => {
      this.cartItems = items;
      this.updateTotals();
    });

    this.cartService.appliedCoupon$.subscribe(coupon => {
      this.appliedCoupon = coupon;
      if (coupon) {
        this.couponInput = coupon.code;
      }
      this.updateTotals();
    });

    this.loadAvailableCoupons();
  }

  updateTotals() {
    this.subTotal = this.cartService.getCartTotal();
    this.discountAmount = this.cartService.getDiscountAmount();
    this.finalTotal = this.cartService.getFinalTotal();
    this.cdr.markForCheck();
    this.cdr.detectChanges();
  }

  loadAvailableCoupons() {
    this.couponService.getActiveCoupons().subscribe({
      next: (coupons) => {
        this.availableCoupons = coupons;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi khi tải danh sách mã giảm giá', err);
      }
    });
  }

  selectCoupon(code: string) {
    this.couponInput = code;
    this.applyCoupon();
  }

  applyCoupon() {
    if (!this.couponInput.trim()) {
      this.couponError = 'Vui lòng nhập mã giảm giá';
      return;
    }

    if (this.subTotal === 0) {
      this.couponError = 'Giỏ hàng đang trống';
      return;
    }

    this.couponError = '';
    this.couponService.validateCoupon(this.couponInput.trim(), this.subTotal).subscribe({
      next: (coupon) => {
        this.cartService.applyCoupon(coupon);
        this.couponError = '';
        this.cdr.markForCheck();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.couponError = err.error?.message || 'Mã giảm giá không hợp lệ';
        this.cartService.removeCoupon();
        this.cdr.markForCheck();
        this.cdr.detectChanges();
      }
    });
  }

  removeCoupon() {
    this.couponInput = '';
    this.couponError = '';
    this.cartService.removeCoupon();
  }

  updateQuantity(index: number, quantity: number) {
    if (quantity > 0) {
      this.cartService.updateQuantity(index, quantity);
    } else {
      this.cartService.removeFromCart(index);
    }
  }

  removeItem(index: number) {
    this.cartService.removeFromCart(index);
  }

  goToCheckout() {
    this.router.navigate(['/checkout']);
  }
}
