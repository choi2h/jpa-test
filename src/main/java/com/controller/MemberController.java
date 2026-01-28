package com.controller;

import com.controller.request.MemberInfoRequest;
import com.domain.Member;
import com.domain.MemberDetail;
import com.domain.MemberType;
import com.repository.MemberDetailRepository;
import com.repository.MemberRepository;
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
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberDetailRepository memberDetailRepository;

    /**
     * 현재 SQL : insert(member) -> select(memberDetail) with join(member_id) -> insert(memberDetail)
     * 단일 PK (@GeneratedValue)일 때
     * 	•	ID가 null → 무조건 신규
     * 	•	SELECT 필요 X
     *
     * 지금 구조 (복합 PK)
     * 	•	PK가 이미 있음 (null 아님)
     * 	•	JPA는 신규/기존을 구분할 수 없음
     *
     * 그래서 Hibernate는:
     * 	1.	PK로 먼저 조회 (SELECT)
     * 	2.	없으면 → INSERT
     * 	3.	있으면 → UPDATE
     */
    @PostMapping
    @Transactional
    public ResponseEntity<Void> saveMember(@RequestBody MemberInfoRequest request) {
        log.info("Receive request for save member. name={}", request.getName());

        Member member = new Member(request.getName());
        MemberDetail memberDetail = new MemberDetail(member, MemberType.MEMBER, request.getAge(), request.getAddress());

        Member saveMember = memberRepository.save(member);
        log.info("Success to save member. memberId={}", saveMember.getId());

        MemberDetail saveMemberDetail = memberDetailRepository.save(memberDetail);
        log.info("Success to save memberDetail. memberDetail.member={}", saveMemberDetail.getMember().getId());

        return ResponseEntity.noContent().build();
    }


}
