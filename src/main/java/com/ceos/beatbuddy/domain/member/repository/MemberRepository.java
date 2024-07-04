package com.ceos.beatbuddy.domain.member.repository;

import com.ceos.beatbuddy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByLoginId(String loginId);
    Optional<Member> findByMemberId(Long memberId);
    Boolean existsDistinctByNickname(String nickname);
    public Boolean existsDistinctByLoginId(String loginId);
}
