import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ToastMessage, ToastService} from '../../core/services/toast';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.html',
  styleUrl: './toast.css',
})
export class Toast implements OnInit{
  toast!: ToastMessage;

  constructor(
    private toastService: ToastService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
      this.toastService.toastState$.subscribe((state) => {
        this.toast = state;
        this.cdr.detectChanges();
      })
  }
}
