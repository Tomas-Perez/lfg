import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
export class Activity {
  @JsonProperty('id', Number, true)
  id?: number = undefined;
  @JsonProperty('name', String)
  name: string = undefined;

}
