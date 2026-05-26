package org.codequistify.master.domain.player.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.codequistify.master.domain.player.dto.UpdatePasswordRequest;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.shared.response.BasicResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Player Authentication")
public interface PlayerAuthenticationApi {

    @Operation(summary = "비밀번호 재설정", description = "비밀번호를 재설정합니다. 기존 비밀번호 인증이 필요합니다. password에 새로운 비밀번호를 담아 보냅니다.")
    ResponseEntity<BasicResponse> updateMyPassword(
            @AuthenticationPrincipal PlayerPrincipal principal, @Valid @RequestBody UpdatePasswordRequest request);

    @Operation(summary = "비밀번호 초기화", description = "비밀번호를 초기화합니다. password에 새로운 비밀번호를 담아 보냅니다.")
    ResponseEntity<BasicResponse> resetMyPassword(
            @AuthenticationPrincipal PlayerPrincipal principal, @Valid @RequestBody UpdatePasswordRequest request);

    @Operation(summary = "계정 삭제", description = "계정을 삭제합니다. 즉시 해당 계정에 대한 데이터가 소실됩니다. 복구는 불가능합니다.")
    ResponseEntity<BasicResponse> deleteMyAccount(@AuthenticationPrincipal PlayerPrincipal principal);
}
