package org.codequistify.master.domain.player.domain.authentication;

import org.codequistify.master.domain.player.domain.vo.EncodedPassword;
import org.codequistify.master.domain.player.domain.vo.OAuthAccessToken;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;

public record PlayerAuthentication(
        EncodedPassword encodedPassword,
        OAuthIdentity oauthIdentity,
        RefreshToken refreshToken,
        OAuthAccessToken oauthAccessToken
) {
    public PlayerAuthentication {
        if (encodedPassword == null) {
            encodedPassword = new EncodedPassword("");
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

    public static PlayerAuthentication local(EncodedPassword encodedPassword) {
        return new PlayerAuthentication(encodedPassword, OAuthIdentity.local(), new RefreshToken(""), new OAuthAccessToken(""));
    }

    public PlayerAuthentication encode(EncodedPassword encodedPassword) {
        return new PlayerAuthentication(encodedPassword, oauthIdentity, refreshToken, oauthAccessToken);
    }

    public PlayerAuthentication updateRefreshToken(RefreshToken refreshToken) {
        return new PlayerAuthentication(encodedPassword, oauthIdentity, refreshToken, oauthAccessToken);
    }

    public PlayerAuthentication updateOAuthAccessToken(OAuthAccessToken oauthAccessToken) {
        return new PlayerAuthentication(encodedPassword, oauthIdentity, refreshToken, oauthAccessToken);
    }

    public PlayerAuthentication clearRefreshToken() {
        return updateRefreshToken(new RefreshToken(""));
    }

    public PlayerAuthentication clearOAuthAccessToken() {
        return updateOAuthAccessToken(new OAuthAccessToken(""));
    }
}
