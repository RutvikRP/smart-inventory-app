// core/guards/auth.guard.ts
import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * AUTH GUARD (Functional Style - Angular 16+)
 *
 * PURPOSE:
 * Protects routes from unauthorized access
 *
 * FLOW:
 * User tries to access /dashboard
 *   ↓
 * Guard checks: Is user logged in?
 *   ↓
 * YES → Allow access ✅
 * NO  → Redirect to login ❌
 */
export const authGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  console.log('🛡️ Auth Guard checking access to:', state.url);

  // Check if user is authenticated
  if (authService.isAuthenticated()) {
    // Check if token is expired
    if (authService.isTokenExpired()) {
      console.log('⏰ Token expired, redirecting to login');
      authService.logout();
      return router.createUrlTree(['/login'], {
        queryParams: { returnUrl: state.url },
      });
    }

    console.log('✅ Access granted');
    return true; // Allow access
  }

  // User not logged in
  console.log('🚫 Access denied, redirecting to login');

  // Redirect to login with return URL
  // After login, user will be redirected back to original page
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url },
  });
};

/**
 * ROLE-BASED AUTH GUARD
 * Protects routes based on user role (Admin, User, Manager, etc.)
 */
export const roleGuard: (allowedRoles: string[]) => CanActivateFn =
  (allowedRoles: string[]) => (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    console.log('🛡️ Role Guard checking access to:', state.url);
    console.log('Required roles:', allowedRoles);

    // Check if user is authenticated
    if (!authService.isAuthenticated()) {
      console.log('🚫 Not authenticated');
      return router.createUrlTree(['/login']);
    }

    // Check user role
    const user = authService.currentUser();
    const userRole = user?.role;

    console.log('User role:', userRole);

    if (userRole && allowedRoles.includes(userRole)) {
      console.log('✅ Role authorized');
      return true;
    }

    console.log('🚫 Insufficient permissions');
    return router.createUrlTree(['/access-denied']);
  };

/**
 * GUEST GUARD
 * Prevents logged-in users from accessing login/register pages
 */
export const guestGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  console.log('🛡️ Guest Guard checking:', state.url);

  // If user is already logged in, redirect to dashboard
  if (authService.isAuthenticated()) {
    console.log('✅ User already logged in, redirecting to dashboard');
    return router.createUrlTree(['/dashboard']);
  }

  console.log('✅ Access granted to guest page');
  return true; // Allow access
};

/* ========================================
 * ALTERNATIVE: CLASS-BASED GUARD (Older Style)
 * Use this if you're on Angular < 15
 * ========================================
 *
 * import { Injectable } from '@angular/core';
 * import { CanActivate, Router } from '@angular/router';
 *
 * @Injectable({ providedIn: 'root' })
 * export class AuthGuard implements CanActivate {
 *   constructor(
 *     private authService: AuthService,
 *     private router: Router
 *   ) {}
 *
 *   canActivate(): boolean {
 *     if (this.authService.isAuthenticated()()) {
 *       return true;
 *     }
 *     this.router.navigate(['/login']);
 *     return false;
 *   }
 * }
 *
 * ========================================
 */

/* ========================================
 * USAGE IN ROUTES (app.routes.ts)
 * ========================================
 *
 * import { Routes } from '@angular/router';
 * import { authGuard, roleGuard, guestGuard } from './core/guards/auth.guard';
 *
 * export const routes: Routes = [
 *   // Public routes (anyone can access)
 *   {
 *     path: 'login',
 *     component: LoginComponent,
 *     canActivate: [guestGuard] // Redirect if already logged in
 *   },
 *   {
 *     path: 'register',
 *     component: RegisterComponent,
 *     canActivate: [guestGuard]
 *   },
 *
 *   // Protected routes (must be logged in)
 *   {
 *     path: 'dashboard',
 *     component: DashboardComponent,
 *     canActivate: [authGuard] // ✅ Protected
 *   },
 *   {
 *     path: 'products',
 *     component: ProductListComponent,
 *     canActivate: [authGuard] // ✅ Protected
 *   },
 *
 *   // Admin-only routes
 *   {
 *     path: 'admin',
 *     component: AdminPanelComponent,
 *     canActivate: [authGuard, roleGuard(['ADMIN'])] // ✅ Only admins
 *   },
 *
 *   // Multiple roles allowed
 *   {
 *     path: 'reports',
 *     component: ReportsComponent,
 *     canActivate: [authGuard, roleGuard(['ADMIN', 'MANAGER'])]
 *   },
 *
 *   // Default redirect
 *   { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
 *   { path: '**', redirectTo: '/dashboard' } // 404 → dashboard
 * ];
 *
 * ========================================
 */

/* ========================================
 * ADVANCED: CHILD ROUTE PROTECTION
 * ========================================
 *
 * Protect all child routes at once:
 *
 * {
 *   path: 'app',
 *   canActivate: [authGuard], // Protects all children
 *   children: [
 *     { path: 'dashboard', component: DashboardComponent },
 *     { path: 'products', component: ProductListComponent },
 *     { path: 'orders', component: OrdersComponent }
 *   ]
 * }
 *
 * ========================================
 */

/* ========================================
 * TESTING THE GUARD
 * ========================================
 *
 * 1. Log out
 * 2. Try to access http://localhost:4200/dashboard
 * 3. Should redirect to /login
 * 4. After login, should redirect back to /dashboard
 *
 * ========================================
 */
