package com.controller.response;

import com.domain.Post;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

@Getter
public class PostInfosResponse {
    private final List<PostInfoResponse> posts;
    private final int count;
    private final int pageNum;
    private final int totalPage;
    private final boolean isFirst;
    private final boolean isLast;

    public PostInfosResponse(Page<Post> postPage) {
        this.posts = new ArrayList<>();
        for (Post post : postPage.getContent()) {
            this.posts.add(new PostInfoResponse(post));
        }
        this.count = postPage.getNumber();
        this.pageNum = postPage.getNumber();
        this.totalPage = postPage.getTotalPages();
        this.isFirst = pageNum == 0;
        this.isLast = pageNum == totalPage-1;
    }

    public PostInfosResponse(Slice<Post> postSlice) {
        this.posts = new ArrayList<>();
        for (Post post : postSlice.getContent()) {
            this.posts.add(new PostInfoResponse(post));
        }
        this.count = postSlice.getNumber();
        this.pageNum = postSlice.getNumber();
        this.totalPage = 0;
        this.isFirst = postSlice.isFirst();
        this.isLast = postSlice.isLast();
    }

}
