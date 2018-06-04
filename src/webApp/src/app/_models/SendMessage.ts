import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
class Payload{
  @JsonProperty('content', String)
  content: string = undefined;

  constructor(content: string){
    this.content = content;
  }
}

@JsonObject
export class SendMessage{
  @JsonProperty('type', String)
  type: string = undefined;
  @JsonProperty('payload', Payload)
  payload: Payload = undefined;

  constructor(type: string, content: string){
    this.type = type;
    this.payload = new Payload(content)
  }
}
