import {JsonConverter, JsonCustomConvert, JsonObject, JsonProperty} from 'json2typescript';
import {DbActivity} from './DbModels/DbActivity';
import {Group} from './Group';

@JsonConverter
class DateConverter implements JsonCustomConvert<Date> {
    serialize(date: Date): any {
        return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" +  date.getDate();
    }
    deserialize(date: any): Date {
        return new Date(date);
    }
}

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
}
