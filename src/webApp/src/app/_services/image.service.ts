import { Injectable } from '@angular/core';
import {HttpService} from './http.service';
import {Observable} from 'rxjs/Observable';
import {catchError, map, switchMap} from 'rxjs/operators';
import {ImageUtil} from '../_models/util/ImageUtil';

@Injectable()
export class ImageService {

  private defaultImg = '/assets/user/user-default.png';

  constructor(private http: HttpService) {

  }

  getImage(url: string): Observable<any> {
    return this.requestImage(url)
      .pipe(
        switchMap(blob => {
          return ImageUtil.createImageFromBlob(blob);
        }),
        catchError(err => this.handleRequestImageError(err))
      );
  }

  private requestImage(url: string): Observable<any> {
    return this.http.get(url, {  responseType: 'blob' });
  }

  private handleRequestImageError(err): Observable<any> {
    console.log('Image retrieval error');
    console.log(err);
    return Observable.of(this.defaultImg);
  }

  uploadImage(url: string, image: any): Observable<boolean> {
    const formData = new FormData();
    formData.append('file', image);
    return this.http.post(url, formData, {
      observe: 'response'
    })
      .pipe(
        map(response => {
          console.log(response);
          return true;
        }),
        catchError((err: any) => this.handleImageUploadError(err))
      );
  }

  private handleImageUploadError(err): Observable<boolean> {
    console.log('Image upload error');
    console.log(err);
    return Observable.of(false);
  }
}
