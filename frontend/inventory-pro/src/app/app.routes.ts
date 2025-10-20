import { Routes } from '@angular/router';
import { ProductFormComponent } from './features/products/product-form/product-form';
import { NavbarComponent } from './layouts/navbar/navbar';
import { Dashboard } from './features/dashboard/dashboard';
import { Suppliers } from './features/suppliers/suppliers';
import { PurchaseOrders } from './features/purchase-orders/purchase-orders';
import { ProductListComponent } from './features/products/product-list/product-list';

export const routes: Routes = [
  { path: '', component: NavbarComponent },
  { path: 'products', component: ProductListComponent },
  { path: 'products/add', component: ProductFormComponent },
  { path: 'products/edit/:id', component: ProductFormComponent },
  { path: 'dashboard', component: Dashboard },
  { path: 'suppliers', component: Suppliers },
  { path: 'purchase-orders', component: PurchaseOrders },
];
