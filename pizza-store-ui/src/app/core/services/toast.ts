import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';

export interface ToastMessage{
  message: string;
  type: 'success' | 'error' | 'info';
  show: boolean;
}
@Injectable({
  providedIn: 'root',
})
export class ToastService {
  //Trạng thái mặc định là ẩn Toast
  private toastState = new BehaviorSubject<ToastMessage>({
    message: '',
    type: 'success',
    show: false
  });

  //Observable để Component đăng ký lắng nghe thay đổi trạng thái Toast
  toastState$ = this.toastState.asObservable();

  // Hàm gọi hiển thị thông báo thành công
  showSuccess(message: string) {
    this.show(message, 'success');
  }

  // Hàm gọi hiển thị thông báo lỗi
  showError(message: string) {
    this.show(message, 'error');
  }

  private show(mes : string, type: 'success' | 'error' | 'info') {
    this.toastState.next({
      message: mes,
      type,
      show: true
    });

    setTimeout(() => {
      this.toastState.next({
        message: '',
        type,
        show: false
      });
    }, 2000); // Ẩn Toast sau 3 giây
  }
}
