import { NgModule } from '@angular/core';
import {HomeComponent} from './home.component';
import {HomeNavbarComponent} from './home-navbar/home-navbar.component';
import {SignInComponent} from './sign-in/sign-in.component';
import {SignUpComponent} from './sign-up/sign-up.component';
import {HomeRouting} from './home.routing';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NotFoundComponent} from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';


@NgModule({
  declarations: [
    SignUpComponent,
    SignInComponent,
    NotFoundComponent,
    HomeNavbarComponent,
    HomeComponent,
    LandingComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbModule.forRoot(),
    HomeRouting
  ],
  providers: [],
})
export class HomeModule { }
