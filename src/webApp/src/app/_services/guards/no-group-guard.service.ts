import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {GroupService} from '../group.service';
import {Observable} from 'rxjs/Observable';
import {map} from 'rxjs/operators';

@Injectable()
export class NoGroupGuardService implements CanActivate {

  constructor(private groupService: GroupService, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.groupService.currentGroupSubject.asObservable().take(1).pipe(
      map(group => {
        if (group === null) {
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
