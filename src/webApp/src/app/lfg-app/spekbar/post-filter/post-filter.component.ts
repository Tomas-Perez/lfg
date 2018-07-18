import {Component, OnDestroy, OnInit} from '@angular/core';
import {Game} from '../../../_models/Game';
import {GameService} from '../../../_services/game.service';
import {PostFilterService} from './post-filter.service';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {PostFilter} from '../../../_models/post-filters/PostFilter';
import {NavBarService} from '../../_services/nav-bar.service';
import {SpekbarLocation} from '../../_models/SpekbarLocation';
import {ChatPlatform} from '../../../_models/ChatPlatform';
import {GamePlatform} from '../../../_models/GamePlatform';
import {PlatformService} from '../../../_services/platform.service';

@Component({
  selector: 'app-post-filter',
  templateUrl: './post-filter.component.html',
  styleUrls: ['./post-filter.component.css', '../spekbar.css']
})
export class PostFilterComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  private games: Game[];
  private gamePlatforms: GamePlatform[];
  private chatPlatforms: ChatPlatform[];
  private selectedGameIndex: number;
  private selectedActivityIndex  = -1;
  private selectedGamePlatformIndex  = -1;
  private selectedChatPlatformIndex  = -1;
  private activeFilters: PostFilter[];

  constructor(
    private gameService: GameService,
    private navBarService: NavBarService,
    private platformService: PlatformService,
    private postFilterService: PostFilterService
  ) { }

  ngOnInit() {

    this.games = [];
    this.gamePlatforms = [];
    this.chatPlatforms = [];

    this.navBarService.spekbarLocationSubject.next(SpekbarLocation.FILTER);

    this.platformService.gamePlatformsSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(platforms => this.gamePlatforms = platforms);

    this.platformService.chatPlatformsSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(platforms => this.chatPlatforms = platforms);

    this.gameService.gamesSubject.takeUntil(this.ngUnsubscribe)
      .subscribe(games => {
        this.games = games;

        if (this.games.length > 0) {
          this.selectedGameIndex = 0;
        }

      });

    this.postFilterService.filtersSubject.takeUntil(this.ngUnsubscribe)
      .subscribe( activeFilters => this.activeFilters = activeFilters);
  }

  gameChanged(event) {
    this.selectedActivityIndex = -1;
  }

  addFilter() {
    if (this.selectedGameIndex < 0) {
      return;
    }
    const game = this.games[this.selectedGameIndex];
    const filter = new PostFilter(game);
    if (this.selectedActivityIndex > -1) {
      filter.activity = game.activities[this.selectedActivityIndex];
    }
    if (this.selectedGamePlatformIndex > -1) {
      filter.gamePlatform = this.gamePlatforms[this.selectedGamePlatformIndex];
    }
    if (this.selectedChatPlatformIndex > -1) {
      filter.chatPlatform = this.chatPlatforms[this.selectedChatPlatformIndex];
    }
    this.postFilterService.addFilter(filter);
  }

  removeFilter(filter: PostFilter) {
    this.postFilterService.removeFilter(filter);
  }

  resetFilters() {
    this.postFilterService.resetFilters();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
