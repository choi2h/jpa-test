package com.controller.response;

import com.domain.Post;
import lombok.Getter;

@Getter
public class PostInfoResponse {
    private final Long id;
    private final String title;
    private final String author;
    private final String contents;

    public PostInfoResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.author = post.getMember().getName();
    }
}
