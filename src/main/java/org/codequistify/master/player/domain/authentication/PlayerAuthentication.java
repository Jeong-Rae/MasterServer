package org.codequistify.master.domain.player.domain.authentication;

import org.codequistify.master.domain.player.domain.vo.HashedPassword;
import org.codequistify.master.domain.player.domain.vo.OAuthAccessToken;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;

public record PlayerAuthentication(
        HashedPassword hashedPassword,
        OAuthIdentity oauthIdentity,
        RefreshToken refreshToken,
        OAuthAccessToken oauthAccessToken
) {
    public PlayerAuthentication {
        if (hashedPassword == null) {
            hashedPassword = new HashedPassword("");
        }
        if (oauthIdentity == null) {
            oauthIdentity = OAuthIdentity.local();
        }
        if (refreshToken == null) {
            refreshToken = new RefreshToken("");
        }
        if (oauthAccessToken == null) {
            oauthAccessToken = new OAuthAccessToken("");
        }
    }

    public static PlayerAuthentication local(HashedPassword hashedPassword) {
        return new PlayerAuthentication(hashedPassword, OAuthIdentity.local(), new RefreshToken(""), new OAuthAccessToken(""));
    }

    public PlayerAuthentication withHashedPassword(HashedPassword hashedPassword) {
        return new PlayerAuthentication(hashedPassword, oauthIdentity, refreshToken, oauthAccessToken);
    }

    public PlayerAuthentication updateRefreshToken(RefreshToken refreshToken) {
        return new PlayerAuthentication(hashedPassword, oauthIdentity, refreshToken, oauthAccessToken);
    }

    public PlayerAuthentication updateOAuthAccessToken(OAuthAccessToken oauthAccessToken) {
        return new PlayerAuthentication(hashedPassword, oauthIdentity, refreshToken, oauthAccessToken);
    }

    public PlayerAuthentication clearRefreshToken() {
        return updateRefreshToken(new RefreshToken(""));
    }

    public PlayerAuthentication clearOAuthAccessToken() {
        return updateOAuthAccessToken(new OAuthAccessToken(""));
    }
}
