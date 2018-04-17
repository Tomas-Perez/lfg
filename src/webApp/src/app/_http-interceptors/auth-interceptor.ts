import {Injectable, Injector} from '@angular/core';
import {
  HttpInterceptor, HttpHandler, HttpRequest
} from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {Router} from '@angular/router';
import {AuthService} from '../_services/auth.service';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/empty';

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
    if (token) {
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
    this.authService = this.injector.get(AuthService);

    console.log('intercepted request:');
    console.log(request);

    // Handle request
    request = this.addAuthHeader(request);


    console.log('Added headers:');
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
/*
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authReq = this.authenticateRequest(request);
    console.log('*Updated httpRequest*', authReq);
    return next.handle(authReq)
      .do((event: HttpEvent<any>) => {
      if (event instanceof HttpResponse) {
        // do stuff with response if you want
      }
    }, (err: any) => {
      if (err instanceof HttpErrorResponse {
        if (err.status === 401) {
          this.authService.collectFailedRequests(request);
        }
      }
    });
  }

*/


/*

intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('*An intercepted httpRequest*', req, AuthService.getAccessToken());
    const authReq = this.authenticateRequest(req);
    console.log('*Updated httpRequest*', authReq);
    return next.handle(authReq)
      .map((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {
          console.log('*An intercepted httpResponse*', event);
          return event;
        }
      })
      .catch((error: any) => {
        if (error instanceof HttpErrorResponse) {
          if (error.status === 403 && error.url !== this.authService.getAuthUrl()) {
            return this.authService
              .refreshToken()
              .flatMap((token) => {
                const authReqRepeat = this.authenticateRequest(req);
                console.log('*Repeating httpRequest*', authReqRepeat);
                return next.handle(authReqRepeat);
              });
          }
        } else {
          return Observable.throw(error);
        }
      });
  }

 */






/*

intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  return next.handle(req).do((event: HttpEvent<any>) => {
    if (event instanceof HttpResponse) {
      // do stuff with response if you want
    }
  }, (err: any) => {
    if (err instanceof HttpErrorResponse {
      if (err.status === 401) {
        this.auth.collectFailedRequest(request);
      }
    }
  });

*/



/*
request.clone({
      setHeaders: {
        Authorization: `Bearer ${this.auth.getToken()}`
      }
    });

authenticateRequest(req)//adds header to the request
obtainAccessToken()//gets fresh token from server and saves it
 */
