package org.codequistify.master.domain.player.persistence;

import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.PlayerStatus;
import org.codequistify.master.domain.player.domain.authentication.OAuthIdentity;
import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.*;
import org.springframework.stereotype.Component;

@Component
public class PlayerPersistenceMapper {
    public Player toDomain(PlayerEntity entity) {
        return new Player(
                new PlayerId(entity.getId()),
                new PlayerProfile(
                        new PlayerName(entity.getName()),
                        new PlayerEmail(entity.getEmail()),
                        new PlayerExperience(entity.getExp() == null ? 0 : entity.getExp())),
                new PlayerAuthentication(
                        new EncodedPassword(entity.getPassword()),
                        OAuthIdentity.of(entity.getOAuthType(), entity.getOAuthId()),
                        new RefreshToken(entity.getRefreshToken()),
                        new OAuthAccessToken(entity.getOAuthAccessToken())),
                PlayerAuthorization.fromStoredAuthorities(entity.getRoles()),
                PlayerStatus.fromLocked(Boolean.TRUE.equals(entity.getLocked())));
    }

    public PlayerEntity toEntity(Player player) {
        return PlayerEntity.builder()
                .id(player.id().value())
                .name(player.profile().name().value())
                .email(player.profile().email().value())
                .password(player.authentication().encodedPassword().value())
                .oAuthType(player.authentication().oauthIdentity().type())
                .oAuthId(player.authentication().oauthIdentity().id())
                .roles(player.authorization().toStoredAuthorities())
                .locked(player.status().locked())
                .oAuthAccessToken(player.authentication().oauthAccessToken().value())
                .refreshToken(player.authentication().refreshToken().value())
                .exp(player.profile().experience().value())
                .build();
    }
}
