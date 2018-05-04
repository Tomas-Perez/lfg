import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {AuthService} from '../auth.service';
import {map} from 'rxjs/operators';

@Injectable()
export class AdminGuardService implements CanActivate {

  constructor(private authService: AuthService,
              private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.authService.authAdmin()
      .pipe(
        map(
          isAdmin => {
            if (isAdmin) {
              return true;
            } else {
              this.router.navigate(['/home']); // TODO reroute to not found
              return false;
            }
          }
        )
    );
  }

}
