import {JsonObject, JsonProperty} from 'json2typescript';
import {DbActivity} from './DbModels/DbActivity';

@JsonObject
class Member {
  @JsonProperty('id', Number)
  id: number = undefined;
  @JsonProperty('username', String)
  username: string = undefined;
}

@JsonObject
export class Group {
  @JsonProperty('id', Number, true)
  id?: number = undefined;
  @JsonProperty('activity', DbActivity, true)
  activity?: DbActivity = undefined;
  @JsonProperty('slots', Number, true)
  slots?: number = undefined;
  @JsonProperty('owner', Member, true)
  owner?: Member = undefined;
  @JsonProperty('members', [Member], true)
  members?: Member[] = undefined;
}


