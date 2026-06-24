import { Component, EventEmitter, OnInit, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../../core/services/user/user.service';
import { Role } from '../../../../core/models/role';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-form.html'
})
export class UserFormComponent implements OnInit {
  @Output() close = new EventEmitter<boolean>();
  
  userForm: FormGroup;
  roles: Role[] = [];
  submitting = false;

  constructor(private fb: FormBuilder, private userService: UserService, private cdr: ChangeDetectorRef) {
    this.userForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: [''],
      dob: [''],
      roleName: ['KITCHEN', Validators.required]
    });
  }

  ngOnInit(): void {
    this.userService.getAllRoles().subscribe(res => {
      if (res.code === 200) {
        this.roles = res.result;
        this.cdr.detectChanges();
      }
    });
  }

  onSubmit(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.userService.createStaff(this.userForm.value).subscribe({
      next: (res) => {
        this.submitting = false;
        this.cdr.detectChanges();
        if (res.code === 200) {
          alert('Thêm nhân viên thành công!');
          this.close.emit(true);
        } else {
          alert('Lỗi: ' + res.message);
        }
      },
      error: (err) => {
        this.submitting = false;
        this.cdr.detectChanges();
        alert('Lỗi server: ' + (err.error?.message || err.message));
      }
    });
  }

  onCancel(): void {
    this.close.emit(false);
  }
}
