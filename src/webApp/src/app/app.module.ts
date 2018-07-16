import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from './_services/auth.service';
import { AppRouting } from './appRouting.routing';
import {HomeModule} from './home/home.module';
import { UserService } from './_services/user.service';
import {httpInterceptorProviders} from './_http-interceptors';
import {LfgAppModule} from './lfg-app/lfg-app.module';
import {AuthUserGuardService} from './_services/guards/auth-user-guard.service';
import {UnauthUserGuardService} from './_services/guards/unauth-user-guard.service';
import {AdminPanelModule} from './admin-panel/admin-panel.module';
import {AdminGuardService} from './_services/guards/admin-guard.service';
import {HttpService} from './_services/http.service';
import {WsService} from './_services/ws.service';
import {ImageService} from './_services/image.service';

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
    AppRouting,
  ],
  providers: [
    HttpService,
    WsService,
    AuthService,
    httpInterceptorProviders,
    UserService,
    AuthUserGuardService,
    UnauthUserGuardService,
    AdminGuardService,
    ImageService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
