package com.ceos.beatbuddy.domain.member.controller;

import com.ceos.beatbuddy.domain.member.application.AdminService;
import com.ceos.beatbuddy.domain.member.dto.AdminResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "admin용 컨트롤러", description = "관리자용 로그인과 회원가입을 할 수 있습니다 이미 생성된 id를 사용해야합니다")
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS })
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/login")
    @Operation(summary = "id를 통한 토큰 발급", description = "기존에 생성된 id를 통해 토큰을 발급받습니다.")
    @Parameter(description = "미리 생성된 id"
            , content = @Content(mediaType = "text/plain")
            , schema = @Schema(implementation = String.class))
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AdminResponseDto.class)))
    public ResponseEntity<String> login(@RequestBody String id) {
        Long adminId = adminService.findAdmin(id);
        AdminResponseDto responseDto = adminService.createAdminToken(adminId, id);

        ResponseCookie cookie = ResponseCookie.from("refresh", responseDto.getRefresh())
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .maxAge(60 * 60 * 24 * 14)
                .build();

        String jsonResponse = responseDto.getAccess().toString();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        ResponseEntity responseEntity = ResponseEntity.ok().headers(headers).body(jsonResponse);
        return responseEntity;
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody String id) {
        Long adminId = adminService.createAdmin(id);

        String result = "id : " + id;
        return ResponseEntity.ok(result + "\n join success!\n");
    }
}
