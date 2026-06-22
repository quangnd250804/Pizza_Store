import { Routes } from '@angular/router';
import {Login} from './features/login/login';
import {Register} from './features/register/register';
import {Home} from './features/home/home';
import {CheckoutComponent} from './features/checkout/checkout';
import {PaymentResultComponent} from './features/payment/payment-result';
import {OrderHistoryComponent} from './features/orders/order-history/order-history';
import {OrderDetailComponent} from './features/orders/order-detail/order-detail';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'checkout', component: CheckoutComponent },
  { path: 'payment-result', component: PaymentResultComponent },
  { path: 'orders', component: OrderHistoryComponent },
  { path: 'orders/:id', component: OrderDetailComponent },
  { path: '', component: Home },
];
