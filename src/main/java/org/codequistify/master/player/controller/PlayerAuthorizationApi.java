package org.codequistify.master.domain.player.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.shared.response.BasicResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "Player Authorization")
public interface PlayerAuthorizationApi {

    @Operation(summary = "관리자 권한 확인", description = "해당 유저가 관리자 권한을 가지고 있는지 검증합니다.")
    ResponseEntity<BasicResponse> getMyAdminAuthorization(@AuthenticationPrincipal PlayerPrincipal principal);
}
