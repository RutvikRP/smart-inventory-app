// core/models/user.model.ts
export interface User {
  id: number;
  email: string;
  name: string;
  role?: string;
}
export interface LoginRequest {
  email: string;
  password: string;
}
export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}
export interface AuthResponse {
  token: string;
  user: User;
}

export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
}
