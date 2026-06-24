import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../../../core/services/product/product';
import { Product } from '../../../../core/models/product';
import { ProductFormComponent } from '../product-form/product-form';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ProductFormComponent],
  templateUrl: './product-list.html'
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  currentPage = 1;
  totalPages = 1;
  limit = 10;

  showForm = false;
  selectedProduct: Product | null = null;

  constructor(private productService: ProductService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getAllProducts('', this.currentPage, this.limit).subscribe(res => {
      if (res.code === 200) {
        this.products = res.result.content;
        this.totalPages = res.result.totalPages;
        this.currentPage = res.result.currentPage;
        this.cdr.detectChanges();
      }
    });
  }

  openCreateForm(): void {
    this.selectedProduct = null;
    this.showForm = true;
  }

  openEditForm(product: Product): void {
    this.selectedProduct = product;
    this.showForm = true;
  }

  deleteProduct(id: number): void {
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
      this.productService.deleteProduct(id).subscribe(res => {
        if (res.code === 200) {
          alert('Xóa thành công');
          this.loadProducts();
        } else {
          alert('Lỗi: ' + res.message);
        }
      });
    }
  }

  closeForm(refresh: boolean): void {
    this.showForm = false;
    this.selectedProduct = null;
    if (refresh) {
      this.loadProducts();
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadProducts();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadProducts();
    }
  }
}
