package com.repository;

import com.domain.Post;
import com.dto.PostCommentInterface;
import com.dto.PostInfoDto;
import com.dto.PostInfoInterface;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findPageByMember_Id(Long memberId, Pageable pageable);
    Slice<Post> findSliceByMember_Id(Long memberId, Pageable pageable);


    @Query("""
        select p
        from Post p
        join fetch p.comments
    """)
    Page<Post> findAllWithComments(Pageable pageable);

    @Query("""
      select new com.dto.PostInfoDto(p.id, p.title)
      from Post p
      where p.member.id = :memberId
    """)
    List<PostInfoDto> findAllByMemberId(@Param("memberId") Long memberId);
    List<PostInfoInterface> findByMember_Id(Long id);
    // select p.id, p.title from post
//    List<PostCommentInterface> findByMember_Id(Long id);

    <T> List<T> findTargetByMember_Id(Long id, Class<T> type);
}
