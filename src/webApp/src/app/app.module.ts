import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import { UserinterfaceComponent } from './userinterface/userinterface.component';
import { UserNavigationComponent } from './user-navigation/user-navigation.component';
import { LoginComponent } from './login/login.component';
import {FormsModule} from '@angular/forms';


@NgModule({
  declarations: [
    AppComponent,
    UserinterfaceComponent,
    UserNavigationComponent,
    LoginComponent
  ],
  imports: [
    FormsModule,
    BrowserModule,
    NgbModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
