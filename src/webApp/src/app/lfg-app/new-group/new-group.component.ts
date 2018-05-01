import { Component, OnInit } from '@angular/core';
import {Group} from '../../_models/Group';
import {Game} from '../../_models/Game';
import {GroupService} from '../../_services/group.service';
import {UserService} from '../../_services/user.service';
import {GameService} from '../../_services/game.service';

@Component({
  selector: 'app-new-group',
  templateUrl: './new-group.component.html',
  styleUrls: ['./new-group.component.css']
})
export class NewGroupComponent implements OnInit {

  private games: Game[];
  private selectedGameIndex: number;
  private selectedActivityIndex: number;
  private group: Group;

  constructor(private groupService: GroupService,
              private userService: UserService,
              private gameService: GameService) { }

  ngOnInit() {
    this.group = new Group;
    this.gameService.getGameList().subscribe(
      games => {
        this.games = games;
      });
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

}
