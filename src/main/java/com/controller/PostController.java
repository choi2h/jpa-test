package com.controller;

import com.controller.request.PostInfoRequest;
import com.controller.response.PostCommentResponse;
import com.controller.response.PostInfoResponse;
import com.controller.response.PostInfosResponse;
import com.domain.Comment;
import com.domain.Member;
import com.domain.Post;
import com.dto.PostCommentInterface;
import com.dto.PostInfoDto;
import com.dto.PostInfoInterface;
import com.repository.CommentRepository;
import com.repository.MemberRepository;
import com.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * Post 등록
     */
    @PostMapping("/v1")
    @Transactional
    public ResponseEntity<Void> savePost(@RequestBody PostInfoRequest request) {
        log.info("Receive request for save post v1. member={}, title={}",
                request.getMemberId(), request.getTitle());

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Not exist member"));

        Post post = new Post(member, request.getTitle(), request.getContents());
        postRepository.save(post);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/v2")
    @Transactional
    public ResponseEntity<Void> savePostV2(@RequestBody PostInfoRequest request) {
        log.info("Receive request for save post v2. member={}, title={}",
                request.getMemberId(), request.getTitle());

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Not exist member"));

        Post post1 = new Post(member, request.getTitle(), request.getContents());
        Comment comment1 = new Comment("hi1");
        Comment comment2 = new Comment("hi2");
        post1.addComment(comment1);
        post1.addComment(comment2);
        postRepository.save(post1);


        Post post2 = new Post(member, request.getTitle(), request.getContents());
        Comment comment3 = new Comment("hi3");
        post2.addComment(comment3);
        postRepository.save(post2);


        return ResponseEntity.noContent().build();
    }

    /**
     * Post 페이징 조회
     */

    /*
        Hibernate:
        select
            p1_0.post_id,
            p1_0.contents,
            p1_0.member_id,
            p1_0.title
        from
            post p1_0
        left join
            member m1_0
                on m1_0.member_id=p1_0.member_id
        where
            m1_0.member_id=?
        limit
            ?, ?
        Hibernate:
            select
                count(p1_0.post_id)
            from
                post p1_0
            left join
                member m1_0
                    on m1_0.member_id=p1_0.member_id
            where
                m1_0.member_id=?
        Hibernate:
            select
                m1_0.member_id,
                md1_0.member_id,
                md1_0.type,
                md1_0.address,
                md1_0.age,
                m1_0.name
            from
                member m1_0
            left join
                member_detail md1_0
                    on m1_0.member_id=md1_0.member_id
            where
                m1_0.member_id=?
     */
    @GetMapping("/page")
    public ResponseEntity<PostInfosResponse> getPagePosts(@RequestParam("author") Long memberId) {
        Pageable pageable = PageRequest.of(0, 2);
        log.info("Success to post page. memberId={}", memberId);
        Page<Post> posts = postRepository.findPageByMember_Id(memberId, pageable);

        PostInfosResponse response = new PostInfosResponse(posts);
        return ResponseEntity.ok(response);
    }

    /*
        Hibernate:
        select
            p1_0.post_id,
            c1_0.post_id,
            c1_0.comment_id,
            c1_0.contents,
            p1_0.contents,
            p1_0.member_id,
            p1_0.title
        from
            post p1_0
        join
            comment c1_0
                on p1_0.post_id=c1_0.post_id
        Hibernate:
            select
                count(p1_0.post_id)
            from
                post p1_0
            join
                comment c1_0
                    on p1_0.post_id=c1_0.post_id
        Hibernate:
            select
                m1_0.member_id,
                md1_0.member_id,
                md1_0.type,
                md1_0.address,
                md1_0.age,
                m1_0.name
            from
                member m1_0
            left join
                member_detail md1_0
                    on m1_0.member_id=md1_0.member_id
            where
                m1_0.member_id=?
     */
    // org.hibernate.orm.query : HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
    // 	•	DB에서 전체 Post + Comment 다 가져옴
    //	•	Hibernate가 메모리에서 페이징
    //	•	데이터 많으면 → OOM 직행
    @GetMapping("/page/v2")
    public ResponseEntity<PostInfosResponse> getPagePostsV2() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Post> posts = postRepository.findAllWithComments(pageable);

        PostInfosResponse response = new PostInfosResponse(posts);
        return ResponseEntity.ok(response);
    }


    /*
        Hibernate:
        select
            p1_0.post_id,
            p1_0.contents,
            p1_0.member_id,
            p1_0.title
        from
            post p1_0
        left join
            member m1_0
                on m1_0.member_id=p1_0.member_id
        where
            m1_0.member_id=?
        limit
            ?, ?

        Hibernate:
            select
                m1_0.member_id,
                md1_0.member_id,
                md1_0.type,
                md1_0.address,
                md1_0.age,
                m1_0.name
            from
                member m1_0
            left join
                member_detail md1_0
                    on m1_0.member_id=md1_0.member_id
            where
                m1_0.member_id=?
     */
    @GetMapping("/slice")
    public ResponseEntity<PostInfosResponse> getSlicePosts(@RequestParam("author") Long memberId) {
        Pageable pageable = PageRequest.of(0, 2);
        log.info("Success to post slice. memberId={}", memberId);

        Slice<Post> posts = postRepository.findSliceByMember_Id(memberId, pageable);
        PostInfosResponse response = new PostInfosResponse(posts);
        return ResponseEntity.ok(response);
    }

    /**
     * Post 단건 조회
     */
    @GetMapping("/v1/{id}")
    public ResponseEntity<Post> getPostV1(@PathVariable("id") Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not exist post"));

        log.info("Success to get post v1. id={}", id);

        return ResponseEntity.ok(post);
    }

    @GetMapping("/v2/{id}")
    public ResponseEntity<PostInfoResponse> getPostV2(@PathVariable("id") Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not exist post"));

        log.info("Success to get post v2. id={}", id);

        PostInfoResponse response = new PostInfoResponse(post);
        return ResponseEntity.ok(response);
    }

    /**
     * Post Projection
     */
    @GetMapping("/pro/query/{memberId}")
    public ResponseEntity<List<PostInfoDto>> getPostDto(@PathVariable("memberId") Long memberId) {
        List<PostInfoDto> response = postRepository.findAllByMemberId(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pro/{memberId}")
    public ResponseEntity<List<PostInfoDto>> getPostItf(@PathVariable("memberId") Long memberId) {
        List<PostInfoInterface> infos = postRepository.findByMember_Id(memberId);
        List<PostInfoDto> response = new ArrayList<>();
        for (PostInfoInterface info : infos) {
            response.add(new PostInfoDto(info.getId(), info.getTitle()));
        }
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/pro/{memberId}")
//    public ResponseEntity<List<PostCommentResponse>> getPostItfV2(@PathVariable("memberId") Long memberId) {
//        List<PostCommentInterface> postComments = postRepository.findByMember_Id(memberId);
//
//        List<PostCommentResponse> response = new ArrayList<>();
//        for (PostCommentInterface postComment : postComments) {
//            response.add(new PostCommentResponse(postComment));
//        }
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/pro/v2/{memberId}")
    public ResponseEntity<List<PostCommentResponse>> getPostDynamic(@PathVariable("memberId") Long memberId) {
        List<PostCommentInterface> postComments = postRepository.findTargetByMember_Id(memberId,
                PostCommentInterface.class);

        List<PostCommentResponse> response = new ArrayList<>();
        for (PostCommentInterface postComment : postComments) {
            response.add(new PostCommentResponse(postComment));
        }
        return ResponseEntity.ok(response);
    }
}
