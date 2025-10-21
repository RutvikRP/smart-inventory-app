// core/services/auth.service.ts
import { Injectable, signal, computed } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { User, LoginRequest, RegisterRequest, AuthResponse } from '../models/auth-response.model';

@Injectable({
  providedIn: 'root', // Singleton - one instance for entire app
})
export class AuthService {
  // API endpoints
  private readonly API_URL = 'http://localhost:8080';
  private readonly AUTH_ENDPOINTS = {
    LOGIN: `${this.API_URL}/auth/login`,
    REGISTER: `${this.API_URL}/auth/register`,
    LOGOUT: `${this.API_URL}/auth/logout`,
  };

  // Storage keys (to avoid typos)
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'user_data';

  // ========== SIGNALS (Modern Angular State Management) ==========

  // Signal: Reactive value that auto-updates UI when changed
  private currentUserSignal = signal<User | null>(null);

  // Computed: Derived state - automatically recalculates
  isAuthenticated = computed(() => this.currentUserSignal() !== null);
  currentUser = computed(() => this.currentUserSignal());

  // ========== ALTERNATIVE: BehaviorSubject (RxJS approach) ==========
  // Use this if you prefer Observables over Signals
  // private currentUserSubject = new BehaviorSubject<User | null>(null);
  // public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // On app start, check if user is already logged in
    this.loadUserFromStorage();
  }

  // ==================== PUBLIC METHODS ====================

  /**
   * LOGIN
   * Sends credentials to backend, receives JWT token
   */
  login(credentials: LoginRequest): Observable<AuthResponse> {
    console.log('🔑 Attempting login for:', credentials.email);

    return this.http.post<AuthResponse>(this.AUTH_ENDPOINTS.LOGIN, credentials).pipe(
      tap((response) => {
        console.log('✅ Login successful:', response);
        this.handleAuthSuccess(response);
      }),
      catchError((error) => this.handleAuthError(error, 'Login'))
    );
  }

  /**
   * REGISTER
   * Creates new user account
   */
  register(userData: RegisterRequest): Observable<AuthResponse> {
    console.log('📝 Registering new user:', userData.email);

    return this.http.post<AuthResponse>(this.AUTH_ENDPOINTS.REGISTER, userData).pipe(
      tap((response) => {
        console.log('✅ Registration successful');
        this.handleAuthSuccess(response);
      }),
      catchError((error) => this.handleAuthError(error, 'Registration'))
    );
  }

  /**
   * LOGOUT
   * Clears token and user data, redirects to login
   */
  logout(): void {
    console.log('👋 Logging out user:', this.currentUser()?.email);

    // Optional: Call backend logout endpoint
    // this.http.post(this.AUTH_ENDPOINTS.LOGOUT, {}).subscribe();

    // Clear storage
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);

    // Clear state
    this.currentUserSignal.set(null);

    // Redirect to login page
    this.router.navigate(['/login']);

    console.log('✅ Logout complete');
  }

  /**
   * GET TOKEN
   * Returns the stored JWT token
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * IS TOKEN EXPIRED
   * Checks if JWT token is still valid
   */
  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    try {
      // JWT structure: header.payload.signature
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp;
      const now = Math.floor(Date.now() / 1000);

      return now >= expiry;
    } catch (error) {
      console.error('❌ Error decoding token:', error);
      return true;
    }
  }

  /**
   * GET USER FROM TOKEN
   * Extracts user info from JWT payload
   */
  getUserFromToken(): User | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return {
        id: payload.userId,
        email: payload.sub, // 'sub' is standard JWT claim for user identifier
        name: payload.name,
        role: payload.role,
      };
    } catch (error) {
      console.error('❌ Error extracting user from token:', error);
      return null;
    }
  }

  // ==================== PRIVATE HELPER METHODS ====================

  /**
   * HANDLE AUTH SUCCESS
   * Called after successful login/register
   */
  private handleAuthSuccess(response: AuthResponse): void {
    // 1. Store token in localStorage (persists across browser sessions)
    localStorage.setItem(this.TOKEN_KEY, response.token);

    // 2. Store user data
    localStorage.setItem(this.USER_KEY, JSON.stringify(response.user));

    // 3. Update app state
    this.currentUserSignal.set(response.user);

    // 4. Navigate to dashboard
    this.router.navigate(['/dashboard']);
  }

  /**
   * LOAD USER FROM STORAGE
   * Called on app startup to restore login state
   */
  private loadUserFromStorage(): void {
    const token = this.getToken();
    const userJson = localStorage.getItem(this.USER_KEY);

    if (token && !this.isTokenExpired() && userJson) {
      try {
        const user: User = JSON.parse(userJson);
        this.currentUserSignal.set(user);
        console.log('✅ User session restored:', user.email);
      } catch (error) {
        console.error('❌ Failed to restore user session:', error);
        this.logout();
      }
    } else {
      console.log('ℹ️ No valid session found');
      // Clear any stale data
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
  }

  /**
   * HANDLE AUTH ERROR
   * Standardized error handling
   */
  private handleAuthError(error: HttpErrorResponse, action: string): Observable<never> {
    console.error(`❌ ${action} failed:`, error);

    let errorMessage = 'An unknown error occurred';

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      switch (error.status) {
        case 401:
          errorMessage = 'Invalid credentials. Please check your email and password.';
          break;
        case 403:
          errorMessage = 'Access forbidden. You do not have permission.';
          break;
        case 404:
          errorMessage = 'Service not found. Please contact support.';
          break;
        case 500:
          errorMessage = 'Server error. Please try again later.';
          break;
        default:
          errorMessage = error.error?.message || `Error: ${error.status}`;
      }
    }

    return throwError(() => new Error(errorMessage));
  }
}

/* ========================================
 * HOW TO USE THIS SERVICE
 * ========================================
 *
 * 1. LOGIN EXAMPLE:
 *
 *    constructor(private authService: AuthService) {}
 *
 *    onLogin() {
 *      this.authService.login({ email: 'user@email.com', password: '123456' })
 *        .subscribe({
 *          next: () => console.log('Login successful'),
 *          error: (err) => console.error(err.message)
 *        });
 *    }
 *
 * 2. CHECK IF LOGGED IN:
 *
 *    isLoggedIn = this.authService.isAuthenticated(); // Returns signal
 *
 *    In template: <div *ngIf="isLoggedIn()">Welcome!</div>
 *
 * 3. GET CURRENT USER:
 *
 *    currentUser = this.authService.currentUser(); // Returns signal
 *
 *    In template: <p>{{ currentUser()?.name }}</p>
 *
 * 4. LOGOUT:
 *
 *    this.authService.logout();
 *
 * ========================================
 */
