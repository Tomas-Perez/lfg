import {AfterViewInit, Component, OnDestroy, OnInit, QueryList, ViewChildren} from '@angular/core';
import * as SimpleBar from 'simplebar';
import {ChatService} from '../../_services/chat.service';
import {Subject} from 'rxjs/Subject';
import {Chat} from '../../_models/Chat';
import {Subscription} from 'rxjs/Subscription';
import {UserService} from '../../_services/user.service';
import {User} from '../../_models/User';
import {Router} from '@angular/router';
import {ChatType} from '../../_models/ChatType';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, OnDestroy, AfterViewInit {

  private ngUnsubscribe: Subject<any> = new Subject();
  user: User;
  chats: Chat[];
  messageSubscriptions: Map<number, Subscription>;
  chatTitles: {title: string, id: number}[];

  @ViewChildren('messages') messageFor: QueryList<any>;
  activatedTabIndex: number;
  messageInput: string;
  chatOpen: boolean;

  constructor(
    private chatService: ChatService,
    private userService: UserService,
    private router: Router
  ) { }

  ngOnInit() {
    this.messageInput = '';
    this.chatTitles = [];
    this.activatedTabIndex = 0;
    this.chatOpen = false;
    this.chats = [];
    this.messageSubscriptions = new Map<number, Subscription>();

    this.userService.userSubject.takeUntil(this.ngUnsubscribe).subscribe(user => this.user = user);

    this.chatService.chatsSubject.takeUntil(this.ngUnsubscribe).subscribe(chats => {
      this.chatTitles = [];
      for (const chat of chats) {
        if (!this.messageSubscriptions.has(chat.id)) {
          const messageSub = chat.messagesSubject.takeUntil(this.ngUnsubscribe).subscribe();
          this.messageSubscriptions.set(chat.id, messageSub);
        }
        this.chatTitles.push(this.getChatTitle(chat));
      }
      this.chats = chats;
      this.checkActivatedTabIndex();
      if (this.chats.length) {
        this.openChat();
      }
    });
  }

  ngAfterViewInit() {
    // this.scrollToBottom();
    this.messageFor.changes.subscribe(t => {
      this.scrollToBottom();
    });
  }

  scrollToBottom() {
    const el = new SimpleBar(document.getElementById('scrollbar'));
    el.getScrollElement().scrollTop = el.getContentElement().clientHeight;
  }

  toggleChat() {
    this.chatOpen = !this.chatOpen;
  }

  openChat() {
    this.chatOpen = true;
  }

  sendMessage() {
    if (this.messageInput.trim() !== '') {
      const b = this.chatService.sendMessage(this.chats[this.activatedTabIndex].id, this.messageInput);
      this.messageInput = '';
    }
  }

  activateTab(index: number) {
    this.activatedTabIndex = index;
  }

  closeTab(i: number) {
    const id = this.chats[i].id;
    this.messageSubscriptions.get(id).unsubscribe();
    this.chatService.deleteChat(id).subscribe( b => {
      if (this.chats.length === 0) {
        this.chatOpen = false;
      }
    }); // TODO everything should depend on this subscription
  }

  checkActivatedTabIndex() {
    if (this.activatedTabIndex > this.chats.length - 1 && this.activatedTabIndex > 0) {
      this.activatedTabIndex--;
    }
  }

  getSenderUsername(idSender: number): string {
    const name = this.chats[this.activatedTabIndex].getMemberUsername(idSender);
    return name != null ? name : 'Unknown';
  }

  /**
   * Returns name of the chatter if its only one or 'Group' if its a group
   * @returns {string}
   */
  getChatTitle(chat: Chat): {title: string, id: number} {
    if (chat.type === ChatType.GROUP) {
      return {title: 'Group', id: -1};
    }
    return chat.members[0].id === this.user.id ? {title: chat.members[1].username, id: chat.members[1].id}
                                    : {title: chat.members[0].username, id: chat.members[0].id};
  }

  getUserInfo(id: number) {
    this.router.navigate(['/app', { outlets: {friends: ['user-info', id] }}], {
      skipLocationChange: true
    });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
