package com.ceos.beatbuddy.domain.member.controller;

import com.ceos.beatbuddy.domain.member.application.MemberService;
import com.ceos.beatbuddy.domain.member.dto.MemberConsentRequestDTO;
import com.ceos.beatbuddy.domain.member.dto.MemberResponseDTO;
import com.ceos.beatbuddy.domain.member.dto.NicknameRequestDTO;
import com.ceos.beatbuddy.domain.member.dto.RegionRequestDTO;
import com.ceos.beatbuddy.global.ResponseTemplate;
import com.ceos.beatbuddy.global.config.jwt.CustomUserDetails;
import com.ceos.beatbuddy.global.config.jwt.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Member Controller", description = "사용자 컨트롤러\n"
        + "현재는 회원가입 관련 로직만 작성되어 있습니다\n"
        + "추후 사용자 상세 정보, 아카이브를 조회하는 기능이 추가될 수 있습니다")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/onboarding/consent")
    @Operation(summary = "사용자 약관 동의", description = "어플리케이션 약관 동의")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "약관 동의 저장 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<MemberResponseDTO> saveTermConsent(@RequestBody MemberConsentRequestDTO memberConsentRequestDTO) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberService.saveMemberConsent(memberId, memberConsentRequestDTO));
    }


    @PostMapping("/onboarding/nickname/duplicate")
    @Operation(summary = "사용자 닉네임 중복확인", description = "사용자가 입력한 닉네임 중복 여부 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 중복이 아니면 true를 반환합니다."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "409", description = "중복된 닉네임입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<Boolean> isNicknameDuplicate(@RequestBody NicknameRequestDTO nicknameRequestDTO) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberService.isDuplicate(memberId, nicknameRequestDTO));
    }

    @PostMapping("/onboarding/nickname/validate")
    @Operation(summary = "사용자 닉네임 오류 확인", description = "사용자가 입력한 닉네임 사용 가능 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용가능한 닉네임이면 true를 반환합니다."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class))),
            @ApiResponse(responseCode = "400", description = "특수문자, 공백, 길이 조건에 위배되는 것이 있습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<Boolean> isNicknameValidate(@RequestBody NicknameRequestDTO nicknameRequestDTO) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberService.isValidate(memberId, nicknameRequestDTO));
    }

    @PostMapping("/onboarding/nickname")
    @Operation(summary = "사용자 닉네임 저장", description = "사용자가 입력한 닉네임으로 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임을 저장에 성공하면 유저의 정보를 반환합니다."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<MemberResponseDTO> saveNickname(@RequestBody NicknameRequestDTO nicknameRequestDTO) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberService.saveNickname(memberId, nicknameRequestDTO));
    }



    @PostMapping("/onboarding/regions")
    @Operation(summary = "사용자 관심지역 설정", description = "사용자의 관심지역을 설정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관심지역 설정에 성공하였습니다."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<MemberResponseDTO> saveRegions(@RequestBody RegionRequestDTO regionRequestDTO) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(memberService.saveRegions(memberId, regionRequestDTO));
    }

}
