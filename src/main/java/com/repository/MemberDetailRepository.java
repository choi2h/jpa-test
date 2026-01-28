package com.repository;

import com.domain.MemberDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDetailRepository extends JpaRepository<MemberDetail, MemberDetail.DetailPk> {

}
