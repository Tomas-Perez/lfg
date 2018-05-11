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
  @JsonProperty('id', Number)
  id: number = undefined;
  @JsonProperty('activity', DbActivity, true)
  activity?: DbActivity = undefined;
  @JsonProperty('slots', Number)
  slots: number = undefined;
  @JsonProperty('owner', Member)
  owner: Member = undefined;
  @JsonProperty('members', [Member])
  members: Member[] = undefined;
}


