import {Component, OnDestroy, OnInit} from '@angular/core';
import {Game} from '../../../_models/Game';
import {GameService} from '../../../_services/game.service';
import {FilterByGame} from '../../../_models/post-filters/FilterByGame';
import {FilterByActivity} from '../../../_models/post-filters/FilterByActivity';
import {PostFilterService} from './post-filter.service';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {PostFilter} from '../../../_models/post-filters/PostFilter';
import {NavBarService} from '../../_services/nav-bar.service';
import {SpekbarLocation} from '../../_models/SpekbarLocation';

@Component({
  selector: 'app-post-filter',
  templateUrl: './post-filter.component.html',
  styleUrls: ['./post-filter.component.css', '../spekbar.css']
})
export class PostFilterComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  private games: Game[];
  private selectedGameIndex: number;
  private selectedActivityIndex  = -1;
  private activeFilters: PostFilter[];

  constructor(
    private gameService: GameService,
    private navBarService: NavBarService,
    private postFilterService: PostFilterService
  ) { }

  ngOnInit() {

    this.navBarService.spekbarLocationSubject.next(SpekbarLocation.FILTER);

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

  addFilter() {
    if (this.selectedGameIndex >= 0) {
      const game = this.games[this.selectedGameIndex];
      if (this.selectedActivityIndex > -1) {
        const activity = game.activities[this.selectedActivityIndex];
        this.postFilterService.addFilter(new FilterByActivity(game.name, game.id, activity.name, activity.id));
      } else {
        this.postFilterService.addFilter(new FilterByGame(game.name, game.id));
      }
    }
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
