import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import { UserinterfaceComponent } from './userinterface/userinterface.component';
import { UserNavigationComponent } from './user-navigation/user-navigation.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { UserService } from './user.service';
import { SignUpComponent } from './sign-up/sign-up.component';
import { AppRoutingModule } from './app-routing.module';
import { SignInComponent } from './sign-in/sign-in.component';
import { HomeComponent } from './home/home.component';
import { HomeNavbarComponent } from './home-navbar/home-navbar.component';


@NgModule({
  declarations: [
    AppComponent,
    UserinterfaceComponent,
    UserNavigationComponent,
    SignUpComponent,
    SignInComponent,
    HomeComponent,
    HomeNavbarComponent
  ],
  imports: [
    HttpClientModule,
    FormsModule,
    BrowserModule,
    NgbModule.forRoot(),
    AppRoutingModule
  ],
  providers: [UserService],
  bootstrap: [AppComponent]
})
export class AppModule { }
