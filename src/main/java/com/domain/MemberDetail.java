package com.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@Table(name = "member_detail")
@ToString(of = {"age", "address"}, exclude = {"id", "member"})
@NoArgsConstructor
public class MemberDetail {

    @EmbeddedId
    private DetailPk id;

    @MapsId("id")
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private int age;

    private String address;

    public MemberDetail (Member member, MemberType memberType, int age, String address) {
        this.member = member;
//        member.setMemberDetail(this);

        DetailPk pk = new DetailPk(member.getId(), memberType);
        this.id = pk;

        this.age = age;
        this.address = address;
    }

    @Getter
    @Embeddable
    @NoArgsConstructor
    public static class DetailPk {
        private Long id;

        @Column(name = "type")
        @Enumerated(EnumType.STRING)
        private MemberType memberType;

        public DetailPk (Long id, MemberType memberType) {
            this.id = id;
            this.memberType = memberType;
        }
    }
}
