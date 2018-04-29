import {Injectable} from '@angular/core';
import {
  HttpInterceptor, HttpHandler, HttpRequest
} from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

@Injectable()
export class ContentTypeInterceptor implements HttpInterceptor {

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<any> {
    request = this.addContentTypeHeader(request);
    return next.handle(request);
  }

  private addContentTypeHeader(request: HttpRequest<any>) {
    return request.clone({
        setHeaders: {
          ContentType: 'application/json'
        }
    });
  }
}
