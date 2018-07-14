import { Injectable } from '@angular/core';
import {PlatformLocation} from '@angular/common';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class WsService {

  private baseUrl: string;

  constructor(private _http: HttpClient,
              private platformLocation: PlatformLocation
  ) {
    const loc = (platformLocation as any).location;
    this.baseUrl = 'ws://' + loc.hostname + ':8080/lfg/websockets'; // get base url
  }

  getUrl(path: string) {
    return this.baseUrl + path;
  }

}
