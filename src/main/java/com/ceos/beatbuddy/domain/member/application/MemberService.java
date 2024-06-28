package com.ceos.beatbuddy.domain.member.application;

import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

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
}
