import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComboService } from '../../../../core/services/combo/combo';
import { Combo } from '../../../../core/models/combo';
import { ComboFormComponent } from '../combo-form/combo-form';

@Component({
  selector: 'app-combo-list',
  standalone: true,
  imports: [CommonModule, ComboFormComponent],
  templateUrl: './combo-list.html'
})
export class ComboListComponent implements OnInit {
  combos: Combo[] = [];
  showForm = false;
  selectedCombo: Combo | null = null;

  constructor(private comboService: ComboService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadCombos();
  }

  loadCombos(): void {
    this.comboService.getCombos().subscribe(res => {
      if (res.code === 200) {
        this.combos = res.result;
        this.cdr.detectChanges();
      }
    });
  }

  openCreateForm(): void {
    this.selectedCombo = null;
    this.showForm = true;
  }

  openEditForm(combo: Combo): void {
    this.selectedCombo = combo;
    this.showForm = true;
  }

  deleteCombo(id: number): void {
    if (confirm('Bạn có chắc chắn muốn xóa combo này?')) {
      this.comboService.deleteCombo(id).subscribe(res => {
        if (res.code === 200) {
          alert('Xóa thành công');
          this.loadCombos();
        } else {
          alert('Lỗi: ' + res.message);
        }
      });
    }
  }

  closeForm(refresh: boolean): void {
    this.showForm = false;
    this.selectedCombo = null;
    if (refresh) {
      this.loadCombos();
    }
  }
}
