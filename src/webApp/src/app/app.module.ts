import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import { UserinterfaceComponent } from './userinterface/userinterface.component';
import { UserNavigationComponent } from './user-navigation/user-navigation.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { UserService } from './user.service';
import { LogInComponent } from './log-in/log-in.component';
import { SignUpComponent } from './sign-up/sign-up.component';


@NgModule({
  declarations: [
    AppComponent,
    UserinterfaceComponent,
    UserNavigationComponent,
    SignUpComponent,
    LogInComponent
  ],
  imports: [
    HttpClientModule,
    FormsModule,
    BrowserModule,
    NgbModule.forRoot()
  ],
  providers: [UserService],
  bootstrap: [AppComponent]
})
export class AppModule { }
