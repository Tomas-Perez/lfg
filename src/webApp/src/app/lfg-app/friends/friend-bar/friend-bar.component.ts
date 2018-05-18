import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-friend-bar',
  templateUrl: './friend-bar.component.html',
  styleUrls: ['./friend-bar.component.css']
})
export class FriendBarComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.router.navigate([{ outlets: {friends: ['friend-list'] }}],
      {
        relativeTo: this.route,
        //skipLocationChange: true
      });
  }

  navigate(url: string) {
    this.router.navigate([{ outlets: {friends: [url] }}],
      {
        relativeTo: this.route,
        //skipLocationChange: true
      });
  }

}
