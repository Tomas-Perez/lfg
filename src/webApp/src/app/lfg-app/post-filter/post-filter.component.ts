import {Component, OnDestroy, OnInit} from '@angular/core';
import {Game} from '../../_models/Game';
import {GameService} from '../../_services/game.service';
import {FilterByGame} from '../../_models/post-filters/FilterByGame';
import {FilterByActivity} from '../../_models/post-filters/FilterByActivity';
import {PostFilterService} from './post-filter.service';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {PostFilter} from '../../_models/post-filters/PostFilter';

@Component({
  selector: 'app-post-filter',
  templateUrl: './post-filter.component.html',
  styleUrls: ['./post-filter.component.css']
})
export class PostFilterComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  private games: Game[];
  private selectedGameIndex: number;
  private selectedActivityIndex  = -1;
  private activeFilters: PostFilter[];

  constructor(
    private gameService: GameService,
    private postFilterService: PostFilterService
  ) { }

  ngOnInit() {
    this.gameService.getGameList().takeUntil(this.ngUnsubscribe)
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
    const game = this.games[this.selectedGameIndex];
    if (this.selectedGameIndex) {
      if (this.selectedActivityIndex > -1) {
        const activity = game.activities[this.selectedActivityIndex];
        this.postFilterService.addFilter(new FilterByActivity(game.name, activity.name, activity.id));
      } else {
        this.postFilterService.addFilter(new FilterByGame(game.name, game.id));
      }
    }
    console.log(this.activeFilters);
    console.log(this.postFilterService.filters);
  }

  resetFilters() {
    this.postFilterService.resetFilters();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
