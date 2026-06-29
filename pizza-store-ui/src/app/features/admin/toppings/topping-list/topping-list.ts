import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToppingService } from '../../../../core/services/topping/topping';
import { Topping } from '../../../../core/models/topping';
import { ToppingFormComponent } from '../topping-form/topping-form';

@Component({
  selector: 'app-topping-list',
  standalone: true,
  imports: [CommonModule, ToppingFormComponent],
  templateUrl: './topping-list.html'
})
export class ToppingListComponent implements OnInit {
  toppings: Topping[] = [];
  showForm = false;
  selectedTopping: Topping | null = null;

  constructor(private toppingService: ToppingService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadToppings();
  }

  loadToppings(): void {
    this.toppingService.getToppings().subscribe(res => {
      if (res.code === 200) {
        this.toppings = res.result;
        this.cdr.detectChanges();
      }
    });
  }

  openCreateForm(): void {
    this.selectedTopping = null;
    this.showForm = true;
  }

  openEditForm(topping: Topping): void {
    this.selectedTopping = topping;
    this.showForm = true;
  }

  deleteTopping(id: number): void {
    if (confirm('Bạn có chắc chắn muốn xóa topping này?')) {
      this.toppingService.deleteTopping(id).subscribe(res => {
        if (res.code === 200) {
          alert('Xóa thành công');
          this.loadToppings();
        } else {
          alert('Lỗi: ' + res.message);
        }
      });
    }
  }

  closeForm(refresh: boolean): void {
    this.showForm = false;
    this.selectedTopping = null;
    if (refresh) {
      this.loadToppings();
    }
  }
}
