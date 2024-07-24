package com.ceos.beatbuddy.domain.heartbeat.repository;

import com.ceos.beatbuddy.domain.heartbeat.entity.Heartbeat;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HeartbeatRepository extends JpaRepository<Heartbeat, Long> {
    @Query("SELECT hb FROM Heartbeat hb WHERE hb.member = :member AND hb.venue = :venue")
    Optional<Heartbeat> findByMemberVenue(@Param("member") Member member, @Param("venue") Venue venue);

    @Query("SELECT hb FROM Heartbeat hb WHERE hb.member = :member ORDER BY hb.createdAt DESC")
    List<Heartbeat> findByMember(@Param("member") Member member);
}
