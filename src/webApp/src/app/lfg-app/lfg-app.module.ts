import { NgModule } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {LfgAppComponent} from './lfg-app.component';
import {LfgAppRouting} from './lfg-app.routing';
import {PostService} from '../_services/post.service';
import { NewPostComponent } from './spekbar/new-post/new-post.component';
import { NewGroupComponent } from './spekbar/new-group/new-group.component';
import { PostFilterComponent } from './spekbar/post-filter/post-filter.component';
import { PostFlowComponent } from './post-flow/post-flow.component';
import { NewPostService } from './spekbar/new-post/new-post.service';
import { PostFilterService } from './spekbar/post-filter/post-filter.service';
import { GroupComponent } from './spekbar/group/group.component';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { GroupGuardService } from '../_services/guards/group-guard.service';
import { NoGroupGuardService } from '../_services/guards/no-group-guard.service';
import { GroupPostService } from './spekbar/group/group-post.service';
import { FriendListComponent } from './friends/friend-list/friend-list.component';
import { FriendRequestsComponent } from './friends/friend-requests/friend-requests.component';
import { FriendBarComponent } from './friends/friend-bar/friend-bar.component';
import { NavBarService } from './_services/nav-bar.service';
import { FriendStateService } from './_services/friend-state.service';
import { FriendService } from '../_services/friend.service';
import { UserInfoComponent } from './friends/user-info/user-info.component';
import {TimeAgoPipe} from 'time-ago-pipe';
import {MeInfoComponent} from './me-info/me-info.component';
import { ChatComponent } from './chat/chat.component';
import { ChatService } from '../_services/chat.service';
import { UserSocketService } from '../_services/user-socket.service';
import { EditUserComponent } from './spekbar/edit-user/edit-user.component';
import {ImageUploadModule} from 'angular2-image-upload';
import { SearchComponent } from './friends/search/search.component';
import {SearchService} from './friends/search/search.service';
import { PlatformService } from '../_services/platform.service';


@NgModule({
  declarations: [
    TimeAgoPipe,
    LfgAppComponent,
    NewPostComponent,
    NewGroupComponent,
    GroupComponent,
    PostFilterComponent,
    PostFlowComponent,
    NavBarComponent,
    FriendListComponent,
    FriendRequestsComponent,
    FriendBarComponent,
    UserInfoComponent,
    MeInfoComponent,
    ChatComponent,
    EditUserComponent,
    SearchComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbModule.forRoot(),
    ImageUploadModule.forRoot(),
    LfgAppRouting
  ],
  providers: [
    PostService,
    NewPostService,
    PostFilterService,
    GroupGuardService,
    NoGroupGuardService,
    GroupPostService,
    NavBarService,
    FriendStateService,
    FriendService,
    ChatService,
    UserSocketService,
    SearchService,
    PlatformService
  ],
})
export class LfgAppModule { }
