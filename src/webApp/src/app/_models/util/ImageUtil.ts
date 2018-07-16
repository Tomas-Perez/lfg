import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';

/**
 * @author Manuel Pedrozo
 */
export class ImageUtil {

  /**
   * @param {Blob} image image blob
   * @returns {Observable<any>} observable of the resulting image
   */
  static createImageFromBlob(image: Blob): Observable<any> {
    const reader = new FileReader();
    const subject = new Subject();
    reader.addEventListener('load', () => {
      subject.next(reader.result);
    }, false);
    if (image) {
      reader.readAsDataURL(image);
    }
    return subject;
  }


}
