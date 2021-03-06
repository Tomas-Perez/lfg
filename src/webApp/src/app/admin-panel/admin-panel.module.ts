import { NgModule } from '@angular/core';
import {AdminPanelRouting} from './admin-panel.routing';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {AdminPanelComponent} from './admin-panel.component';
import { GameListComponent } from './game-list/game-list.component';
import { GameEditComponent } from './game-edit/game-edit.component';
import { GameNewComponent } from './game-new/game-new.component';
import {ImageUploadModule} from 'angular2-image-upload';


@NgModule({
  declarations: [
    AdminPanelComponent,
    GameListComponent,
    GameEditComponent,
    GameNewComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ImageUploadModule.forRoot(),
    NgbModule.forRoot(),
    AdminPanelRouting
  ],
  providers: [],
})
export class AdminPanelModule { }
