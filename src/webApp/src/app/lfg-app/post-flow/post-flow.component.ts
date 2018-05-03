import { Component, OnInit } from '@angular/core';
import {Post} from '../../_models/Post';
import {PostService} from '../../_services/post.service';

@Component({
  selector: 'app-post-flow',
  templateUrl: './post-flow.component.html',
  styleUrls: ['./post-flow.component.css']
})
export class PostFlowComponent implements OnInit {

  posts: Post[];

  constructor(private postService: PostService) { }

  ngOnInit() {
    this.postService.getPosts().subscribe(
      posts => {
        this.posts = posts;
      }
    );
  }

}
