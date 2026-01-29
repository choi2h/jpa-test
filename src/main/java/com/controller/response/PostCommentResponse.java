package com.controller.response;

import com.dto.PostCommentInterface;
import com.dto.PostCommentInterface.CommentInterface;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class PostCommentResponse {
    private final Long id;
    private final String title;
    private final String author;
    private final List<String> comments;

    public PostCommentResponse(PostCommentInterface postComment) {
        this.id = postComment.getId();
        this.title = postComment.getTitle();
        this.author = postComment.getMemberName();
        this.comments = new ArrayList<>();
        for (CommentInterface commentInterface : postComment.getComments()) {
            this.comments.add(commentInterface.getContents());
        }
    }

//    public PostCommentResponse(PostCommentInterface postComment) {
//        this.id = postComment.getId();
//        this.title = postComment.getTitle();
//        this.author = postComment.getMember().getName();
//        this.comments = new ArrayList<>();
//        for (CommentView commentInterface : postComment.getComments()) {
//            this.comments.add(commentInterface.getContents());
//        }
//    }

}
