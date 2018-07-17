import {JsonObject, JsonProperty} from 'json2typescript';
import {Activity} from './Activity';

@JsonObject
export class Game {
  @JsonProperty('id', Number, true)
  id?: number = undefined;
  @JsonProperty('name', String, true)
  name?: string = undefined;
  @JsonProperty('activities', [Activity], true)
  activities?: Activity[] = undefined;
}
