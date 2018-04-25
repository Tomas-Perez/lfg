import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { UserInterfaceComponent } from './user-interface/user-interface.component';
import { UserNavigationComponent } from './user-interface/user-navigation/user-navigation.component';
import { HttpClientModule } from '@angular/common/http';

import { AuthService } from './_services/auth.service';
import { AppRoutingModule } from './app-routing.module';
import {HomeModule} from './home/home.module';
import { UserService } from './_services/user.service';
import {httpInterceptorProviders} from './_http-interceptors/index';
import { LfgAppComponent } from './lfg-app/lfg-app.component';

@NgModule({
  declarations: [
    AppComponent,
    UserInterfaceComponent,
    UserNavigationComponent,
    LfgAppComponent,
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    HomeModule,
    AppRoutingModule
  ],
  providers: [httpInterceptorProviders, AuthService, UserService],
  bootstrap: [AppComponent]
})
export class AppModule { }
