import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { UserinterfaceComponent } from './userinterface/userinterface.component';
import { UserNavigationComponent } from './user-navigation/user-navigation.component';
import { HttpClientModule } from '@angular/common/http';

import { UserService } from './user.service';
import { AppRoutingModule } from './app-routing.module';
import {HomeModule} from './home/home.module';


@NgModule({
  declarations: [
    AppComponent,
    UserinterfaceComponent,
    UserNavigationComponent,
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    HomeModule,
    AppRoutingModule
  ],
  providers: [UserService],
  bootstrap: [AppComponent]
})
export class AppModule { }
