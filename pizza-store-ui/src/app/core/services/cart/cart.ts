import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Product } from '../../models/product';
import { Topping } from '../../models/topping';
import { Combo } from '../../models/combo';
import { Coupon } from '../../models/coupon';

export interface CartItem {
  quantity: number;
  price: number; // Base price of size + toppings price OR combo price

  // For products
  product?: Product;
  sizeId?: number;
  sizeName?: string;
  toppings?: Topping[];

  // For combos
  combo?: Combo;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItems = new BehaviorSubject<CartItem[]>(this.loadCart());
  cartItems$ = this.cartItems.asObservable();

  private appliedCoupon = new BehaviorSubject<Coupon | null>(this.loadCoupon());
  appliedCoupon$ = this.appliedCoupon.asObservable();

  constructor() { }

  private getCartKey(): string {
    const token = localStorage.getItem('authToken');
    if (!token) return 'cart_guest';
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return `cart_${payload.sub || 'guest'}`;
    } catch (e) {
      return 'cart_guest';
    }
  }

  private getCouponKey(): string {
    return `${this.getCartKey()}_coupon`;
  }

  private loadCart(): CartItem[] {
    const savedCart = localStorage.getItem(this.getCartKey());
    return savedCart ? JSON.parse(savedCart) : [];
  }

  private loadCoupon(): Coupon | null {
    const savedCoupon = localStorage.getItem(this.getCouponKey());
    return savedCoupon ? JSON.parse(savedCoupon) : null;
  }

  private saveCart(items: CartItem[]) {
    localStorage.setItem(this.getCartKey(), JSON.stringify(items));
    this.cartItems.next(items);
  }

  private saveCoupon(coupon: Coupon | null) {
    if (coupon) {
      localStorage.setItem(this.getCouponKey(), JSON.stringify(coupon));
    } else {
      localStorage.removeItem(this.getCouponKey());
    }
    this.appliedCoupon.next(coupon);
  }

  reloadCart() {
    this.cartItems.next(this.loadCart());
    this.appliedCoupon.next(this.loadCoupon());
  }

  addToCart(item: CartItem) {
    const items = this.cartItems.getValue();

    // Check if item already exists
    let existingIndex = -1;
    if (item.product) {
      existingIndex = items.findIndex(i =>
        i.product && i.product.id === item.product!.id &&
        i.sizeId === item.sizeId &&
        this.areToppingsEqual(i.toppings || [], item.toppings || [])
      );
    } else if (item.combo) {
      existingIndex = items.findIndex(i => i.combo && i.combo.id === item.combo!.id);
    }

    if (existingIndex > -1) {
      items[existingIndex].quantity += item.quantity;
    } else {
      items.push(item);
    }

    this.saveCart(items);
  }

  removeFromCart(index: number) {
    const items = this.cartItems.getValue();
    items.splice(index, 1);
    this.saveCart(items);
  }

  updateQuantity(index: number, quantity: number) {
    const items = this.cartItems.getValue();
    if (quantity > 0) {
      items[index].quantity = quantity;
      this.saveCart(items);
    }
  }

  clearCart() {
    this.saveCart([]);
    this.saveCoupon(null);
  }

  getCartTotal(): number {
    return this.cartItems.getValue().reduce((total, item) => {
      return total + (item.price * item.quantity);
    }, 0);
  }

  applyCoupon(coupon: Coupon) {
    this.saveCoupon(coupon);
  }

  removeCoupon() {
    this.saveCoupon(null);
  }

  getDiscountAmount(): number {
    const subtotal = this.getCartTotal();
    const coupon = this.appliedCoupon.getValue();
    if (!coupon) return 0;

    let discount = 0;
    if (coupon.discountType === 'AMOUNT') {
      discount = coupon.discountValue;
    } else {
      discount = subtotal * (coupon.discountValue / 100);
    }

    return Math.min(discount, subtotal); // Cannot discount more than subtotal
  }

  getFinalTotal(): number {
    return this.getCartTotal() - this.getDiscountAmount();
  }

  private areToppingsEqual(t1: Topping[], t2: Topping[]): boolean {
    if (t1.length !== t2.length) return false;
    const sorted1 = [...t1].sort((a, b) => a.id - b.id);
    const sorted2 = [...t2].sort((a, b) => a.id - b.id);
    return sorted1.every((val, index) => val.id === sorted2[index].id);
  }
}
