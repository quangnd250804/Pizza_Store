import { Component, EventEmitter, Input, OnInit, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToppingService } from '../../../../core/services/topping/topping';
import { Topping, ToppingRequest } from '../../../../core/models/topping';

@Component({
  selector: 'app-topping-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './topping-form.html'
})
export class ToppingFormComponent implements OnInit {
  @Input() topping: Topping | null = null;
  @Output() close = new EventEmitter<boolean>();
  
  form: FormGroup;
  submitting = false;

  constructor(private fb: FormBuilder, private toppingService: ToppingService, private cdr: ChangeDetectorRef) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      isAvailable: [true]
    });
  }

  ngOnInit(): void {
    if (this.topping) {
      this.form.patchValue({
        name: this.topping.name,
        price: this.topping.price,
        isAvailable: this.topping.isAvailable
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const request: ToppingRequest = this.form.value;

    if (this.topping) {
      this.toppingService.updateTopping(this.topping.id, request).subscribe({
        next: (res) => {
          this.submitting = false;
          this.cdr.detectChanges();
          if (res.code === 200) {
            alert('Cập nhật thành công!');
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
    } else {
      this.toppingService.createTopping(request).subscribe({
        next: (res) => {
          this.submitting = false;
          this.cdr.detectChanges();
          if (res.code === 200) {
            alert('Thêm thành công!');
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
  }

  onCancel(): void {
    this.close.emit(false);
  }
}
