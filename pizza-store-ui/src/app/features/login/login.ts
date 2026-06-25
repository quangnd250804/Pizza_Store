import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterModule} from '@angular/router';
import {Auth} from '../../core/services/auth';
import {ToastService} from '../../core/services/toast';
import {CartService} from '../../core/services/cart/cart';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  loginForm!: FormGroup;
  errorMessage: string = '';
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private auth: Auth,
    private toastService: ToastService,
    private cdr: ChangeDetectorRef,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();// Kích hoạt hiển thị viền đỏ báo lỗi nếu form trống
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.auth.login(this.loginForm.value).subscribe({
      next: (response) => {
        this.isLoading = false;

        console.log('Backend trả về:', response);

        if (response.code === 200 && response.result) {
          this.auth.saveToken(response.result.accessToken, response.result.refreshToken);
          this.cartService.reloadCart();

          this.toastService.showSuccess('Đăng nhập thành công!Chào mừng bạn đến với Pizza Store');

          if (this.auth.isAdmin()) {
            this.router.navigate(['/admin']);
          } else if (this.auth.isCashier()) {
            this.router.navigate(['/cashier']);
          } else {
            this.router.navigate(['/']);
          }
        }else{
          this.errorMessage = response.message || 'Đăng nhập thất bại';
        }
        this.cdr.detectChanges();
      },

      error: (err) => {
        this.isLoading = false;

        console.error('Lỗi kết nối API:', err);

        if (err.error && err.error.message) {
          this.errorMessage = err.error.message;

          this.toastService.showError(this.errorMessage);
        }else{
          this.errorMessage = 'Đã xảy ra lỗi khi kết nối đến server';
        }
        this.cdr.detectChanges();
      }
    });
  }
}
