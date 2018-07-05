import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SignInComponent} from './sign-in/sign-in.component';
import {SignUpComponent} from './sign-up/sign-up.component';
import {HomeComponent} from './home.component';
import {UnauthUserGuardService} from '../_services/guards/unauth-user-guard.service';
import {NotFoundComponent} from './not-found/not-found.component';
import {LandingComponent} from './landing/landing.component';

const routes: Routes = [
  { path: '', component: HomeComponent, children: [
    { path: '', pathMatch: 'full', component: LandingComponent},
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
