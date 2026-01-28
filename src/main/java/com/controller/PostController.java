package com.controller;

import com.controller.request.PostInfoRequest;
import com.domain.Comment;
import com.domain.Member;
import com.domain.Post;
import com.repository.MemberRepository;
import com.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Void> savePost(@RequestBody PostInfoRequest request) {
        log.info("Receive request for save post. member={}, title={}", request.getMemberId(), request.getTitle());

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Not exist member"));

        Post post = new Post(member, request.getTitle(), request.getContents());
        postRepository.save(post);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/v2")
    @Transactional
    public ResponseEntity<Void> savePostV2(@RequestBody PostInfoRequest request) {
        log.info("Receive request for save post. member={}, title={}", request.getMemberId(), request.getTitle());

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Not exist member"));

        Post post1 = new Post(member, request.getTitle(), request.getContents());
        Comment comment1 = new Comment("hi1");
        Comment comment2 = new Comment("hi2");
        post1.addComment(comment1);
        post1.addComment(comment2);

        Post post2 = new Post(member, request.getTitle(), request.getContents());

        postRepository.save(post1);
        postRepository.save(post2);

        return ResponseEntity.noContent().build();
    }

}
