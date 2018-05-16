import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {NewPostModel} from './new-post.model';

@Injectable()
export class NewPostService {

  newPostModel: NewPostModel;
  postSubject: BehaviorSubject<NewPostModel>;

  constructor() {
    this.newPostModel = new NewPostModel();
    this.postSubject = new BehaviorSubject(this.newPostModel);
  }

  updatePost(newPostModel: NewPostModel) {
    this.newPostModel = newPostModel;
    this.postSubject.next(newPostModel);
  }

}
