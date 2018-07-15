import {JsonObject, JsonProperty} from 'json2typescript';
import {DbActivity} from './DbModels/DbActivity';
import {Group} from './Group';
import {DateConverter} from './JsonConverters/DateConverter';
import {GamePlatform} from './GamePlatform';
import {ChatPlatform} from './ChatPlatform';

@JsonObject
class Owner {
  @JsonProperty('id', Number)
  id: number = undefined;
  @JsonProperty('username', String)
  username: string = undefined;
}

@JsonObject
export class Post {
  @JsonProperty('id', Number)
  id: number = undefined;
  @JsonProperty('activity', DbActivity)
  activity: DbActivity = undefined;
  @JsonProperty('owner', Owner)
  owner: Owner = undefined;
  @JsonProperty('description', String)
  description: string = undefined;
  @JsonProperty('group', Group, true)
  group?: Group = undefined;
  @JsonProperty('date', DateConverter, true)
  date?: Date = undefined;
  @JsonProperty('gamePlatforms', [GamePlatform], true)
  gamePlatforms?: GamePlatform[] = undefined;
  @JsonProperty('chatPlatforms', [ChatPlatform], true)
  chatPlatforms?: ChatPlatform[] = undefined;
}
