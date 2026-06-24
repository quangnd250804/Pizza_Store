import { Routes } from '@angular/router';
import {Login} from './features/login/login';
import {Register} from './features/register/register';
import {Home} from './features/home/home';
import {CheckoutComponent} from './features/checkout/checkout';
import {PaymentResultComponent} from './features/payment/payment-result';
import {OrderHistoryComponent} from './features/orders/order-history/order-history';
import {OrderDetailComponent} from './features/orders/order-detail/order-detail';

import {AdminLayoutComponent} from './shared/admin-layout/admin-layout';
import {DashboardComponent} from './features/admin/dashboard/dashboard';
import {UserListComponent} from './features/admin/users/user-list/user-list';
import {CategoryListComponent} from './features/admin/categories/category-list/category-list';
import {ProductListComponent} from './features/admin/products/product-list/product-list';
import {ToppingListComponent} from './features/admin/toppings/topping-list/topping-list';
import {ComboListComponent} from './features/admin/combos/combo-list/combo-list';
import {OrderListComponent} from './features/admin/orders/order-list/order-list';
import {AdminOrderDetailComponent} from './features/admin/orders/admin-order-detail/admin-order-detail';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'checkout', component: CheckoutComponent },
  { path: 'payment-result', component: PaymentResultComponent },
  { path: 'orders', component: OrderHistoryComponent },
  { path: 'orders/:id', component: OrderDetailComponent },
  { 
    path: 'admin', 
    component: AdminLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'users', component: UserListComponent },
      { path: 'categories', component: CategoryListComponent },
      { path: 'products', component: ProductListComponent },
      { path: 'toppings', component: ToppingListComponent },
      { path: 'combos', component: ComboListComponent },
      { path: 'orders', component: OrderListComponent },
      { path: 'orders/:id', component: AdminOrderDetailComponent }
    ]
  },
  { path: '', component: Home },
];
