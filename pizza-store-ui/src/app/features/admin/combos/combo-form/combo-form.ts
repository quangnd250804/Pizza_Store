import { Component, EventEmitter, Input, OnInit, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ComboService } from '../../../../core/services/combo/combo';
import { ProductService } from '../../../../core/services/product/product';
import { Combo, ComboRequest } from '../../../../core/models/combo';
import { Product } from '../../../../core/models/product';
import { UploadService } from '../../../../core/services/upload/upload';

@Component({
  selector: 'app-combo-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './combo-form.html'
})
export class ComboFormComponent implements OnInit {
  @Input() combo: Combo | null = null;
  @Output() close = new EventEmitter<boolean>();
  
  form: FormGroup;
  submitting = false;
  isUploading = false;
  products: Product[] = [];

  constructor(
    private fb: FormBuilder, 
    private comboService: ComboService,
    private productService: ProductService,
    private cdr: ChangeDetectorRef,
    private uploadService: UploadService
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      imageUrl: [''],
      description: [''],
      isAvailable: [true],
      details: this.fb.array([])
    });
  }

  get details() {
    return this.form.get('details') as FormArray;
  }

  ngOnInit(): void {
    // Load products with limit=100 for the dropdown (simple approach)
    this.productService.getAllProducts('', 1, 100).subscribe(res => {
      if (res.code === 200) {
        this.products = res.result.content;
        this.cdr.detectChanges();
      }
    });

    if (this.combo) {
      this.form.patchValue({
        name: this.combo.name,
        price: this.combo.price,
        imageUrl: this.combo.imageUrl,
        description: this.combo.description,
        isAvailable: this.combo.isAvailable
      });
      
      if (this.combo.details && this.combo.details.length > 0) {
        this.combo.details.forEach(d => {
          this.details.push(this.fb.group({
            productId: [d.productId, Validators.required],
            quantity: [d.quantity, [Validators.required, Validators.min(1)]]
          }));
        });
      } else {
        this.addDetail();
      }
    } else {
      this.addDetail();
    }
  }

  addDetail() {
    this.details.push(this.fb.group({
      productId: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]]
    }));
  }

  removeDetail(index: number) {
    this.details.removeAt(index);
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.isUploading = true;
      this.uploadService.uploadImage(file).subscribe({
        next: (res) => {
          this.isUploading = false;
          if (res.code === 200 && res.result) {
            this.form.patchValue({ imageUrl: res.result });
          } else {
            alert('Upload ảnh lỗi: ' + res.message);
          }
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.isUploading = false;
          alert('Upload ảnh thất bại: ' + (err.error?.message || err.message));
          this.cdr.detectChanges();
        }
      });
    }
  }

  onSubmit(): void {
    if (this.isUploading) {
      alert('Vui lòng chờ ảnh tải lên xong.');
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const request: ComboRequest = this.form.value;

    if (this.combo) {
      this.comboService.updateCombo(this.combo.id, request).subscribe({
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
      this.comboService.createCombo(request).subscribe({
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
