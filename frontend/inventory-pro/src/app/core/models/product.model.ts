export enum UnitOfMeasure {
  PCS = 'PCS',
  KG = 'KG',
  LITER = 'LITER',
  GRAM = 'GRAM',
  METER = 'METER',
  BOX = 'BOX',
  DOZEN = 'DOZEN',
  PACK = 'PACK',
}

export interface Product {
  id: number;
  name: string;
  sku: string;
  price: number;
  quantity: number;
  description?: string;
  uom: UnitOfMeasure;
  createdAt: string;
  updatedAt: string;
  active: boolean;
  deletedAt?: string;
  version: number;
}

export interface CreateProductDto {
  name: string;
  sku: string;
  price: number;
  quantity?: number;
  description?: string;
  uom: UnitOfMeasure;
}

export interface UpdateProductDto {
  name?: string;
  sku?: string;
  price?: number;
  quantity?: number;
  description?: string;
  uom?: UnitOfMeasure;
  active?: boolean;
}

export interface ProductFilterParams {
  page?: number;
  size?: number;
  sort?: string;
  name?: string;
  sku?: string;
  uom?: UnitOfMeasure;
  active?: boolean;
  minPrice?: number;
  maxPrice?: number;
  minQuantity?: number;
  maxQuantity?: number;
}
