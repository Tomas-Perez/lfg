import {JsonObject, JsonProperty} from 'json2typescript';
import {DbActivity} from './DbActivity';

@JsonObject
class Owner {
  @JsonProperty('id', Number)
  id: number = undefined;
  @JsonProperty('name', String)
  name: string = undefined;
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
}
