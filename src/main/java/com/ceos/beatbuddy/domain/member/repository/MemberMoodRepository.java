package com.ceos.beatbuddy.domain.member.repository;

import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberMood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberMoodRepository extends JpaRepository<MemberMood, Long> {
    @Query("SELECT mm FROM MemberMood mm WHERE mm.member = :member ORDER BY mm.createdAt DESC LIMIT 1")
    Optional<MemberMood> findLatestMoodByMember(@Param("member") Member member);

    @Query("SELECT mm FROM MemberMood mm WHERE mm.member = :member ORDER BY mm.createdAt DESC")
    List<MemberMood> findAllByMember(@Param("member") Member member);

    boolean existsByMember(Member member);

    void deleteByMember(Member member);
}
