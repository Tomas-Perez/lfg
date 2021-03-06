import {DbGame} from './DbGame';
import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
export class DbActivity {
  @JsonProperty('id', Number, true)
  id?: number = undefined;
  @JsonProperty('name', String)
  name: string = undefined;
  @JsonProperty('game', DbGame, true)
  game?: DbGame = undefined;
  @JsonProperty('gameID', Number, true)
  gameID?: number = undefined;
}
