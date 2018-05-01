import { Component, OnInit } from '@angular/core';
import 'SimpleBar';
import {User} from '../_models/User';
import {UserService} from '../_services/user.service';
import {PostService} from '../_services/post.service';
import {AuthService} from '../_services/auth.service';
import {Router, NavigationExtras, ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-lfg-app',
  templateUrl: './lfg-app.component.html',
  styleUrls: ['./lfg-app.component.css']
})
export class LfgAppComponent implements OnInit {

  user: User;

  constructor(private userService: UserService,
              private postService: PostService,
              private authService: AuthService,
              private router: Router,
              private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.userService.getCurrentUser().subscribe(
      user => {
        this.user = user;
      });
  }

  logOut() {
    this.authService.logout();
    this.router.navigate(['/home']);
  }

  navigate() {
    this.router.navigate([{ outlets: {spekbar: ['new-post'] }}],
      {
        relativeTo: this.route,
        skipLocationChange: true
    });
  }

  // [routerLink]="[{ outlets: {'spekbar':['new-post'] }}]"

}
