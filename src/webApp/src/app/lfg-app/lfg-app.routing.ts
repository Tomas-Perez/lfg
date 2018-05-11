import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LfgAppComponent} from './lfg-app.component';
import {AuthUserGuardService} from '../_services/guards/auth-user-guard.service';
import {NewPostComponent} from './new-post/new-post.component';
import {NewGroupComponent} from './new-group/new-group.component';
import {PostFilterComponent} from './post-filter/post-filter.component';
import {GroupComponent} from './group/group.component';
import {GroupGuardService} from '../_services/guards/group-guard.service';
import {NoGroupGuardService} from '../_services/guards/no-group-guard.service';

const routes: Routes = [
  { path: 'app', component: LfgAppComponent, canActivate: [AuthUserGuardService], children: [
    {path: 'filter-posts', component: PostFilterComponent, outlet: 'spekbar'},
    {path: 'new-post', component: NewPostComponent, outlet: 'spekbar'},
    {path: 'new-group', component: NewGroupComponent, canActivate: [NoGroupGuardService], outlet: 'spekbar'},
    {path: 'group', component: GroupComponent, canActivate: [GroupGuardService], outlet: 'spekbar'}
  ]}
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [ RouterModule ]

})
export class LfgAppRouting { }
