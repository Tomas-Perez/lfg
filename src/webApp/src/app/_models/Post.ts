import {Activity} from './Activity';
import {JsonObject, JsonProperty} from 'json2typescript';

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
  @JsonProperty('activity', Activity)
  activity: Activity = undefined;
  @JsonProperty('date', Date)
  date: Date = undefined;
  @JsonProperty('owner', Owner)
  owner: Owner = undefined;
  @JsonProperty('description', String)
  description: string = undefined;
}


