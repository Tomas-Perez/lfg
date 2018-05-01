import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from './_services/auth.service';
import { AppRoutingModule } from './app-routing.module';
import {HomeModule} from './home/home.module';
import { UserService } from './_services/user.service';
import {httpInterceptorProviders} from './_http-interceptors/index';
import {LfgAppModule} from './lfg-app/lfg-app.module';
import {AuthUserGuardService} from './_services/guards/auth-user-guard.service';
import {UnauthUserGuardService} from './_services/guards/unauth-user-guard.service';
import {AdminPanelModule} from './admin-panel/admin-panel.module';
import {GameService} from './_services/game.service';
import { GroupService } from './_services/group.service';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    HomeModule,
    LfgAppModule,
    AdminPanelModule,
    AppRoutingModule,
  ],
  providers: [
    AuthService,
    httpInterceptorProviders,
    UserService,
    AuthUserGuardService,
    GameService,
    UnauthUserGuardService,
    GroupService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
