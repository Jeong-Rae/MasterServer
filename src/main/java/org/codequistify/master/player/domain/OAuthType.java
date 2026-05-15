package org.codequistify.master.domain.player.domain.authentication;

import lombok.Getter;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum OAuthType {
    POL(0, "자체"),
    GOOGLE(1, "구글"),
    KAKAO(2, "카카오"),
    NAVER(3, "네이버"),
    GITHUB(4, "깃허브");

    private final int code;
    private final String title;

    OAuthType(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public static OAuthType from(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_OAUTH_CODE, HttpStatus.BAD_REQUEST);
        }

        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_OAUTH_CODE, HttpStatus.BAD_REQUEST));
    }
}
