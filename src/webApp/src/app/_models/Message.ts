import {DateConverter} from './JsonConverters/DateConverter';
import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
export class Message {
  @JsonProperty('id', Number)
  id: number = undefined;

  @JsonProperty('sender', Number)
  senderId: number = undefined;

  @JsonProperty('message', String)
  message: string = undefined;

  @JsonProperty('date', DateConverter)
  date: Date = undefined;
}
