import { Component, OnInit } from '@angular/core';
import 'simplebar';
import {User} from '../_models/User';
import {UserService} from '../_services/user.service';
import {PostService} from '../_services/post.service';
import {AuthService} from '../_services/auth.service';
import {Router, NavigationExtras, ActivatedRoute} from '@angular/router';
import {Post} from '../_models/Post';

@Component({
  selector: 'app-lfg-app',
  templateUrl: './lfg-app.component.html',
  styleUrls: ['./lfg-app.component.css']
})
export class LfgAppComponent implements OnInit {

  user: User;
  posts: Post[];

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
    this.postService.getPosts().subscribe(
      posts => {
        this.posts = posts;
      }
    );
  }

  logOut() {
    this.authService.logout();
    this.router.navigate(['/home']);
  }

  navigate(url: string) {
    this.router.navigate([{ outlets: {spekbar: [url] }}],
      {
        relativeTo: this.route,
        skipLocationChange: true
    });
  }

  // [routerLink]="[{ outlets: {'spekbar':['new-post'] }}]"


}
