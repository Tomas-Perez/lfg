import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {AuthService} from '../auth.service';
import {map} from 'rxjs/operators';

@Injectable()
export class UnauthUserGuardService implements CanActivate {

  constructor(private authService: AuthService, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.authService.isLoggedIn().take(1).pipe(
      map(
        b => {
          if (b) {
            this.router.navigate(['/home']);
            return false;
          } else {
            return true;
          }
        }
      )
    );
  }

}
