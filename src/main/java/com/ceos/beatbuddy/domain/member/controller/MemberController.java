package com.ceos.beatbuddy.domain.member.controller;

import com.ceos.beatbuddy.domain.member.application.MemberService;
import com.ceos.beatbuddy.domain.member.dto.MemberConsentRequestDTO;
import com.ceos.beatbuddy.domain.member.dto.MemberResponseDTO;
import com.ceos.beatbuddy.domain.member.dto.NicknameRequestDTO;
import com.ceos.beatbuddy.domain.member.dto.RegionRequestDTO;
import com.ceos.beatbuddy.global.ResponseTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Member Controller", description = "사용자 컨트롤러\n"
        + "현재는 회원가입 관련 로직만 작성되어 있습니다\n"
        + "추후 사용자 상세 정보, 아카이브를 조회하는 기능이 추가될 수 있습니다")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/onboarding/consent/{memberId}")
    @Operation(summary = "사용자 약관 동의", description = "어플리케이션 약관 동의")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "약관 동의 저장 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<MemberResponseDTO> saveTermConsent(@PathVariable Long memberId,
                                                             @RequestBody MemberConsentRequestDTO memberConsentRequestDTO) {
        return ResponseEntity.ok(memberService.saveMemberConsent(memberId, memberConsentRequestDTO));
    }


    @PostMapping("/onboarding/nickname/{memberId}")
    @Operation(summary = "사용자 닉네임 설정", description = "사용자의 닉네임을 설정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 설정에 성공하였습니다."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<MemberResponseDTO> saveNickname(@PathVariable Long memberId,
                                                          @RequestBody NicknameRequestDTO nicknameRequestDTO) {
        return ResponseEntity.ok(memberService.saveAndCheckNickname(memberId, nicknameRequestDTO));
    }

    @PostMapping("/onboarding/regions/{memberId}")
    @Operation(summary = "사용자 관심지역 설정", description = "사용자의 관심지역을 설정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관심지역 설정에 성공하였습니다."
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않습니다",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<MemberResponseDTO> saveRegions(@PathVariable Long memberId,
                                                         @RequestBody RegionRequestDTO regionRequestDTO) {
        ;
        return ResponseEntity.ok(memberService.saveRegions(memberId, regionRequestDTO));
    }

}
