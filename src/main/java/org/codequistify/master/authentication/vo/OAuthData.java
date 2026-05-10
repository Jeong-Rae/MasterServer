package org.codequistify.master.domain.authentication.vo;

import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.OAuthIdentity;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.HashedPassword;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;

public record OAuthData(
        OAuthToken token,
        OAuthResource resource
) {
    public static OAuthData of(OAuthToken token, OAuthResource resource) {
        return new OAuthData(token, resource);
    }

    public Player toPlayer(OAuthType oAuthType) {
        return Player.create(
                PlayerId.newId(),
                new PlayerProfile(
                        new PlayerName(resource.name()),
                        new PlayerEmail(resource.email()),
                        new PlayerExperience(0)),
                new PlayerAuthentication(
                        new HashedPassword(""),
                        OAuthIdentity.of(oAuthType, resource.id()),
                        null,
                        null),
                PlayerAuthorization.basicPlayer());
    }
}
