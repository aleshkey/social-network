import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

const COMMENT_API = 'http://localhost:8080/api/comment/';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private http: HttpClient) { }

  addCommentToPost(postId: number, message: string){
    return this.http.post(COMMENT_API+postId+'/create', {
      message: message
    });
  }

  getCommentsToPost(postId: number){
    return this.http.get(COMMENT_API+postId+'/all');
  }

  delete(commentId: number){
    return this.http.post(COMMENT_API+commentId+'/delete', null);
  }

}
