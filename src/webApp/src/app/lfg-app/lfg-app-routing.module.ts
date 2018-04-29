import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LfgAppComponent} from './lfg-app.component';
import {AuthUserGuardService} from '../_services/auth-user-guard.service';

const routes: Routes = [
  { path: 'app', component: LfgAppComponent, canActivate: [AuthUserGuardService]}
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [ RouterModule ]

})
export class LfgAppRoutingModule { }
