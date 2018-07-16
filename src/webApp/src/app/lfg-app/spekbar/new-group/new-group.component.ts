import {Component, OnDestroy, OnInit} from '@angular/core';
import {DbGroup} from '../../../_models/DbModels/DbGroup';
import {Game} from '../../../_models/Game';
import {GroupService} from '../../../_services/group.service';
import {UserService} from '../../../_services/user.service';
import {GameService} from '../../../_services/game.service';
import 'rxjs/add/operator/takeUntil';
import {Subject} from 'rxjs/Subject';
import {Router} from '@angular/router';
import {User} from '../../../_models/User';
import {SpekbarLocation} from '../../_models/SpekbarLocation';
import {NavBarService} from '../../_services/nav-bar.service';
import {ChatService} from '../../../_services/chat.service';
import {PlatformService} from '../../../_services/platform.service';
import {GamePlatform} from '../../../_models/GamePlatform';
import {ChatPlatform} from '../../../_models/ChatPlatform';

@Component({
  selector: 'app-new-group',
  templateUrl: './new-group.component.html',
  styleUrls: ['./new-group.component.css', '../spekbar.css']
})
export class NewGroupComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  private games: Game[];
  private gamePlatforms: GamePlatform[];
  private chatPlatforms: ChatPlatform[];
  private group: DbGroup;
  private selectedGameIndex: number;
  private selectedActivityIndex: number;
  private selectedGamePlatformIndex: number;
  private selectedChatPlatformIndex: number;
  private user: User;
  slots: number;

  constructor(private groupService: GroupService,
              private userService: UserService,
              private gameService: GameService,
              private platformService: PlatformService,
              private router: Router,
              private chatService: ChatService,
              private navBarService: NavBarService
              ) { }

  ngOnInit() {
    this.games = [];
    this.gamePlatforms = [];
    this.chatPlatforms = [];
    this.selectedGameIndex = null;
    this.selectedActivityIndex = null;
    this.selectedGamePlatformIndex = null;
    this.selectedChatPlatformIndex = -1;
    this.slots = 2;

    this.group = new DbGroup();

    this.navBarService.spekbarLocationSubject.next(SpekbarLocation.GROUP);

    this.userService.userSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(user => this.user = user);

    this.gameService.gamesSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(games => this.games = games);

    this.platformService.gamePlatformsSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(platforms => this.gamePlatforms = platforms);

    this.platformService.chatPlatformsSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(platforms => this.chatPlatforms = platforms);

  }

  newGroup() {
    this.group.slots = this.slots < 2 ? 2 : (this.slots > 15 ? 15 : this.slots);
    this.group.activityID = this.games[this.selectedGameIndex].activities[this.selectedActivityIndex].id;
    this.group.gamePlatform = this.gamePlatforms[this.selectedGamePlatformIndex].id;
    if (this.selectedChatPlatformIndex >= 0) {
      this.group.chatPlatform = this.chatPlatforms[this.selectedChatPlatformIndex].id;
    }
    this.group.ownerID = this.user.id;
    this.groupService.newGroup(this.group).subscribe(
      response => {
        if (response) {
          console.log('Group created');

          // this.chatService.newChat(ChatType.GROUP, [this.user.id]); // TODO

          this.router.navigate(['app/', { outlets: {spekbar: ['group'] }}],
            {
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
