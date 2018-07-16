import {DateConverter} from './JsonConverters/DateConverter';
import {JsonObject, JsonProperty} from 'json2typescript';
import {BasicUser} from './BasicUser';

@JsonObject
export class Message {
  @JsonProperty('id', Number)
  id: number = undefined;

  @JsonProperty('sender', BasicUser)
  sender: BasicUser = undefined;

  @JsonProperty('message', String)
  message: string = undefined;

  @JsonProperty('date', DateConverter)
  date: Date = undefined;
}
