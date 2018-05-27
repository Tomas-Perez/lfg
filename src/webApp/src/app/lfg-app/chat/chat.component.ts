import {AfterViewInit, Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import * as SimpleBar from 'simplebar';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, AfterViewInit {

  chats: {username: string, messages: {id: number, date: Date, message: string}[]}[];
  user = {id: 0};


  @ViewChildren('messages') messageFor: QueryList<any>;
  activatedTabIndex: number;
  messageInput: string;
  chatOpen: boolean;

  constructor() { }

  ngOnInit() {
    this.messageInput = '';
    this.activatedTabIndex = 0;
    this.chatOpen = false;

    this.chats = [
      {username: 'Lethanity', messages: [{id: 1, date: new Date(), message: 'Hola'},
                                          {id: 0, date: new Date(),  message: 'Hola'},
                                        {id: 1, date: new Date(),  message: 'Chau'}]
      },
      {username: 'Juenga13', messages: [{id: 0, date: new Date(),  message: 'Hole'},
                                        {id: 1, date: new Date(),  message: 'Hole'},
                                        {id: 0, date: new Date(),  message: 'Cheu'}]
      }
    ];

  }

  ngAfterViewInit() {
    this.scrollToBottom();
    this.messageFor.changes.subscribe(t => {
      console.log('asdfasdf');
      this.scrollToBottom();
    });
  }

  scrollToBottom() {
    const el = new SimpleBar(document.getElementById('scrollbar'));
    el.getScrollElement().scrollTop = el.getContentElement().clientHeight;
  }

  toggleChat(){
    this.chatOpen = !this.chatOpen;
  }

  sendMessage() {
    if (this.chats.length && this.messageInput.trim() === '') {
      return;
    }
    this.chats[this.activatedTabIndex].messages.push({id: this.user.id, date: new Date(), message: this.messageInput});
    this.messageInput = '';
  }

  activateTab(index: number) {
    this.activatedTabIndex = index;
  }

  closeTab(index: number) {
    if (index === this.activatedTabIndex) {
    this.activatedTabIndex = 0;
  }
  this.chats.splice(index, 1); // TODO delete from actual chats
}

}
