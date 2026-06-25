import { Component, EventEmitter, Input, OnInit, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CategoryService } from '../../../../core/services/categoty/category';
import { Category, CategoryRequest } from '../../../../core/models/category';
import { UploadService } from '../../../../core/services/upload/upload';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './category-form.html'
})
export class CategoryFormComponent implements OnInit {
  @Input() category: Category | null = null;
  @Output() close = new EventEmitter<boolean>();
  
  form: FormGroup;
  submitting = false;
  isUploading = false;

  constructor(
    private fb: FormBuilder, 
    private categoryService: CategoryService, 
    private cdr: ChangeDetectorRef,
    private uploadService: UploadService
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      code: ['', Validators.required],
      imageUrl: [''],
      description: [''],
      isActive: [true]
    });
  }

  ngOnInit(): void {
    if (this.category) {
      this.form.patchValue({
        name: this.category.name,
        code: this.category.code,
        imageUrl: this.category.imageUrl,
        description: this.category.description,
        isActive: this.category.isActive
      });
    }
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
    const request: CategoryRequest = this.form.value;

    if (this.category) {
      // Update
      this.categoryService.updateCategory(this.category.id, request).subscribe({
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
      // Create
      this.categoryService.createCategory(request).subscribe({
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
