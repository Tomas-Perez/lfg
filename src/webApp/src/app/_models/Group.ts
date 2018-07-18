import {JsonObject, JsonProperty} from 'json2typescript';
import {DbActivity} from './DbModels/DbActivity';
import {GamePlatform} from './GamePlatform';
import {ChatPlatform} from './ChatPlatform';

@JsonObject
export class Member {

  constructor(id?: number, username?: string) {
    this.id = id || undefined;
    this.username = username || undefined;
  }

  @JsonProperty('id', Number)
  id: number = undefined;
  @JsonProperty('username', String)
  username: string = undefined;

  image: any;
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
  @JsonProperty('gamePlatform', GamePlatform, true)
  gamePlatform?: GamePlatform = undefined;
  @JsonProperty('chatPlatform', ChatPlatform, true)
  chatPlatform?: ChatPlatform = undefined;

  addMember(newMember: Member) {
    for (const member of this.members) {
      if (member.id === newMember.id) {
        return;
      }
    }
    this.members.push(newMember);
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


