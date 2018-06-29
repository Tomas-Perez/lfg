import {JsonObject, JsonProperty} from 'json2typescript';


@JsonObject
export class BasicUser {

  constructor(id?: number, username?: string) {
    this.id = id || undefined;
    this.username = username || undefined;
  }

  @JsonProperty('id', Number)
  id: number = undefined;

  @JsonProperty('username', String)
  username: string = undefined;

}
