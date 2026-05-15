package org.codequistify.master.domain.authentication.service;

import org.codequistify.master.domain.authentication.vo.OAuthCode;
import org.codequistify.master.domain.authentication.vo.OAuthData;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;

public interface OAuthStrategy {
    OAuthType type();

    String loginUrl();

    OAuthData fetchOAuthData(OAuthCode code);
}
