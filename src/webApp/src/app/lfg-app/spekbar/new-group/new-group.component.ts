import {Component, OnDestroy, OnInit} from '@angular/core';
import {DbGroup} from '../../../_models/DbModels/DbGroup';
import {Game} from '../../../_models/Game';
import {GroupService} from '../../../_services/group.service';
import {UserService} from '../../../_services/user.service';
import {GameService} from '../../../_services/game.service';
import 'rxjs/add/operator/takeUntil';
import {Subject} from 'rxjs/Subject';
import {ActivatedRoute, Router} from '@angular/router';
import {User} from '../../../_models/User';

@Component({
  selector: 'app-new-group',
  templateUrl: './new-group.component.html',
  styleUrls: ['./new-group.component.css', '../spekbar.css']
})
export class NewGroupComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  private games: Game[];
  private group: DbGroup;
  private selectedGameIndex: number;
  private selectedActivityIndex: number;
  private user: User;

  constructor(private groupService: GroupService,
              private userService: UserService,
              private gameService: GameService,
              private router: Router,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.group = new DbGroup();

    this.gameService.gamesSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(games => this.games = games);

    this.userService.userSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(user => this.user = user);
  }

  newGroup() {
    this.group.activityID = this.games[this.selectedGameIndex].activities[this.selectedActivityIndex].id;
    this.group.ownerID = this.user.id;
    this.groupService.newGroup(this.group).subscribe(
      response => {
        if (response) {
          console.log('Group created');

          this.router.navigate(['../', { outlets: {spekbar: ['group'] }}],
            {
              relativeTo: this.route,
              skipLocationChange: true
            });

        }
      }
    );
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
