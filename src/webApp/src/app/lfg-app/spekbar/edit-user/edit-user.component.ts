import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavBarService} from '../../_services/nav-bar.service';
import {SpekbarLocation} from '../../_models/SpekbarLocation';
import {Subject} from 'rxjs/Subject';
import {UserService} from '../../../_services/user.service';
import {User} from '../../../_models/User';
import {FileHolder} from 'angular2-image-upload';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css', '../spekbar.css']
})
export class EditUserComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  user: User;
  file: FileHolder;
  imgState: boolean;

  constructor(
    private userService: UserService,
    private navBarService: NavBarService
  ) { }

  ngOnInit() {
    this.imgState = false;
    this.file = null;

    this.navBarService.spekbarLocationSubject.next(SpekbarLocation.EDITUSER);

    this.userService.userSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(user => {
        this.user = user;
      });
  }

  onUploadFinished(file: FileHolder) {
    this.imgState = true;
    this.file = file;
  }

  onRemoved(file: FileHolder) {
    this.imgState = false;
    this.file = null;
  }

  onUpload() {
    if (this.file == null) {
      return;
    }
    // TODO upload file (use userService)
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
