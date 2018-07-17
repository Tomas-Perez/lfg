import {Component, OnDestroy, OnInit} from '@angular/core';
import {Game} from '../../../_models/Game';
import {GameService} from '../../../_services/game.service';
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
