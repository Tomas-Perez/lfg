import { Injectable } from '@angular/core';
import {SpekbarLocation} from '../_models/SpekbarLocation';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Injectable()
export class NavBarService {

  private spekbarLocation: SpekbarLocation;
  spekbarLocationSubject: BehaviorSubject<SpekbarLocation>;

  constructor() {
    this.spekbarLocationSubject = new BehaviorSubject<SpekbarLocation>(SpekbarLocation.NOTHING);
    this.spekbarLocationSubject.subscribe(spekbarLocation => this.spekbarLocation = spekbarLocation);
  }

}
