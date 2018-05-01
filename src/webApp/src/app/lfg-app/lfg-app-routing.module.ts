import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LfgAppComponent} from './lfg-app.component';
import {AuthUserGuardService} from '../_services/guards/auth-user-guard.service';
import {NewPostComponent} from './new-post/new-post.component';
import {NewGroupComponent} from './new-group/new-group.component';
import {PostFilterComponent} from './post-filter/post-filter.component';

const routes: Routes = [
  { path: 'app', component: LfgAppComponent, canActivate: [AuthUserGuardService], children: [
    {path: 'filter-posts', component: PostFilterComponent, outlet: 'spekbar'},
    {path: 'new-post', component: NewPostComponent, outlet: 'spekbar'},
    {path: 'new-group', component: NewGroupComponent, outlet: 'spekbar'}
  ]}
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [ RouterModule ]

})
export class LfgAppRoutingModule { }
