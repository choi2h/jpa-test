package com.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInfoRequest {
    private String name;
    private int age;
    private String address;
}
