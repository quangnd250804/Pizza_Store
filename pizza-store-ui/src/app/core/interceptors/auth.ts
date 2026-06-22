import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {Auth} from '../services/auth';
import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {catchError, switchMap, throwError} from 'rxjs';

export const auth: HttpInterceptorFn = (req, next) => {
  const auth = inject(Auth);
  const router = inject(Router);

  const accessToken = auth.getToken();
  let authReq = req;

  // 1. Tự động đính kèm Access Token vào Header nếu tồn tại (trừ các API login/register/refresh công khai)
  if (accessToken && !req.url.includes('/api/v1/auth/')) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`
      }
    });
  }

  // 2. Chuyển tiếp Request đi và bắt lỗi tập trung nếu có phản hồi lỗi từ Backend
  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      //Nếu Backend trả về lỗi 401 Unauthorized (Access Token hết hạn)
      if (error.status === 401 && !req.url.includes('/api/v1/auth/refresh')) {
        const refreshToken = auth.getReFreshToken();

        if (refreshToken) {
          //Gọi API refresh token để lấy Access Token mới
          return auth.refreshToken(refreshToken).pipe(
            switchMap((response) => {
              if (response.code === 200 && response.result) {
                //Lưu Access Token mới vào localStorage
                auth.saveToken(response.result.accessToken, response.result.refreshToken);

                //Tạo lại Request gốc với Access Token mới và gửi lại
                const newAuthReq = req.clone({
                  setHeaders: {
                    Authorization: `Bearer ${response.result.accessToken}`
                  }
                });

                return next(newAuthReq);
              }

              auth.clearTokens();
              router.navigate(['/login']);
              return throwError(() => error);
            }),
            catchError((refreshErr) => {
              // Bất kỳ lỗi nào xảy ra trong quá trình refresh -> Đá văng ra màn hình Login
              auth.clearTokens();
              router.navigate(['/login']);
              return throwError(() => refreshErr);
            })
          );
        }
      }

      // Nếu là các mã lỗi khác (400, 403, 500...), ném tiếp ra ngoài cho Component xử lý hiển thị alert
      return throwError(() => error);
    })
  );
};
