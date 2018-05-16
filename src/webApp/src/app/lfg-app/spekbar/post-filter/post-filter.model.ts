import {DbPost} from '../../../_models/DbModels/DbPost';

export class PostFilterModel {
  dbPost: DbPost;
  selectedGameIndex: number;

  constructor(newPostModel?) {
    newPostModel = newPostModel || {};
    this.dbPost = newPostModel.dbPost || new DbPost();
    this.selectedGameIndex = newPostModel.selectedGameIndex || -1;
  }

}
