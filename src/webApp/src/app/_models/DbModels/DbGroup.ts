import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
export class DbGroup {
  @JsonProperty('activityID', Number, true)
  activityID?: number = undefined;
  @JsonProperty('ownerID', Number)
  ownerID: number = undefined;
  @JsonProperty('slots', Number)
  slots: number = undefined;
  @JsonProperty('gamePlatform', Number, true)
  gamePlatform?: number = undefined;
  @JsonProperty('chatPlatform', Number, true)
  chatPlatform?: number = undefined;

  constructor() { }
}
