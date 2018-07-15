import {DbPost} from '../../../_models/DbModels/DbPost';

export class NewPostModel {
  dbPost: DbPost;
  selectedGameIndex: number;
  selectedActivityIndex: number;
  selectedGamePlatformIndex: number;
  selectedChatPlatformIndex: number;

  constructor(newPostModel?) {
    newPostModel = newPostModel || {};
    this.dbPost = newPostModel.dbPost || new DbPost();
    this.selectedGameIndex = newPostModel.selectedGameIndex || null;
    this.selectedActivityIndex = newPostModel.selectedActivityIndex || null;
    this.selectedGamePlatformIndex = newPostModel.selectedGamePlatformIndex || null;
    this.selectedChatPlatformIndex = newPostModel.selectedChatPlatformIndex || -1;
  }

}
