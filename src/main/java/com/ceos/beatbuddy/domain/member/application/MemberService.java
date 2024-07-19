package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.member.dto.MemberConsentRequestDTO;
import com.ceos.beatbuddy.domain.member.dto.MemberResponseDTO;
import com.ceos.beatbuddy.domain.member.dto.NicknameDTO;
import com.ceos.beatbuddy.domain.member.dto.Oauth2MemberDto;
import com.ceos.beatbuddy.domain.member.dto.OnboardingResponseDto;
import com.ceos.beatbuddy.domain.member.dto.RegionRequestDTO;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.repository.MemberGenreRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberMoodRepository;
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
    private final MemberMoodRepository memberMoodRepository;
    private final MemberGenreRepository memberGenreRepository;
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣._]*$");

    /**
     * loginId로 유저 식별자 조회 유저가 존재하면 식별자 반환 유저가 존재하지 않으면 회원가입 처리 후 식별자 반환
     *
     * @param loginId
     * @param name
     * @return UserId
     */
    @Transactional
    public Oauth2MemberDto findOrCreateUser(String loginId, String name) throws CustomException {
        Member member = memberRepository.findByLoginId(loginId)
                .orElse(null);
        if (member == null) {
            return Oauth2MemberDto.of(this.join(loginId, name));
        } else {
            return Oauth2MemberDto.of(member);
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
     * @param name
     * @return UserId
     */
    private Member join(String loginId, String name) {
        return memberRepository.save(
                Member.builder()
                        .loginId(loginId)
                        .realName(name)
                        .role("USER")
                        .nickname(name)
                        .build());
    }


    public Boolean isDuplicate(Long memberId, NicknameDTO nicknameDTO) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        String nickname = nicknameDTO.getNickname();
        if (memberRepository.existsDistinctByNickname(nickname)) {
            throw new CustomException(MemberErrorCode.NICKNAME_ALREADY_EXIST);
        }
        return true;
    }

    public Boolean isValidate(Long memberId, NicknameDTO nicknameDTO) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        String nickname = nicknameDTO.getNickname();
        if (nickname.length() > 12) {
            throw new CustomException(MemberErrorCode.NICKNAME_OVER_LENGTH);
        }
        if (nickname.contains(" ")) {
            throw new CustomException(MemberErrorCode.NICKNAME_SPACE_EXIST);
        }
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new CustomException(MemberErrorCode.NICKNAME_SYMBOL_EXIST);
        }
        return true;
    }

    @Transactional
    public MemberResponseDTO saveMemberConsent(Long memberId, MemberConsentRequestDTO memberConsentRequestDTO) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        member.saveConsents(memberConsentRequestDTO.getIsLocationConsent(),
                memberConsentRequestDTO.getIsMarketingConsent());
        memberRepository.save(member);
        return MemberResponseDTO.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .isLocationConsent(member.getIsLocationConsent())
                .isMarketingConsent(member.getIsMarketingConsent())
                .build();
    }

    @Transactional
    public MemberResponseDTO saveNickname(Long memberId, NicknameDTO nicknameDTO) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        String nickname = nicknameDTO.getNickname();
        member.saveNickname(nickname);
        memberRepository.save(member);
        return MemberResponseDTO.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .isLocationConsent(member.getIsLocationConsent())
                .isMarketingConsent(member.getIsMarketingConsent())
                .build();
    }





    @Transactional
    public MemberResponseDTO saveRegions(Long memberId, RegionRequestDTO regionRequestDTO) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        List<Region> regions = Arrays.stream(regionRequestDTO.getRegions().split(","))
                .map(Region::fromText)
                .collect(Collectors.toList());
        member.saveRegions(regions);
        memberRepository.save(member);
        return MemberResponseDTO.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .isLocationConsent(member.getIsLocationConsent())
                .isMarketingConsent(member.getIsMarketingConsent())
                .build();
    }

    public OnboardingResponseDto isOnboarding(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));

        return getOnboardingMap(member);
    }

    private OnboardingResponseDto getOnboardingMap(Member member) {
        OnboardingResponseDto responseDto = new OnboardingResponseDto(); 

        if (memberGenreRepository.existsByMember(member)) {
            responseDto.setGenre();
        }
        else {
            return responseDto;
        }
        if (memberMoodRepository.existsByMember(member)){
            responseDto.setMood();
        }
        else {
            return responseDto;
        }
        if(memberRepository.existsRegionsByMember(member))
        {
            responseDto.setRegion();
        }
        return responseDto;
    }

    public Boolean isTermConsent(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        if(member.getIsLocationConsent() && member.getIsMarketingConsent())
        {
            return true;
        }
        return false;
    }

    public Boolean getNicknameSet(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        return member.getSetNewNickname();
    }

    public NicknameDTO getNickname(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        return NicknameDTO.builder()
                .nickname(member.getNickname()).build();
    }
}
