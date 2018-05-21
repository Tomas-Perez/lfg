import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LfgAppComponent} from './lfg-app.component';
import {AuthUserGuardService} from '../_services/guards/auth-user-guard.service';
import {NewPostComponent} from './spekbar/new-post/new-post.component';
import {NewGroupComponent} from './spekbar/new-group/new-group.component';
import {PostFilterComponent} from './spekbar/post-filter/post-filter.component';
import {GroupComponent} from './spekbar/group/group.component';
import {GroupGuardService} from '../_services/guards/group-guard.service';
import {NoGroupGuardService} from '../_services/guards/no-group-guard.service';
import {FriendListComponent} from './friends/friend-list/friend-list.component';
import {FriendRequestsComponent} from './friends/friend-requests/friend-requests.component';
import {UserInfoComponent} from './friends/user-info/user-info.component';

const routes: Routes = [
  { path: 'app', component: LfgAppComponent, canActivate: [AuthUserGuardService], children: [
    {path: '', component: PostFilterComponent, outlet: 'spekbar'},
    {path: 'filter-posts', component: PostFilterComponent, outlet: 'spekbar'},
    {path: 'new-post', component: NewPostComponent, canActivate: [NoGroupGuardService], outlet: 'spekbar'},
    {path: 'new-group', component: NewGroupComponent, canActivate: [NoGroupGuardService], outlet: 'spekbar'},
    {path: 'group', component: GroupComponent, canActivate: [GroupGuardService], outlet: 'spekbar'},
    {path: '', component: FriendListComponent, outlet: 'friends'},
    {path: 'friend-list', component: FriendListComponent, outlet: 'friends'},
    {path: 'user-info/:id', component: UserInfoComponent, outlet: 'friends'},
    {path: 'friend-requests', component: FriendRequestsComponent, outlet: 'friends'}
  ]}
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [ RouterModule ]

})
export class LfgAppRouting { }
