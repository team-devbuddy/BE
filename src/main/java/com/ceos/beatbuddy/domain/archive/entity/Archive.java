package com.ceos.beatbuddy.domain.archive.entity;


import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberGenre;
import com.ceos.beatbuddy.domain.member.entity.MemberMood;
import com.ceos.beatbuddy.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Archive extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long archiveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberId")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberMoodId")
    private MemberMood memberMood;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberGenreId")
    private MemberGenre memberGenre;
}
