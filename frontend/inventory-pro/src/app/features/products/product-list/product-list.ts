import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { Product, UnitOfMeasure, ProductFilterParams } from '../../../core/models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './product-list.html',
  styleUrls: ['./product-list.scss'],
})
export class ProductListComponent implements OnInit {
  private productService = inject(ProductService);

  // Signals for reactive state
  products = signal<Product[]>([]);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  // Pagination
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);
  totalItems = signal<number>(0);
  totalPages = computed(() => Math.ceil(this.totalItems() / this.pageSize()));

  // Filters
  searchTerm = signal<string>('');
  selectedUom = signal<UnitOfMeasure | ''>('');
  showActiveOnly = signal<boolean>(true);
  sortBy = signal<string>('name');
  sortDirection = signal<'asc' | 'desc'>('asc');

  // UI State
  selectedProducts = signal<number[]>([]);
  showFilters = signal<boolean>(false);

  // Enums for template
  readonly UnitOfMeasure = UnitOfMeasure;
  readonly uomOptions = Object.values(UnitOfMeasure);

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading.set(true);
    this.error.set(null);

    const params: ProductFilterParams = {
      page: this.currentPage() - 1, // Backend uses 0-based pagination
      size: this.pageSize(),
      sort: `${this.sortBy()},${this.sortDirection()}`,
      name: this.searchTerm() || undefined,
      uom: this.selectedUom() || undefined,
      active: this.showActiveOnly() || undefined,
    };

    this.productService.getAll(params).subscribe({
      next: (response) => {
        this.products.set(response.content);
        this.totalItems.set(response.numberOfElements != undefined ? response.numberOfElements : 0);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.message || 'Failed to load products');
        this.loading.set(false);
      },
    });
  }

  onSearch(): void {
    this.currentPage.set(1);
    this.loadProducts();
  }

  onFilterChange(): void {
    this.currentPage.set(1);
    this.loadProducts();
  }

  onSort(column: string): void {
    if (this.sortBy() === column) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortBy.set(column);
      this.sortDirection.set('asc');
    }
    this.loadProducts();
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
    this.loadProducts();
  }

  onPageSizeChange(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(1);
    this.loadProducts();
  }

  toggleProductSelection(id: number): void {
    const current = this.selectedProducts();
    if (current.includes(id)) {
      this.selectedProducts.set(current.filter((i) => i !== id));
    } else {
      this.selectedProducts.set([...current, id]);
    }
  }

  selectAll(): void {
    if (this.selectedProducts().length === this.products().length) {
      this.selectedProducts.set([]);
    } else {
      this.selectedProducts.set(this.products().map((p) => p.id));
    }
  }

  deleteProduct(id: number): void {
    if (!confirm('Are you sure you want to delete this product?')) return;

    this.productService.delete(id).subscribe({
      next: () => {
        this.loadProducts();
      },
      error: (err) => {
        alert(err.message || 'Failed to delete product');
      },
    });
  }

  toggleActiveStatus(product: Product): void {
    const action = product.active
      ? this.productService.deactivate(product.id)
      : this.productService.activate(product.id);

    action.subscribe({
      next: () => {
        this.loadProducts();
      },
      error: (err) => {
        alert(err.message || 'Failed to update product status');
      },
    });
  }

  bulkDelete(): void {
    if (this.selectedProducts().length === 0) return;
    if (!confirm(`Delete ${this.selectedProducts().length} products?`)) return;

    // Implement bulk delete if your backend supports it
    console.log('Bulk delete:', this.selectedProducts());
  }

  exportToCSV(): void {
    this.productService
      .exportToCsv({
        name: this.searchTerm() || undefined,
        uom: this.selectedUom() || undefined,
        active: this.showActiveOnly() || undefined,
      })
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `products-${new Date().getTime()}.csv`;
          link.click();
        },
        error: (err) => {
          alert('Failed to export products');
        },
      });
  }

  clearFilters(): void {
    this.searchTerm.set('');
    this.selectedUom.set('');
    this.showActiveOnly.set(true);
    this.loadProducts();
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
    }).format(value);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  }
}
