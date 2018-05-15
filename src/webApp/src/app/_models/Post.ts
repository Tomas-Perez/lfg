import {JsonObject, JsonProperty} from 'json2typescript';
import {DbActivity} from './DbModels/DbActivity';

@JsonObject
class Owner {
  @JsonProperty('id', Number)
  id: number = undefined;
  @JsonProperty('username', String)
  username: string = undefined;
}

@JsonObject
class PostGroup {
  @JsonProperty('id', Number)
  id: number = undefined;
}

@JsonObject
export class Post {
  @JsonProperty('id', Number)
  id: number = undefined;
  @JsonProperty('activity', DbActivity)
  activity: DbActivity = undefined;
  @JsonProperty('date', String) // TODO date converter
  date: string = undefined;
  @JsonProperty('owner', Owner)
  owner: Owner = undefined;
  @JsonProperty('description', String)
  description: string = undefined;
  @JsonProperty('group', PostGroup, true)
  group: PostGroup = undefined;
}
