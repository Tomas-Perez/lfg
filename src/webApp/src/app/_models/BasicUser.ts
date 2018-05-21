import {JsonObject, JsonProperty} from 'json2typescript';


@JsonObject
export class BasicUser {

  @JsonProperty('id', Number)
  id: number = undefined;

  @JsonProperty('username', String)
  username: string = undefined;

}
