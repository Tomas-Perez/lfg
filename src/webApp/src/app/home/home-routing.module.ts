import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SignInComponent} from './sign-in/sign-in.component';
import {SignUpComponent} from './sign-up/sign-up.component';
import {HomeComponent} from './home.component';
import {UnauthUserGuardService} from '../_services/unauth-user-guard.service';

const routes: Routes = [
  { path: 'home', component: HomeComponent, children: [
    { path: 'sign-in', canActivate: [UnauthUserGuardService], component: SignInComponent},
    { path: 'sign-up', canActivate: [UnauthUserGuardService], component: SignUpComponent}
  ]},
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [ RouterModule ]

})
export class HomeRoutingModule { }
