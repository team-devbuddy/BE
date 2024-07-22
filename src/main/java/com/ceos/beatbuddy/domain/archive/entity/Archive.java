package com.ceos.beatbuddy.domain.archive.entity;


import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberGenre;
import com.ceos.beatbuddy.domain.member.entity.MemberMood;
import com.ceos.beatbuddy.domain.member.entity.RegionConverter;
import com.ceos.beatbuddy.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberMoodId")
    private MemberMood memberMood;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberGenreId")
    private MemberGenre memberGenre;

    @Convert(converter = RegionConverter.class)
    private List<Region> regions;

    public void updateArchive(MemberGenre memberGenre, MemberMood memberMood, List<Region> regions){
        this.memberGenre = memberGenre;
        this.memberMood = memberMood;
        this.regions = regions;
    }
}
