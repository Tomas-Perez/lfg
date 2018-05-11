import { Injectable } from '@angular/core';
import {DbPost} from '../../_models/DbModels/DbPost';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Injectable()
export class GroupPostService {

  post: DbPost;
  postSubject: BehaviorSubject<DbPost>;

  constructor() {
    this.post = new DbPost();
    this.postSubject = new BehaviorSubject(this.post);
  }

  updatePost(post: DbPost) {
    this.post = post;
    this.postSubject.next(post);
  }

}
