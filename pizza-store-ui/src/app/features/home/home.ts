import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Auth } from '../../core/services/auth';
import { Category } from '../../core/models/category';
import { Product, ProductVariant } from '../../core/models/product';
import { Combo } from '../../core/models/combo';
import { Topping } from '../../core/models/topping';
import { CategoryService } from '../../core/services/categoty/category';
import { ProductService } from '../../core/services/product/product';
import { ComboService } from '../../core/services/combo/combo';
import { ToppingService } from '../../core/services/topping/topping';
import { DecimalPipe } from '@angular/common';
import { CartComponent } from '../cart/cart';
import { CartService } from '../../core/services/cart/cart';
import { HeaderComponent } from '../../shared/header/header';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.html',
  imports: [
    DecimalPipe,
    CartComponent,
    RouterModule,
    HeaderComponent
  ],
  styleUrls: ['./home.css']
})
export class Home implements OnInit {
  categories: Category[] = [];
  products: Product[] = [];
  combos: Combo[] = [];
  toppings: Topping[] = [];
  selectedCategoryCode: string = ''; //Lưu mã danh mục đang được chọn (mặc định để trống '' tức là hiển thị "Tất cả").
  currentTab: 'menu' | 'combo' = 'menu'; //Để biết khách đang đứng ở tab "Gọi món lẻ" hay tab "Mua Combo".
  selectedVariants: { [productId: number]: ProductVariant } = {}; //Lưu số lượng từng biến thể của sản phẩm (key là productId, value là ProductVariant chứa thông tin biến thể và số lượng).
  selectedToppings: { [productId: number]: Topping[] } = {}; //Lưu các topping đã chọn cho từng sản phẩm (key là productId, value là mảng Topping đã chọn).

  currentPage: number = 1;
  totalPages: number = 1;
  totalElements: number = 0;

  constructor(
    private auth: Auth,
    private router: Router,
    private categoryService: CategoryService,
    private productService: ProductService,
    private comboService: ComboService,
    private toppingService: ToppingService,
    private cdr: ChangeDetectorRef,
    private cartService: CartService
  ) { }

  ngOnInit(): void {

    this.categoryService.getActiveCategories().subscribe({
      next: (response) => {
        if (response.code === 200 && response.result) {
          this.categories = response.result;
          console.log('Danh muc da tai: ', this.categories);

          this.loadMenu();
        }
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.log('Loi khi tai danh muc: ', error);

        this.loadMenu();
        this.cdr.detectChanges();
      }
    });

    this.comboService.getActiveCombos().subscribe({
      next: (response) => {
        if (response.code === 200 && response.result) {
          this.combos = response.result;
          console.log('Combo da tai: ', this.combos);
        }
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.log('Loi khi tai combo: ', error);
        this.cdr.detectChanges();
      }
    });

    this.toppingService.getActiveToppings().subscribe({
      next: (response) => {
        if (response.code === 200 && response.result) {
          this.toppings = response.result;
          console.log('Topping da tai: ', this.toppings);
        }
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.log('Loi khi tai combo: ', error);
        this.cdr.detectChanges();
      }
    });
  }

  loadMenu(categoryCode?: string, page: number = 1) {
    this.selectedCategoryCode = categoryCode !== undefined ? categoryCode : this.selectedCategoryCode;
    this.productService.getMenu(this.selectedCategoryCode, page).subscribe({
      next: (response) => {
        if (response.code === 200 && response.result) {
          this.products = response.result.content;
          this.currentPage = response.result.currentPage;
          this.totalPages = response.result.totalPages;
          this.totalElements = response.result.totalElements;

          console.log('Danh sách sản phẩm: ', this.products);

          // Ensure compatibility: some backends return `available`/`deleted` while frontend expects `isAvailable`/`isDeleted`
          for (const p of this.products as any[]) {
            if (p.available !== undefined && p.isAvailable === undefined) {
              p.isAvailable = p.available;
            }
            if (p.deleted !== undefined && p.isDeleted === undefined) {
              p.isDeleted = p.deleted;
            }
          }

          for (const product of this.products) {
            if (product.variants && product.variants.length > 0) {
              this.selectedVariants[product.id] = product.variants[0];
            }
          }
        }
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.log('Loi khi tai menu: ', error);
        this.cdr.detectChanges();
      }
    });
  }

  changePage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.loadMenu(this.selectedCategoryCode, page);
      this.cdr.detectChanges();
    }
  }

  onSizeChange(productId: number, variant: ProductVariant) {
    this.selectedVariants[productId] = variant;
    this.cdr.detectChanges();
  }

  onToppingToggle(productId: number, topping: Topping) {
    // Nếu sản phẩm này chưa từng được chọn topping nào, khởi tạo mảng rỗng
    if (!this.selectedToppings[productId]) {
      this.selectedToppings[productId] = [];
    }

    const currentToppings = this.selectedToppings[productId];
    const index = currentToppings.findIndex(t => t.id === topping.id);

    if (index > -1) {
      // Nếu topping đã tồn tại trong mảng, bỏ chọn (xóa khỏi mảng)
      currentToppings.splice(index, 1);
    } else {
      // Nếu topping chưa tồn tại, thêm vào mảng
      currentToppings.push(topping);
    }

    this.cdr.detectChanges();
  }

  isToppingSelected(productId: number, toppingId: number): boolean {
    if (!this.selectedToppings[productId]) return false;
    return this.selectedToppings[productId].some(t => t.id === toppingId);
  }

  addToCart(product: Product) {
    const selectedVariant = this.selectedVariants[product.id];
    const chosenToppings = [...(this.selectedToppings[product.id] || [])];

    const toppingPrice = chosenToppings.reduce((total, t) => total + t.price, 0);
    const basePrice = selectedVariant ? selectedVariant.price : product.variants && product.variants.length > 0 ? 0 : 0; // Or standard price
    const totalPrice = toppingPrice + basePrice;

    if (selectedVariant) {
      this.cartService.addToCart({
        product: product,
        sizeId: selectedVariant.sizeId,
        sizeName: selectedVariant.sizeName,
        quantity: 1,
        toppings: chosenToppings,
        price: totalPrice
      });
    } else {
      alert(`Vui lòng chọn kích cỡ cho ${product.name}!`);
    }
  }

  addComboToCart(combo: Combo) {
    this.cartService.addToCart({
      combo: combo,
      quantity: 1,
      price: combo.price
    });
  }
}
