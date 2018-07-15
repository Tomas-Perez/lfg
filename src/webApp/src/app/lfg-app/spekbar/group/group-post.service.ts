import { Injectable } from '@angular/core';
import {DbPost} from '../../../_models/DbModels/DbPost';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {UserService} from '../../../_services/user.service';

@Injectable()
export class GroupPostService {

  post: DbPost;
  postSubject: BehaviorSubject<DbPost>;

  constructor(private userService: UserService) {
    this.post = new DbPost();
    this.postSubject = new BehaviorSubject(this.post);
    this.userService.userSubject.subscribe(user => {
      this.post = new DbPost();
      this.postSubject.next(this.post);
    });
  }

  updatePost(post: DbPost) {
    this.post = post;
    this.postSubject.next(post);
  }

}
