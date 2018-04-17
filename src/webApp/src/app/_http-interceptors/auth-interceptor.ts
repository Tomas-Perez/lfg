import { Injectable } from '@angular/core';
import {
  HttpEvent, HttpInterceptor, HttpHandler, HttpRequest
} from '@angular/common/http';

import { Observable } from 'rxjs/Observable';


/** Pass untouched request through to the next request handler. */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler):
  Observable<HttpEvent<any>> {
    console.log('lamaoooo interceptor over here, howdy')
    return next.handle(req);
  }
}
/*
@Injectable()
export class WebApiInterceptor implements HttpInterceptor {
  constructor(private tokenService: TokenService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('*An intercepted httpRequest*', req, this.tokenService.accessToken);
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
          if (error.status === 403 && error.url !== environment.authEndpoint) {
            return this.tokenService
              .obtainAccessToken()
              .flatMap((token) => {
                const authReqRepeat = this.authenticateRequest(req);
                console.log('*Repeating httpRequest*', authReqRepeat);
                return next.handle(authReqRepeat);
              });
          }
        } else {
          return Observable.throw(error);
        }
      })
  }
}

authenticateRequest(req)//adds header to the request
obtainAccessToken()//gets fresh token from server and saves it
 */
