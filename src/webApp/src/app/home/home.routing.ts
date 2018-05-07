import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SignInComponent} from './sign-in/sign-in.component';
import {SignUpComponent} from './sign-up/sign-up.component';
import {HomeComponent} from './home.component';
import {UnauthUserGuardService} from '../_services/guards/unauth-user-guard.service';
import {NotFoundComponent} from './not-found/not-found.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent, children: [
    { path: 'not-found', component: NotFoundComponent},
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
export class HomeRouting { }
