// src/app/models/api-response.model.ts
export type ApiResponse<T> = T;

export interface PaginatedResponse<T> {
  content: T[]; // the actual items
  totalElements: number; // total number of items
  number: number; // current page number (0-based)
  size: number; // page size
  totalPages: number; // total pages
  // Optional: you can include other fields if needed
  first?: boolean;
  last?: boolean;
  numberOfElements?: number;
}
