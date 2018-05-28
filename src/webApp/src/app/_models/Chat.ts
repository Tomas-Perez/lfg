import {JsonObject, JsonProperty} from 'json2typescript';
import {Message} from './Message';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@JsonObject
class Member {
  @JsonProperty('id', Number)
  id: number = undefined;
}

@JsonObject
export class Chat {

  @JsonProperty('id', Number)
  id: number = undefined;

  @JsonProperty('members', [Member])
  members: Member[] = undefined;


  messages: Message[] = [];
  messagesSubject: BehaviorSubject<Message[]> = new BehaviorSubject<Message[]>(this.messages);

  pushMessage(message: Message){
    this.messages.push(message);
    this.messagesSubject.next(this.messages);
  }
}
