import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../../../core/services/categoty/category';
import { Category } from '../../../../core/models/category';
import { CategoryFormComponent } from '../category-form/category-form';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule, CategoryFormComponent],
  templateUrl: './category-list.html'
})
export class CategoryListComponent implements OnInit {
  categories: Category[] = [];
  showForm = false;
  selectedCategory: Category | null = null;

  constructor(private categoryService: CategoryService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe(res => {
      if (res.code === 200) {
        this.categories = res.result;
        this.cdr.detectChanges();
      }
    });
  }

  openCreateForm(): void {
    this.selectedCategory = null;
    this.showForm = true;
  }

  openEditForm(category: Category): void {
    this.selectedCategory = category;
    this.showForm = true;
  }

  deleteCategory(id: number): void {
    if (confirm('Bạn có chắc chắn muốn xóa danh mục này?')) {
      this.categoryService.deleteCategory(id).subscribe(res => {
        if (res.code === 200) {
          alert('Xóa thành công');
          this.loadCategories();
        } else {
          alert('Lỗi: ' + res.message);
        }
      });
    }
  }

  closeForm(refresh: boolean): void {
    this.showForm = false;
    this.selectedCategory = null;
    if (refresh) {
      this.loadCategories();
    }
  }
}
