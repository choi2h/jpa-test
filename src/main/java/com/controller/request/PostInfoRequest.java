package com.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostInfoRequest {
    private Long memberId;
    private String title;
    private String contents;
}
