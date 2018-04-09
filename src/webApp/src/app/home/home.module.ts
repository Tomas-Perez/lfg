import { NgModule } from '@angular/core';
import {HomeComponent} from './home.component';
import {HomeNavbarComponent} from './home-navbar/home-navbar.component';
import {SignInComponent} from './sign-in/sign-in.component';
import {SignUpComponent} from './sign-up/sign-up.component';
import {HomeRoutingModule} from './home-routing.module';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {AngularFontAwesomeModule} from 'angular-font-awesome';


@NgModule({
  declarations: [
    SignUpComponent,
    SignInComponent,
    HomeNavbarComponent,
    HomeComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    AngularFontAwesomeModule,
    NgbModule.forRoot(),
    HomeRoutingModule
  ],
  providers: [],
})
export class HomeModule { }
