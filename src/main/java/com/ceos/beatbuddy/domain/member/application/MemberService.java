package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.member.dto.MemberConsentRequestDTO;
import com.ceos.beatbuddy.domain.member.dto.MemberResponseDTO;
import com.ceos.beatbuddy.domain.member.dto.NicknameDTO;
import com.ceos.beatbuddy.domain.member.dto.Oauth2MemberDto;
import com.ceos.beatbuddy.domain.member.dto.OnboardingResponseDto;
import com.ceos.beatbuddy.domain.member.dto.RegionRequestDTO;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberGenre;
import com.ceos.beatbuddy.domain.member.entity.MemberMood;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberGenreErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberMoodErrorCode;
import com.ceos.beatbuddy.domain.member.repository.MemberGenreRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberMoodRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.global.CustomException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberMoodRepository memberMoodRepository;
    private final MemberGenreRepository memberGenreRepository;
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣._]*$");

    @Value("${iamport.api.key}")
    private String imp_key;

    @Value("${iamport.api.secret}")
    private String imp_secret;

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

    @Transactional
    public String getToken() {
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://api.iamport.kr/users/getToken";
        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("imp_key", imp_key);
        tokenRequest.put("imp_secret", imp_secret);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, String>> tokenEntity = new HttpEntity<>(tokenRequest, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(tokenUrl, HttpMethod.POST, tokenEntity, Map.class);

        Map body = tokenResponse.getBody();
        Map response = (Map) body.get("response");
        return response.get("access_token").toString();
    }

    public ResponseEntity<Map> getUserData(String token, String imp_uid) {
        RestTemplate restTemplate = new RestTemplate();
        String certificationUrl = UriComponentsBuilder.fromHttpUrl("https://api.iamport.kr/certifications/{imp_uid}")
                .buildAndExpand(imp_uid)
                .toUriString();

        HttpHeaders certificationHeaders = new HttpHeaders();
        certificationHeaders.set("Authorization", "Bearer "+token);

        HttpEntity<String> certificationEntity = new HttpEntity<>(certificationHeaders);
        ResponseEntity<Map> exchange = restTemplate.exchange(certificationUrl, HttpMethod.GET, certificationEntity,
                Map.class);

        return exchange;
    }

    public void verifyUserData(ResponseEntity<Map> userData, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String userName = userData.getBody().get("name").toString();
        if (!userName.equals(member.getNickname())) {
            throw new CustomException(MemberErrorCode.USERNAME_NOT_MATCH);
        }

        String userBirth = userData.getBody().get("birth").toString();
        LocalDate userBirthDate = LocalDate.parse(userBirth, formatter);

        if (Period.between(userBirthDate, LocalDate.now()).getYears() >= 19) {
            member.setAdultUser();
        } else {
            throw new CustomException(MemberErrorCode.MEMBER_NOT_ADULT);
        }

    }

    public OnboardingResponseDto isOnboarding(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));

        return getOnboardingMap(member);
    }

    private OnboardingResponseDto getOnboardingMap(Member member) {
        OnboardingResponseDto responseDto = new OnboardingResponseDto();

        if (member.getIsAdult()) {
            responseDto.setAdultCert();
        } else {
            return responseDto;
        }

        if (memberGenreRepository.existsByMember(member)) {
            responseDto.setGenre();
        } else {
            return responseDto;
        }
        if (memberMoodRepository.existsByMember(member)) {
            responseDto.setMood();
        } else {
            return responseDto;
        }
        if (memberRepository.existsRegionsByMemberId(member.getMemberId())) {
            responseDto.setRegion();
        }
        return responseDto;
    }

    public Boolean isTermConsent(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        if (member.getIsLocationConsent() && member.getIsMarketingConsent()) {
            return true;
        }
        return false;
    }

    public Boolean getNicknameSet(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        return member.getSetNewNickname();
    }

    public NicknameDTO getNickname(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        return NicknameDTO.builder()
                .nickname(member.getNickname()).build();
    }

    public Member getUser(Long memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
    }

    public Boolean getCertification(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));

        return member.getIsAdult();
    }

    @Transactional
    public void tempVerify(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        member.setAdultUser();
    }

    public List<String> getPreferences(Long memberId){
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));

        MemberGenre memberGenre = memberGenreRepository.findLatestGenreByMember(member).orElseThrow(()->new CustomException(MemberGenreErrorCode.MEMBER_GENRE_NOT_EXIST));
        MemberMood memberMood = memberMoodRepository.findLatestMoodByMember(member).orElseThrow(()->new CustomException(MemberMoodErrorCode.MEMBER_MOOD_NOT_EXIST));
        List<String> trueGenreElements = Vector.getTrueGenreElements(memberGenre.getGenreVector());
        List<String> trueMoodElements = Vector.getTrueMoodElements(memberMood.getMoodVector());
        List<String> preferenceList = new ArrayList<>(trueGenreElements);
        preferenceList.addAll(trueMoodElements);

        return preferenceList;
    }
}
