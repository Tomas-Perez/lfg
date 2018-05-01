import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
export class DbPost {
  @JsonProperty('description', String)
  description: string = undefined;
  @JsonProperty('activityID', Number, true)
  activityID?: number = undefined;
  @JsonProperty('ownerID', Number, true)
  ownerID?: number = undefined;
  @JsonProperty('groupID', Number, true)
  groupID?: number = undefined;
}
