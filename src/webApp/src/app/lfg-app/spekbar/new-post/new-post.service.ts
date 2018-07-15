import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {NewPostModel} from './new-post.model';
import {UserService} from '../../../_services/user.service';

@Injectable()
export class NewPostService {

  newPostModel: NewPostModel;
  postSubject: BehaviorSubject<NewPostModel>;

  constructor(private userService: UserService) {
    this.newPostModel = new NewPostModel();
    this.postSubject = new BehaviorSubject(this.newPostModel);
    this.userService.userSubject.subscribe(user => {
      this.newPostModel = new NewPostModel();
      this.postSubject.next(this.newPostModel);
    });
  }

  updatePost(newPostModel: NewPostModel) {
    this.newPostModel = newPostModel;
    this.postSubject.next(newPostModel);
  }



}
