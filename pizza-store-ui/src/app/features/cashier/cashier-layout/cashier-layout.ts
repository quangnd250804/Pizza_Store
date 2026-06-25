import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router } from '@angular/router';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-cashier-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './cashier-layout.html'
})
export class CashierLayoutComponent implements OnInit, OnDestroy {
  currentTime: Date = new Date();
  timerInterval: ReturnType<typeof setInterval> | undefined;
  cashierName: string = 'Thu Ngân';

  constructor(private authService: Auth, private router: Router) {}

  ngOnInit(): void {
    const token = this.authService.getToken();
    if (token) {
      try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const decoded = JSON.parse(atob(base64));
        if (decoded.sub) {
          this.cashierName = decoded.sub;
        }
      } catch (e) {
        console.error('Lỗi parse token', e);
      }
    }

    this.timerInterval = setInterval(() => {
      this.currentTime = new Date();
    }, 1000);
  }

  ngOnDestroy(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  logout(): void {
    const refreshToken = this.authService.getReFreshToken();
    if (refreshToken) {
      this.authService.logoutServer(refreshToken).subscribe({
        next: () => {
          this.authService.clearTokens();
          this.router.navigate(['/login']);
        },
        error: () => {
          this.authService.clearTokens();
          this.router.navigate(['/login']);
        }
      });
    } else {
      this.authService.clearTokens();
      this.router.navigate(['/login']);
    }
  }
}
