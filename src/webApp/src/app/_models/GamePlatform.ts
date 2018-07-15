import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
export class GamePlatform {
  @JsonProperty('id', Number)
  id: number = undefined;

  @JsonProperty('name', String, true)
  name: string = undefined;
}
