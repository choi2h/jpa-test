package com.dto;

import lombok.Getter;
import lombok.Value;

/**
 * ⚠️ 이 방식의 치명적인 단점 (그래서 잘 안 씀)
 *
 * 1. 필드 순서에 강하게 의존
 *    엔티티 필드 순서 바뀌면 → 런타임 에러
 * 2. 조인/계산 컬럼 불가
 * 3. 명시성이 없음
 *    쿼리 읽기 힘듦, 유지보수 지옥
 * 4. IDE/컴파일 타임 체크 없음
 */
@Value
public class PostInfoDto {
    private final Long id;
    private final String title;
}
