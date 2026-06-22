import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {Router, RouterModule} from '@angular/router';
import {Auth} from '../../core/services/auth';
import {ToastService} from '../../core/services/toast';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register implements OnInit {
  registerForm!: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private auth: Auth,
    private toastService: ToastService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
      ]],
      email: ['', [Validators.required, Validators.email]],
      fullName: ['', [Validators.required]],
      phoneNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      dob: ['', [Validators.required]],
    });
  }

  onSubmit() {
    if(this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const requestData = { ...this.registerForm.value };
    if (requestData.dob) {
      requestData.dob = `${requestData.dob}T00:00:00`;
    }

    this.auth.register(requestData).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.code === 200 || response.code === 201) {
          this.successMessage = 'Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.';

          this.toastService.showSuccess(this.successMessage);
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        }else{
          this.errorMessage = response.message || 'Đăng ký thất bại';
        }
        this.cdr.detectChanges();
      },

      error: (err) => {
        this.isLoading = false;
        if (err.error && err.error.message) {
          this.errorMessage = err.error.message;
          this.toastService.showError(this.errorMessage);
        } else {
          this.errorMessage = 'Đã xảy ra lỗi khi kết nối, vui lòng thử lại sau!';
        }
        this.cdr.detectChanges();
      }
    })
  }
}
