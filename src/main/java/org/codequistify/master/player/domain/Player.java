package org.codequistify.master.domain.player.domain;

import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.HashedPassword;
import org.codequistify.master.domain.player.domain.vo.OAuthAccessToken;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;

public record Player(
        PlayerId id,
        PlayerProfile profile,
        PlayerAuthentication authentication,
        PlayerAuthorization authorization,
        PlayerStatus status
) {
    public Player {
        if (id == null || profile == null || authentication == null || authorization == null || status == null) {
            throw new IllegalArgumentException("player aggregate fields must not be null");
        }
    }

    public static Player create(
            PlayerId id,
            PlayerProfile profile,
            PlayerAuthentication authentication,
            PlayerAuthorization authorization
    ) {
        return new Player(id, profile, authentication, authorization, PlayerStatus.ACTIVE);
    }

    public Player withHashedPassword(HashedPassword hashedPassword) {
        return new Player(id, profile, authentication.withHashedPassword(hashedPassword), authorization, status);
    }

    public Player updateRefreshToken(RefreshToken refreshToken) {
        return new Player(id, profile, authentication.updateRefreshToken(refreshToken), authorization, status);
    }

    public Player clearRefreshToken() {
        return new Player(id, profile, authentication.clearRefreshToken(), authorization, status);
    }

    public boolean hasRefreshToken(RefreshToken refreshToken) {
        return authentication.hasRefreshToken(refreshToken);
    }

    public Player updateOAuthAccessToken(OAuthAccessToken oauthAccessToken) {
        return new Player(id, profile, authentication.updateOAuthAccessToken(oauthAccessToken), authorization, status);
    }

    public Player clearOAuthAccessToken() {
        return new Player(id, profile, authentication.clearOAuthAccessToken(), authorization, status);
    }

    public boolean hasOAuthAccessToken() {
        return authentication.hasOAuthAccessToken();
    }

    public OAuthAccessToken oauthAccessToken() {
        return authentication.oauthAccessToken();
    }

    public Player increaseExperience(int point) {
        return new Player(id, profile.increaseExperience(point), authentication, authorization, status);
    }

    public Player transition(PlayerStatusTransition transition) {
        PlayerStatus next = transition.apply(status);
        PlayerProfile nextProfile = next == PlayerStatus.DELETED ? profile.clearPersonalInformation() : profile;
        return new Player(id, nextProfile, authentication.clearOAuthAccessToken().clearRefreshToken(), authorization, next);
    }
}
