import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
class GroupsId {
  @JsonProperty('id', Number)
  id: number = undefined;
}

@JsonObject
class PostId {
  @JsonProperty('id', Number)
  id: number = undefined;
}

@JsonObject
class ChatId {
  @JsonProperty('id', Number)
  id: number = undefined;
}

@JsonObject
export class User {

  @JsonProperty('id', Number, true)
  id?: number = undefined;

  @JsonProperty('username', String)
  username: string = undefined;

  @JsonProperty('email', String)
  email: string = undefined;

  @JsonProperty('password', String, true)
  password?: string = undefined;

  @JsonProperty('admin', Boolean, true)
  admin?: boolean = undefined;

  @JsonProperty('groups', [GroupsId], true)
  groups?: GroupsId[] = undefined;

  @JsonProperty('post', PostId, true)
  post?: PostId = undefined;

  @JsonProperty('chats', [ChatId], true)
  chats?: ChatId[] = undefined;

  image: any = null;
}
