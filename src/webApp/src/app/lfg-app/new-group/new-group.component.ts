import {Component, OnDestroy, OnInit} from '@angular/core';
import {DbGroup} from '../../_models/DbModels/DbGroup';
import {Game} from '../../_models/Game';
import {GroupService} from '../../_services/group.service';
import {UserService} from '../../_services/user.service';
import {GameService} from '../../_services/game.service';
import 'rxjs/add/operator/takeUntil';
import {Subject} from 'rxjs/Subject';

@Component({
  selector: 'app-new-group',
  templateUrl: './new-group.component.html',
  styleUrls: ['./new-group.component.css']
})
export class NewGroupComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  private games: Game[];
  private group: DbGroup;
  private selectedGameIndex: number;
  private selectedActivityIndex: number;

  constructor(private groupService: GroupService,
              private userService: UserService,
              private gameService: GameService) { }

  ngOnInit() {
    this.group = new DbGroup();

    this.gameService.gamesSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(games => this.games = games);

  }

  newGroup() {
    this.group.activityID = this.games[this.selectedGameIndex].activities[this.selectedActivityIndex].id;
    this.userService.getCurrentUser().subscribe(
      user => {
        this.group.ownerID = user.id;
        this.groupService.newGroup(this.group).subscribe(
          response => {
            if (response) {
              console.log('Group created');
            }
          }
        );
      }
    );
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
