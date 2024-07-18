package com.ceos.beatbuddy.domain.member.repository;

import com.ceos.beatbuddy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByMemberId(Long memberId);
    Boolean existsDistinctByNickname(String nickname);
    Optional<Member> findByNickname(String nickname);
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Member m WHERE m.memberId = :memberId AND SIZE(m.regions) > 0")
    Boolean existsRegionsByMember(Member member);
}
