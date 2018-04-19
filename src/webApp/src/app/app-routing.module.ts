import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {UserInterfaceComponent} from './user-interface/user-interface.component';
import {LfgAppComponent} from "./lfg-app/lfg-app.component";

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full'},
  { path: 'user', component: UserInterfaceComponent },
  { path: 'app', component: LfgAppComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [ RouterModule ]

})
export class AppRoutingModule { }
