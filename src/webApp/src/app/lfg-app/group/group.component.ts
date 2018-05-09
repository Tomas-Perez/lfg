import {Component, OnDestroy, OnInit} from '@angular/core';
import {Group} from '../../_models/Group';
import {GroupService} from '../../_services/group.service';
import {Subject} from 'rxjs/Subject';
import {User} from '../../_models/User';
import {UserService} from '../../_services/user.service';

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.css']
})
export class GroupComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  group: Group;
  emptySlots: Array<number>;
  user: User;

  constructor(private groupService: GroupService, private userService: UserService) { }

  ngOnInit() {
    this.groupService.currentGroupSubject.takeUntil(this.ngUnsubscribe).subscribe((group: Group) => {
      this.group = group;
      this.emptySlots = Array.from(Array(group.slots - group.members.length)).map((x, i) => i);
    });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
