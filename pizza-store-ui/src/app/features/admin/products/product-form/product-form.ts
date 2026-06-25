import { Component, EventEmitter, Input, OnInit, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProductService } from '../../../../core/services/product/product';
import { CategoryService } from '../../../../core/services/categoty/category';
import { Product, ProductRequest } from '../../../../core/models/product';
import { Category } from '../../../../core/models/category';
import { UploadService } from '../../../../core/services/upload/upload';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './product-form.html'
})
export class ProductFormComponent implements OnInit {
  @Input() product: Product | null = null;
  @Output() close = new EventEmitter<boolean>();
  
  form: FormGroup;
  submitting = false;
  isUploading = false;
  categories: Category[] = [];

  constructor(
    private fb: FormBuilder, 
    private productService: ProductService,
    private categoryService: CategoryService,
    private cdr: ChangeDetectorRef,
    private uploadService: UploadService
  ) {
    this.form = this.fb.group({
      categoryId: ['', Validators.required],
      name: ['', Validators.required],
      imageUrl: [''],
      description: [''],
      isAvailable: [true],
      variants: this.fb.array([])
    });
  }

  get variants() {
    return this.form.get('variants') as FormArray;
  }

  ngOnInit(): void {
    this.categoryService.getActiveCategories().subscribe(res => {
      if (res.code === 200) {
        this.categories = res.result;
        this.cdr.detectChanges();
      }
    });

    if (this.product) {
      this.form.patchValue({
        categoryId: this.product.categoryId,
        name: this.product.name,
        imageUrl: this.product.imageUrl,
        description: this.product.description,
        isAvailable: this.product.isAvailable
      });
      
      if (this.product.variants && this.product.variants.length > 0) {
        this.product.variants.forEach(v => {
          this.variants.push(this.fb.group({
            sizeId: [v.sizeId, Validators.required],
            price: [v.price, [Validators.required, Validators.min(0)]]
          }));
        });
      } else {
        this.addVariant();
      }
    } else {
      this.addVariant();
    }
  }

  addVariant() {
    this.variants.push(this.fb.group({
      sizeId: [1, Validators.required], // Mặc định size 1 (Small)
      price: [0, [Validators.required, Validators.min(0)]]
    }));
  }

  removeVariant(index: number) {
    this.variants.removeAt(index);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
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
    const request: ProductRequest = this.form.value;

    if (this.product) {
      this.productService.updateProduct(this.product.id, request).subscribe({
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
      this.productService.addProduct(request).subscribe({
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
