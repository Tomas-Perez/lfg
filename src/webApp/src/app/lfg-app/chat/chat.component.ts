import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {

  chats: {username: string, id: number}[];
  activatedTabIndex: number;

  constructor() { }

  ngOnInit() {
    this.activatedTabIndex = 0;
    this.chats = [{username: 'Lethanity', id: 1}, {username: 'juanga13', id: 2}];
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
