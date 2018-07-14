import {JsonObject, JsonProperty} from 'json2typescript';
import {DbActivity} from './DbModels/DbActivity';

@JsonObject
class Member {

  constructor(id?: number, username?: string) {
    this.id = id || undefined;
    this.username = username || undefined;
  }

  @JsonProperty('id', Number)
  id: number = undefined;
  @JsonProperty('username', String)
  username: string = undefined;
}

@JsonObject
export class Group {
  @JsonProperty('id', Number, true)
  id?: number = undefined;
  @JsonProperty('activity', DbActivity, true)
  activity?: DbActivity = undefined;
  @JsonProperty('slots', Number, true)
  slots?: number = undefined;
  @JsonProperty('owner', Member, true)
  owner?: Member = undefined;
  @JsonProperty('members', [Member], true)
  members?: Member[] = undefined;

  addMember(id: number, username: string) {
    for (const member of this.members) {
      if (member.id === id) {
        return;
      }
    }
    this.members.push(new Member(id, username));
  }

  deleteMember(id: number) {
    for (let i = 0; i < this.members.length; i++) {
      if (this.members[i].id === id) {
        this.members.splice(i, 1);
        return;
      }
    }
  }

  changeOwner(id: number) {
    for (const member of this.members) {
      if (member.id === id) {
        this.owner = member;
        return;
      }
    }
  }
}


