import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
export class ChatPlatform {
  @JsonProperty('id', Number)
  id: number = undefined;

  @JsonProperty('name', String, true)
  name: string = undefined;
}
