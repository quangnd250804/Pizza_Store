import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Auth } from '../../core/services/auth';
import { CartService, CartItem } from '../../core/services/cart/cart';
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './header.html'
})
export class HeaderComponent implements OnInit {
  username: string = '';
  isProfileMenuOpen: boolean = false;
  isCartMenuOpen: boolean = false;
  isLoggedIn: boolean = false;
  cartCount: number = 0;
  cartItems: CartItem[] = [];
  total: number = 0;

  constructor(
    private auth: Auth,
    private router: Router,
    private cartService: CartService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const token = this.auth.getToken();
    if (token) {
      this.isLoggedIn = true;
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.username = payload.sub || 'Tài khoản';
      } catch (e) {
        this.username = 'Tài khoản';
      }
    } else {
      this.isLoggedIn = false;
    }

    this.cartService.cartItems$.subscribe(items => {
      this.cartItems = items;
      this.cartCount = items.length;
      this.total = this.cartService.getCartTotal();
      this.cdr.detectChanges();
    });
  }

  logout() {
    const refreshToken = this.auth.getReFreshToken();
    if (refreshToken) {
      this.auth.logoutServer(refreshToken).subscribe({
        next: () => {
          this.executeClientLogout();
        },
        error: (err) => {
          console.error('Lỗi khi gọi API logout:', err);
          this.executeClientLogout();
        }
      });
    } else {
      this.executeClientLogout();
    }
  }

  private executeClientLogout() {
    this.auth.clearTokens();
    this.cartService.reloadCart();
    this.router.navigate(['/login']);
  }

  toggleProfileMenu() {
    this.isProfileMenuOpen = !this.isProfileMenuOpen;
    if (this.isProfileMenuOpen) this.isCartMenuOpen = false;
  }

  toggleCartMenu() {
    this.isCartMenuOpen = !this.isCartMenuOpen;
    if (this.isCartMenuOpen) this.isProfileMenuOpen = false;
  }

  updateQuantity(index: number, quantity: number) {
    if (quantity > 0) {
      this.cartService.updateQuantity(index, quantity);
    } else {
      this.cartService.removeFromCart(index);
    }
    this.cdr.detectChanges();
  }

  removeItem(index: number) {
    this.cartService.removeFromCart(index);
    this.cdr.detectChanges();
  }

  goToCheckout() {
    this.isCartMenuOpen = false;
    this.router.navigate(['/checkout']);
  }
}
