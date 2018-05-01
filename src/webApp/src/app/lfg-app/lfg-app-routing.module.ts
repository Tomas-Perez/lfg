import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LfgAppComponent} from './lfg-app.component';
import {AuthUserGuardService} from '../_services/guards/auth-user-guard.service';
import {NewPostComponent} from './new-post/new-post.component';

const routes: Routes = [
  { path: 'app', component: LfgAppComponent, canActivate: [AuthUserGuardService], children: [
    {path: 'new-post', component: NewPostComponent, outlet: 'spekbar'}
  ]}
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [ RouterModule ]

})
export class LfgAppRoutingModule { }
