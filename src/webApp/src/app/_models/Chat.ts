import {JsonObject, JsonProperty} from 'json2typescript';
import {Message} from './Message';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@JsonObject
class Member {
  @JsonProperty('id', Number)
  id: number = undefined;

  @JsonProperty('username', String)
  username: string = undefined;

  constructor(id?: number, username?: string){
    this.id = id || undefined;
    this.username = username || undefined;
  }
}

@JsonObject
export class Chat {

  @JsonProperty('id', Number)
  id: number = undefined;

  @JsonProperty('members', [Member])
  members: Member[] = undefined;

  @JsonProperty('messages', [Message], true)
  messages: Message[] = [];

  messagesSubject: BehaviorSubject<Message[]> = new BehaviorSubject<Message[]>(this.messages);

  pushMessage(message: Message) {
    this.messages.push(message);
    this.messagesSubject.next(this.messages);
  }

  updateMembers(ids: number[]) {
    this.members = [];
    for (const id of ids) {
      const member = new Member(id);
      this.members.push(member);
    }
  }

  addMember(id: number) {
    for (const member of this.members) {
      if (member.id === id) {
        return;
      }
    }
    const newMember = new Member(id);
    this.members.push(newMember);
  }

  removeMember(id: number) {
    for(let i = 0; i < this.members.length; i++) {
      if (this.members[i].id === id) {
        this.members.splice(i, 1);
        return;
      }
    }
  }

  getMemberUsername(id: number): string {
    for (const member of this.members) {
      if (id === member.id) {
        return member.username;
      }
    }
    return null;
  }

}
