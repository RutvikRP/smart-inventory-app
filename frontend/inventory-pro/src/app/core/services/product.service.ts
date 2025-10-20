import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  Product,
  CreateProductDto,
  UpdateProductDto,
  ProductFilterParams,
} from '../models/product.model';
import { ApiResponse, PaginatedResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiService = inject(ApiService);
  private endpoint = 'products';

  /**
   * Get all products with pagination and filters
   */
  getAll(params?: ProductFilterParams): Observable<PaginatedResponse<Product>> {
    return this.apiService.get<PaginatedResponse<Product>>(this.endpoint, params);
  }

  /**
   * Get active products only
   */
  getActive(params?: ProductFilterParams): Observable<PaginatedResponse<Product>> {
    return this.apiService.get<PaginatedResponse<Product>>(`${this.endpoint}/active`, params);
  }

  /**
   * Get product by ID
   */
  getById(id: number): Observable<ApiResponse<Product>> {
    return this.apiService.get<ApiResponse<Product>>(`${this.endpoint}/${id}`);
  }

  /**
   * Get product by SKU
   */
  getBySku(sku: string): Observable<ApiResponse<Product>> {
    return this.apiService.get<ApiResponse<Product>>(`${this.endpoint}/sku/${sku}`);
  }

  /**
   * Create new product
   */
  create(product: CreateProductDto): Observable<ApiResponse<Product>> {
    return this.apiService.post<ApiResponse<Product>>(this.endpoint, product);
  }

  /**
   * Update existing product
   */
  update(id: number, product: UpdateProductDto): Observable<ApiResponse<Product>> {
    return this.apiService.put<ApiResponse<Product>>(`${this.endpoint}/${id}`, product);
  }

  /**
   * Soft delete product
   */
  delete(id: number): Observable<ApiResponse<void>> {
    return this.apiService.delete<ApiResponse<void>>(`${this.endpoint}/${id}`);
  }

  /**
   * Activate product
   */
  activate(id: number): Observable<ApiResponse<Product>> {
    return this.apiService.patch<ApiResponse<Product>>(`${this.endpoint}/${id}/activate`, {});
  }

  /**
   * Deactivate product
   */
  deactivate(id: number): Observable<ApiResponse<Product>> {
    return this.apiService.patch<ApiResponse<Product>>(`${this.endpoint}/${id}/deactivate`, {});
  }

  /**
   * Update product quantity
   */
  updateQuantity(id: number, quantity: number, version: number): Observable<ApiResponse<Product>> {
    return this.apiService.patch<ApiResponse<Product>>(`${this.endpoint}/${id}/quantity`, {
      quantity,
      version,
    });
  }

  /**
   * Search products
   */
  search(query: string): Observable<ApiResponse<Product[]>> {
    return this.apiService.get<ApiResponse<Product[]>>(`${this.endpoint}/search`, { q: query });
  }

  /**
   * Get low stock products
   */
  getLowStock(threshold: number = 10): Observable<ApiResponse<Product[]>> {
    return this.apiService.get<ApiResponse<Product[]>>(`${this.endpoint}/low-stock`, { threshold });
  }

  /**
   * Export products to CSV
   */
  exportToCsv(params?: ProductFilterParams): Observable<Blob> {
    return this.apiService.get<Blob>(`${this.endpoint}/export/csv`, params);
  }
}
