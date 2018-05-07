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


@NgModule({
  declarations: [
    LfgAppComponent,
    NewPostComponent,
    NewGroupComponent,
    PostFilterComponent,
    PostFlowComponent
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
    PostFilterService
  ],
})
export class LfgAppModule { }
