import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
export class User {

  @JsonProperty('id', Number, true)
  id?: number = undefined;

  @JsonProperty('username', String)
  username: string = undefined;

  @JsonProperty('email', String)
  email: string = undefined;

  @JsonProperty('password', String, true)
  password?: string = undefined;

  @JsonProperty('admin', Boolean, true)
  admin?: boolean = undefined;
}
