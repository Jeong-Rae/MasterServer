package org.codequistify.master.domain.player.dto;

import org.codequistify.master.domain.authentication.dto.SignUpRequest;
import org.codequistify.master.domain.authentication.vo.OAuthData;
import org.codequistify.master.domain.authentication.vo.OAuthResource;
import org.codequistify.master.domain.authentication.vo.OAuthToken;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.EncodedPassword;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDtoMappingTest {

    @Test
    void playerProfileDtoCanBeCreatedFromDomain() {
        Player player = Player.create(
                new PlayerId("018f2fcb-7c0a-7cc0-8a2a-111111111111"),
                new PlayerProfile(new PlayerName("pol"), new PlayerEmail("pol@example.com"), new PlayerExperience(7)),
                PlayerAuthentication.local(new EncodedPassword("encoded")),
                PlayerAuthorization.basicPlayer());

        org.codequistify.master.domain.player.dto.PlayerProfile dto =
                org.codequistify.master.domain.player.dto.PlayerProfile.from(player);

        assertEquals(player.id().value(), dto.id());
        assertEquals(player.profile().name().value(), dto.name());
        assertEquals(player.profile().email().value(), dto.email());
        assertEquals(player.profile().experience().value(), dto.level());
        assertEquals(player.authorization().toStoredAuthorities(), dto.roles());
    }

    @Test
    void signUpRequestCanCreatePlayerDomain() {
        SignUpRequest request = new SignUpRequest("pol", "pol@example.com", "raw-password");

        Player player = request.toPlayer();

        assertEquals(request.name(), player.profile().name().value());
        assertEquals(request.email(), player.profile().email().value());
        assertEquals(request.password(), player.authentication().encodedPassword().value());
        assertEquals(OAuthType.POL, player.authentication().oauthIdentity().type());
    }

    @Test
    void oauthDataCanCreatePlayerDomain() {
        OAuthData oAuthData = OAuthData.of(
                new OAuthToken("access", "refresh", 1L, 2L, "scope", "bearer", "id-token"),
                new OAuthResource("oauth-id", "oauth@example.com", "oauth-name"));

        Player player = oAuthData.toPlayer(OAuthType.GOOGLE);

        assertEquals("oauth-name", player.profile().name().value());
        assertEquals("oauth@example.com", player.profile().email().value());
        assertEquals(OAuthType.GOOGLE, player.authentication().oauthIdentity().type());
        assertEquals("oauth-id", player.authentication().oauthIdentity().id());
    }
}
