import {JsonObject, JsonProperty} from 'json2typescript';
import {OnlineStatus} from './OnlineStatus';

@JsonObject
export class BasicUser {

  constructor(id?: number, username?: string, status?: OnlineStatus, image?: any) {
    this.id = id || undefined;
    this.username = username || undefined;
    this.status = status || undefined;
    this.image = image || null;
  }

  @JsonProperty('status', OnlineStatus, true)
  status: OnlineStatus = undefined;

  @JsonProperty('id', Number)
  id: number = undefined;

  @JsonProperty('username', String)
  username: string = undefined;

  image: any = null;

}
