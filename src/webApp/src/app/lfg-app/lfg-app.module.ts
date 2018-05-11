import { NgModule } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {AngularFontAwesomeModule} from 'angular-font-awesome';
import {LfgAppComponent} from './lfg-app.component';
import {LfgAppRouting} from './lfg-app.routing';
import {PostService} from '../_services/post.service';
import { NewPostComponent } from './new-post/new-post.component';
import { NewGroupComponent } from './new-group/new-group.component';
import { PostFilterComponent } from './post-filter/post-filter.component';
import { PostFlowComponent } from './post-flow/post-flow.component';
import { NewPostService } from './new-post/new-post.service';
import { PostFilterService } from './post-filter/post-filter.service';
import { GroupComponent } from './group/group.component';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import {GroupGuardService} from '../_services/guards/group-guard.service';
import {NoGroupGuardService} from '../_services/guards/no-group-guard.service';
import { GroupPostService } from './group/group-post.service';


@NgModule({
  declarations: [
    LfgAppComponent,
    NewPostComponent,
    NewGroupComponent,
    GroupComponent,
    PostFilterComponent,
    PostFlowComponent,
    NavBarComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    AngularFontAwesomeModule,
    NgbModule.forRoot(),
    LfgAppRouting
  ],
  providers: [
    PostService,
    NewPostService,
    PostFilterService,
    GroupGuardService,
    NoGroupGuardService,
    GroupPostService
  ],
})
export class LfgAppModule { }
