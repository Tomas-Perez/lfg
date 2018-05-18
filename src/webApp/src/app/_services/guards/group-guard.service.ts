import { Injectable } from '@angular/core';
import {GroupService} from '../group.service';
import {Observable} from 'rxjs/Observable';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import 'rxjs/add/operator/take';
import {map} from 'rxjs/operators';

@Injectable()
export class GroupGuardService implements CanActivate {

  constructor(private groupService: GroupService, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {

    return this.groupService.currentGroupSubject.asObservable().take(1).pipe(
      map(group => {
        if (group !== null) {
          return true;
        } else {
          this.router.navigate(['/app', { outlets: {spekbar: [''] }}], {
            skipLocationChange: true
          });
          return false;
        }
      })
    );

  }
}
/*

    return this.groupService.currentGroupSubject.take(1)(group => {
        if (group !== null) {
          return true;
        } else {
          this.router.navigate(['/app', { outlets: {spekbar: ['new-group'] }}], {
            skipLocationChange: true
          });
          return false;
        }
      });
 */
