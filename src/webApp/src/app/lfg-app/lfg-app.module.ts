import { NgModule } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {AngularFontAwesomeModule} from 'angular-font-awesome';
import {LfgAppComponent} from './lfg-app.component';
import {LfgAppRoutingModule} from './lfg-app-routing.module';
import {PostService} from '../_services/post.service';
import { NewPostComponent } from './new-post/new-post.component';
import { NewGroupComponent } from './new-group/new-group.component';
import { PostFilterComponent } from './post-filter/post-filter.component';


@NgModule({
  declarations: [
    LfgAppComponent,
    NewPostComponent,
    NewGroupComponent,
    PostFilterComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    AngularFontAwesomeModule,
    NgbModule.forRoot(),
    LfgAppRoutingModule
  ],
  providers: [
    PostService
  ],
})
export class LfgAppModule { }
