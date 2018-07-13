import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {PlatformLocation} from '@angular/common';

@Injectable()
export class HttpService {

    private DEFAULT_HEADERS = {'Content-Type': 'application/json'}; // in content-type-interceptor
    // private _authToken: string; // in auth service

    private baseUrl: string;

  /*
    get authToken(): string {
      if (!this._authToken ) {
        this._authToken = this.cookieService.get('4UtH-t0k3n');
      }
      return this._authToken;
    }
    set authToken(value: string) { this._authToken = value; }
    */

    get defaultHeaders(): HttpHeaders { return new HttpHeaders(this.DEFAULT_HEADERS); }
    get defaultOptions(): any { return { headers: this.defaultHeaders }; }

    get defaultHttp(): HttpClient { return this._http; }

    constructor(private _http: HttpClient,
                private platformLocation: PlatformLocation
                ) {
      const loc = (platformLocation as any).location;
      this.baseUrl = 'http://' + loc.hostname + ':8080/lfg'; // get base url
      console.log(this.baseUrl);
    }

    public get(url: string, options: any): Observable<any> {
        return this._http.get(this.baseUrl + url, options);
    }

    public post(url: string, body: any, options: any): Observable<any> {
        return (this._http.post(this.baseUrl + url, body, options));
    }

    public put(url: string, body: any): Observable<any> {
        return (this._http.put(this.baseUrl + url, body, this.requestOptions()));
    }

    public delete(url: string): Observable<any> {
        return (this._http.delete(this.baseUrl + url, this.requestOptions()));
    }

    public patch(url: string, body: any): Observable<any> {
        return (this._http.patch(this.baseUrl + url, body, this.requestOptions()));
    }

    public head(url: string): Observable<any> {
        return (this._http.head(this.baseUrl + url, this.requestOptions()));
    }

    public options(url: string): Observable<any> {
        return (this._http.options(this.baseUrl + url, this.requestOptions()));
    }

    private requestOptions() {
        // const authHeader = { Authorization: this.authToken } ;
        return {
            headers: new HttpHeaders(Object.assign(this.DEFAULT_HEADERS /*, authHeader*/ )),
        };
    }

}
