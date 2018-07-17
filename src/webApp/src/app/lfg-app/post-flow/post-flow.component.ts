import {Component, OnDestroy, OnInit} from '@angular/core';
import {Post} from '../../_models/Post';
import {PostService} from '../../_services/post.service';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';
import {GroupService} from '../../_services/group.service';
import {Router} from '@angular/router';
import {UserService} from '../../_services/user.service';
import {User} from '../../_models/User';
import {ChatService} from '../../_services/chat.service';
import {ChatType} from '../../_models/ChatType';
import {GameService} from '../../_services/game.service';

@Component({
  selector: 'app-post-flow',
  templateUrl: './post-flow.component.html',
  styleUrls: ['./post-flow.component.css'],
})
export class PostFlowComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<any> = new Subject();
  posts: Post[];
  inGroup: boolean;
  user: User;
  gameImages: Map<number, any>;
  imageReady: boolean[];

  constructor(private postService: PostService,
              private groupService: GroupService,
              private chatService: ChatService,
              private gameService: GameService,
              private userService: UserService,
              private router: Router
  ) { }

  ngOnInit() {
    this.gameImages = new Map<number, any>();
    this.imageReady = [];
    this.posts = [];

    this.userService.userSubject.takeUntil(this.ngUnsubscribe).subscribe(
      user => this.user = user
    );
    this.groupService.currentGroupSubject.takeUntil(this.ngUnsubscribe).subscribe(
      group => this.inGroup = group != null
    );
    this.postService.postsSubject.takeUntil(this.ngUnsubscribe).subscribe(
      posts => {
        this.posts = posts;
        this.getGameImages();
      }
    );
    // this.postService.updatePosts();
  }



  getGameImages() {
    /*
    const array = [];
    for (let i = 0; i < this.posts.length; i++) {
      const id = this.posts[i].activity.game.id;
      this.imageReady.push(false);
      console.log(array.indexOf(id));
      if (array.indexOf(id) < 0 && !this.gameImages.has(id)) {
        array.push(id);
        this.gameService.getGameImage(id).subscribe(img => {
          this.gameImages.set(id, img);
          this.imageReady[i] = true;
          console.log('-----------------------');
          console.log('i: ' + i);
        });
      }
    }
    */

    const array = [];
    for (let i = 0; i < this.posts.length; i++) {
      const id = this.posts[i].activity.game.id;
      if (array.indexOf(id) < 0 && !this.gameImages.has(id)) {
        array.push(id);
        this.gameService.getGameImage(id).subscribe(img => {
          this.gameImages.set(id, img);
          console.log('-----------------------');
          console.log('i: ' + i);
        });
      }
    }
  }

  updatePosts() {
    this.postService.updatePosts();
  }

  joinGroup(groupId: number) {
    this.groupService.joinGroup(groupId)
      .subscribe(response => {
        if (response) {
          this.router.navigate(['/app', { outlets: {spekbar: ['group'] }}], {
            skipLocationChange: true
          });
        }
      });
  }

  getUserInfo(id: number) {
    this.router.navigate(['/app', { outlets: {friends: ['user-info', id] }}], {
      skipLocationChange: true
    });
  }

  newChat(id: number) {
    this.chatService.newChat(ChatType.PRIVATE, id);
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
