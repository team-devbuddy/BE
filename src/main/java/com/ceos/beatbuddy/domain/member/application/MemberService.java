package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.member.dto.MemberConsentRequestDTO;
import com.ceos.beatbuddy.domain.member.dto.MemberResponseDTO;
import com.ceos.beatbuddy.domain.member.dto.NicknameRequestDTO;
import com.ceos.beatbuddy.domain.member.dto.RegionRequestDTO;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.global.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣._]*$");

    /**
     * loginId로 유저 식별자 조회
     * 유저가 존재하면 식별자 반환
     * 유저가 존재하지 않으면 회원가입 처리 후 식별자 반환
     *
     * @param loginId
     * @return UserId
     */
    public Long findOrCreateUser(String loginId) {
        Long userId = memberRepository.findByLoginId(loginId);

        if (userId == null) {
            return this.join(loginId);
        } else {
            return userId;
        }
    }

    /**
     * TODO: 회원가입 처리
     * 1. 서비스 약관 동의
     * 2. 온보딩 취향 검사
     * 3. 유저 생성
     * 4. 유저 정보 저장
     * 5. 유저 식별자 반환
     *
     * @param loginId
     * @return UserId
     */
    private Long join(String loginId) {
        //userService.joinAgree();
        //userService.onboard();
        //return userRepository.save(User.builder().loginId(loginId).build()); //TODO: save()의 반환값이 User인지 Long인지 확인 필요
        return null;
    }

    private void isDuplicate(String nickname) {
        if (memberRepository.existsDistinctByNickname(nickname)) {
            throw new CustomException(MemberErrorCode.NICKNAME_ALREADY_EXIST);
        }
        if (memberRepository.existsDistinctByLoginId(nickname)) {
            throw new CustomException(MemberErrorCode.LOGINID_ALREADY_EXIST);
        }
    }

    private void validateNickname(String nickname) {
        if (nickname.length() > 12) {
            throw new CustomException(MemberErrorCode.NICKNAME_OVER_LENGTH);
        }
        if (nickname.contains(" ")) {
            throw new CustomException(MemberErrorCode.NICKNAME_SPACE_EXIST);
        }
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new CustomException(MemberErrorCode.NICKNAME_SYMBOL_EXIST);
        }
    }

    @Transactional
    public MemberResponseDTO saveMemberConsent(Long memberId, MemberConsentRequestDTO memberConsentRequestDTO) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        member.saveConsents(memberConsentRequestDTO.getIsLocationConsent(), memberConsentRequestDTO.getIsMarketingConsent());
        memberRepository.save(member);
        return MemberResponseDTO.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .isLocationConsent(member.isLocationConsent())
                .isMarketingConsent(member.isMarketingConsent())
                .build();
    }

    @Transactional
    public MemberResponseDTO saveAndCheckNickname(Long memberId, NicknameRequestDTO nicknameRequestDTO) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        String nickname = nicknameRequestDTO.getNickname();
        isDuplicate(nickname);
        validateNickname(nickname);
        member.saveNickname(nickname);
        memberRepository.save(member);
        return MemberResponseDTO.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .isLocationConsent(member.isLocationConsent())
                .isMarketingConsent(member.isMarketingConsent())
                .build();
    }

    @Transactional
    public MemberResponseDTO saveRegions(Long memberId, RegionRequestDTO regionRequestDTO) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        List<Region> regions = Arrays.stream(regionRequestDTO.getRegions().split(","))
                .map(Region::fromText)
                .collect(Collectors.toList());
        member.saveRegions(regions);
        memberRepository.save(member);
        return MemberResponseDTO.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .isLocationConsent(member.isLocationConsent())
                .isMarketingConsent(member.isMarketingConsent())
                .build();
    }

}
