import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot, CanActivate,
  Router, RouterStateSnapshot
} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {AuthService} from '../auth.service';
import 'rxjs/add/operator/take';
import {map} from 'rxjs/operators';


@Injectable()
export class AuthUserGuardService implements CanActivate {

  constructor(private authService: AuthService, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.authService.isLoggedIn().take(1).pipe(
      map(
        b => {
          if (b) {
            return true;
          } else {
            this.router.navigate(['/home/sign-in']);
            return false;
          }
        }
      )
    );
  }
}
