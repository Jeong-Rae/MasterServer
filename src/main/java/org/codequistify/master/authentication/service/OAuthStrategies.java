package org.codequistify.master.domain.authentication.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class OAuthStrategies {
    private final Map<OAuthType, OAuthStrategy> strategies;

    public OAuthStrategies(List<OAuthStrategy> strategies) {
        this.strategies =
                strategies.stream().collect(Collectors.toUnmodifiableMap(OAuthStrategy::type, strategy -> strategy));
    }

    public OAuthStrategy find(OAuthType type) {
        OAuthStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new BusinessException(ErrorCode.INVALID_OAUTH_CODE, HttpStatus.BAD_REQUEST);
        }
        return strategy;
    }
}
