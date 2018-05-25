import {Component, OnInit} from '@angular/core';
import 'simplebar';
import {UserService} from '../_services/user.service';
import 'rxjs/add/operator/takeUntil';

@Component({
  selector: 'app-lfg-app',
  templateUrl: './lfg-app.component.html',
  styleUrls: ['./lfg-app.component.css']
})
export class LfgAppComponent implements OnInit {

  constructor() { }

  ngOnInit() {}

}
