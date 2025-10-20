import { Component, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './layouts/navbar/navbar';
import { ProductFormComponent } from './features/products/product-form/product-form';
import { Dashboard } from './features/dashboard/dashboard';
import { PurchaseOrders } from './features/purchase-orders/purchase-orders';
import { Suppliers } from './features/suppliers/suppliers';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    // RouterLink,
    NavbarComponent,
    // ProductFormComponent,
    // Dashboard,
    // PurchaseOrders,
    // Suppliers,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('inventory-pro');
}
