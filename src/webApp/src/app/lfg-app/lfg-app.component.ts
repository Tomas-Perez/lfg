import { Component, OnInit } from '@angular/core';
import * as SimpleBar from 'simplebar';

@Component({
  selector: 'app-lfg-app',
  templateUrl: './lfg-app.component.html',
  styleUrls: ['./lfg-app.component.css']
})
export class LfgAppComponent implements OnInit {

  bar: any;

  constructor() { }

  ngOnInit() {
    this.bar = new SimpleBar(<HTMLElement>document.querySelector('.scrollable'), { autoHide: true });
  }

}
