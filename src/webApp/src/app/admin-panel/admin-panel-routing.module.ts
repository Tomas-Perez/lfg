import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AdminPanelComponent} from './admin-panel.component';
import {GameListComponent} from './game-list/game-list.component';
import {GameEditComponent} from './game-edit/game-edit.component';
import {GameNewComponent} from './game-new/game-new.component';

const routes: Routes = [
  { path: 'admin-panel', component: AdminPanelComponent , children: [
    { path: 'games', component: GameListComponent},
    { path: 'new-game', component: GameNewComponent},
    { path: 'edit-game/:id', component: GameEditComponent}
  ]}
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [ RouterModule ]

})
export class AdminPanelRoutingModule { }
