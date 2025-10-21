// core/interceptors/auth.interceptor.ts
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

/**
 * JWT AUTH INTERCEPTOR (Functional Style - Angular 16+)
 *
 * PURPOSE:
 * Automatically attaches JWT token to all outgoing HTTP requests
 *
 * FLOW:
 * 1. Request goes out → Interceptor catches it
 * 2. Checks if token exists
 * 3. Adds "Authorization: Bearer <token>" header
 * 4. Sends request to backend
 * 5. If 401 error → logout user (token expired/invalid)
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Get JWT token from AuthService
  const token = authService.getToken();

  // ============ SKIP TOKEN FOR PUBLIC ENDPOINTS ============
  // Don't add token to login/register requests (they don't need it)
  const publicEndpoints = ['/auth/login', '/auth/register'];
  const isPublicEndpoint = publicEndpoints.some((endpoint) => req.url.includes(endpoint));

  if (isPublicEndpoint) {
    console.log('🌐 Public endpoint, skipping token:', req.url);
    return next(req); // Pass request as-is
  }

  // ============ ATTACH TOKEN IF AVAILABLE ============
  let clonedRequest = req;

  if (token) {
    console.log('🔐 Attaching JWT token to request:', req.url);

    // Clone request and add Authorization header
    // WHY CLONE? HTTP requests are immutable in Angular
    clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
        // Your Spring Boot expects: Authorization: Bearer <jwt_token>
      },
    });
  } else {
    console.log('⚠️ No token available for request:', req.url);
  }

  // ============ SEND REQUEST & HANDLE ERRORS ============
  return next(clonedRequest).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error('❌ HTTP Error:', error.status, error.message);

      // Handle 401 Unauthorized (token expired/invalid)
      if (error.status === 401) {
        console.log('🚫 Unauthorized! Token may be expired. Logging out...');

        // Auto-logout user
        authService.logout();

        // Redirect to login page
        router.navigate(['/login'], {
          queryParams: { returnUrl: router.url },
        });
      }

      // Handle 403 Forbidden (insufficient permissions)
      if (error.status === 403) {
        console.log('🚫 Forbidden! User lacks permissions');
        router.navigate(['/access-denied']);
      }

      return throwError(() => error);
    })
  );
};

/* ========================================
 * ALTERNATIVE: CLASS-BASED INTERCEPTOR (Older Style)
 * Use this if you're on Angular < 15
 * ========================================
 *
 * import { Injectable } from '@angular/core';
 * import {
 *   HttpRequest,
 *   HttpHandler,
 *   HttpEvent,
 *   HttpInterceptor
 * } from '@angular/common/http';
 * import { Observable } from 'rxjs';
 *
 * @Injectable()
 * export class AuthInterceptor implements HttpInterceptor {
 *   constructor(private authService: AuthService) {}
 *
 *   intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
 *     const token = this.authService.getToken();
 *
 *     if (token) {
 *       req = req.clone({
 *         setHeaders: { Authorization: `Bearer ${token}` }
 *       });
 *     }
 *
 *     return next.handle(req);
 *   }
 * }
 *
 * ========================================
 */

/* ========================================
 * HOW THIS HELPS YOU
 * ========================================
 *
 * WITHOUT INTERCEPTOR:
 *
 * getProducts() {
 *   const headers = new HttpHeaders({
 *     'Authorization': `Bearer ${this.authService.getToken()}`
 *   });
 *   return this.http.get('/api/products', { headers });
 * }
 *
 * getUsers() {
 *   const headers = new HttpHeaders({
 *     'Authorization': `Bearer ${this.authService.getToken()}`
 *   });
 *   return this.http.get('/api/users', { headers });
 * }
 *
 * // You have to repeat this for EVERY API call! 😫
 *
 * ----------------------------------------
 *
 * WITH INTERCEPTOR:
 *
 * getProducts() {
 *   return this.http.get('/api/products');
 * }
 *
 * getUsers() {
 *   return this.http.get('/api/users');
 * }
 *
 * // Token is added automatically! 🎉
 *
 * ========================================
 */

/* ========================================
 * CONFIGURATION IN app.config.ts
 * ========================================
 *
 * import { ApplicationConfig } from '@angular/core';
 * import { provideHttpClient, withInterceptors } from '@angular/common/http';
 * import { authInterceptor } from './core/interceptors/auth.interceptor';
 *
 * export const appConfig: ApplicationConfig = {
 *   providers: [
 *     provideHttpClient(
 *       withInterceptors([authInterceptor]) // Register interceptor
 *     )
 *   ]
 * };
 *
 * ========================================
 */
