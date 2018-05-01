import {Injectable, Injector} from '@angular/core';
import {
  HttpInterceptor, HttpHandler, HttpRequest
} from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {Router} from '@angular/router';
import {AuthService} from '../_services/auth.service';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';
import 'rxjs/add/observable/empty';
import 'rxjs/add/observable/throw';

/** Pass untouched request through to the next request handler. */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  authService;
  refreshTokenInProgress = false;

  tokenRefreshedSource = new Subject();
  tokenRefreshed$ = this.tokenRefreshedSource.asObservable();

  constructor(private injector: Injector, private router: Router) {}

  addAuthHeader(request) {
    const token = this.authService.getAccessToken();
    if (token !== null) {
      return request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    return request;
  }

  refreshToken() {
    if (this.refreshTokenInProgress) {
      return new Observable(observer => {
        this.tokenRefreshed$.subscribe(() => {
          observer.next();
          observer.complete();
        });
      });
    } else {
      this.refreshTokenInProgress = true;

      return this.authService.refreshToken()
        .do(() => {
          this.refreshTokenInProgress = false;
          this.tokenRefreshedSource.next();
        });
    }
  }

  logout() {
    this.authService.logout();
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<any> {
    if (request.url === AuthService.authUrl) {
      next.handle(request);
    }

    this.authService = this.injector.get(AuthService);
    request = this.addAuthHeader(request);
    console.log('intercepted request:');
    console.log(request);

    // Handle response
    return next.handle(request).catch(error => {
      if (error.status === 401) {
        return this.refreshToken()
          .switchMap(() => {
            request = this.addAuthHeader(request);
            return next.handle(request);
          })
          .catch(() => {
            this.logout();
            return Observable.empty();
          });
      }
      return Observable.throw(error);
    });
  }

}
