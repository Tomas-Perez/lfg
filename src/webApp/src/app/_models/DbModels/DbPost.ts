import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
export class DbPost {

  @JsonProperty('description', String, true)
  description?: string = undefined;
  @JsonProperty('activityID', Number, true)
  activityID?: number = undefined;
  @JsonProperty('ownerID', Number, true)
  ownerID?: number = undefined;
  @JsonProperty('groupID', Number, true)
  groupID?: number = undefined;
  @JsonProperty('gamePlatforms', [Number], true)
  gamePlatforms?: number[] = undefined;
  @JsonProperty('chatPlatforms', [Number], true)
  chatPlatforms?: number[] = undefined;

  constructor() {
    this.description = '';
  }

}
