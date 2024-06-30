package com.ceos.beatbuddy.domain.member.entity;

import com.ceos.beatbuddy.domain.vector.entity.Vector;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private String loginId;

    private String nickname;


}
