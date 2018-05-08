import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
export class DbGame {

  constructor()
  constructor(name: string)
  constructor(name?: string) {
    this.name = name;
  }

  @JsonProperty('id', Number, true)
  id?: number = undefined;
  @JsonProperty('name', String)
  name: string = undefined;
}
