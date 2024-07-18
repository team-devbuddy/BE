package com.ceos.beatbuddy.domain.archive.repository;

import com.ceos.beatbuddy.domain.archive.entity.Archive;
import com.ceos.beatbuddy.domain.heartbeat.entity.Heartbeat;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberGenre;
import com.ceos.beatbuddy.domain.member.entity.MemberMood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    @Query("SELECT a FROM Archive a WHERE a.member = :member ORDER BY a.updatedAt DESC")
    List<Archive> findByMember(@Param("member") Member member);

    boolean existsByMemberAndMemberMoodAndMemberGenre(Member member, MemberMood memberMood, MemberGenre memberGenre);
}
