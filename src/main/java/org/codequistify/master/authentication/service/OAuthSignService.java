package org.codequistify.master.domain.authentication.service;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.authentication.vo.OAuthCode;
import org.codequistify.master.domain.authentication.vo.OAuthData;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.vo.OAuthAccessToken;
import org.codequistify.master.domain.player.dto.PlayerProfileResponse;
import org.codequistify.master.domain.player.security.PlayerDetailsService;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuthSignService {
    private final OAuthStrategies oAuthStrategies;
    private final PlayerDetailsService playerDetailsService;

    public String loginUrl(OAuthType type) {
        return oAuthStrategies.find(type).loginUrl();
    }

    @Transactional
    @LogMonitoring
    public PlayerProfileResponse login(OAuthType type, OAuthCode code) {
        OAuthData oAuthData = oAuthStrategies.find(type).fetchOAuthData(code);
        Player player = findOrCreatePlayer(type, oAuthData)
                .updateOAuthAccessToken(new OAuthAccessToken(oAuthData.token().access_token()));

        player = playerDetailsService.save(player);
        return PlayerProfileResponse.from(player);
    }

    private Player findOrCreatePlayer(OAuthType type, OAuthData oAuthData) {
        try {
            return playerDetailsService.findOnePlayerByEmail(
                    oAuthData.resource().email());
        } catch (BusinessException exception) {
            if (exception.getErrorCode() != ErrorCode.PLAYER_NOT_FOUND) {
                throw exception;
            }
            return oAuthData.toPlayer(type);
        }
    }
}
