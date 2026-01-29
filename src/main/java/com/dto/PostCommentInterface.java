package com.dto;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;

public interface PostCommentInterface {
    Long getId();
    String getTitle();

    @Value("#{target.member.name}")
    String getMemberName();

    List<CommentInterface> getComments();

    interface CommentInterface {
        @Value("#{target.contents}")
        String getContents();
    }

//    MemberView getMember();
//    List<CommentView> getComments();
//
//    interface MemberView {
//        String getName();
//    }
//
//    interface CommentView {
//        String getContents();
//    }
}
