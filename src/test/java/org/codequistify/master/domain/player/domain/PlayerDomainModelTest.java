package org.codequistify.master.domain.player.domain;

import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authentication.OAuthIdentity;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.authorization.PlayerAccessType;
import org.codequistify.master.domain.player.domain.authorization.PlayerAccesses;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoles;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.EncodedPassword;
import org.codequistify.master.domain.player.domain.vo.OAuthAccessToken;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDomainModelTest {

    @Test
    void playerSeparatesProfileAuthenticationAndAuthorization() {
        Player player = Player.create(
                PlayerId.newId(),
                new PlayerProfile(
                        new PlayerName("pol"),
                        new PlayerEmail("pol@example.com"),
                        new PlayerExperience(0)),
                PlayerAuthentication.local(new EncodedPassword("encoded")),
                PlayerAuthorization.basicPlayer());

        assertEquals("pol", player.profile().name().value());
        assertEquals("pol@example.com", player.profile().email().value());
        assertEquals("encoded", player.authentication().encodedPassword().value());
        assertTrue(player.authorization().hasRole(PlayerRoleType.PLAYER));
        assertTrue(player.authorization().hasAccess(PlayerAccessType.BASIC_PROBLEMS_ACCESS));
        assertEquals(PlayerStatus.ACTIVE, player.status());
    }

    @Test
    void playerStatusChangesOnlyThroughTransitionEnum() {
        Player player = Player.create(
                PlayerId.newId(),
                new PlayerProfile(new PlayerName("pol"), new PlayerEmail("pol@example.com"), new PlayerExperience(1)),
                new PlayerAuthentication(
                        new EncodedPassword("encoded"),
                        OAuthIdentity.of(OAuthType.GOOGLE, "google-id"),
                        new RefreshToken("refresh"),
                        new OAuthAccessToken("access")),
                new PlayerAuthorization(
                        PlayerRoles.of(PlayerRoleType.PLAYER),
                        PlayerAccesses.of(PlayerAccessType.BASIC_PROBLEMS_ACCESS)));

        Player locked = player.transition(PlayerStatusTransition.LOCK);
        Player deleted = locked.transition(PlayerStatusTransition.DELETE);

        assertEquals(PlayerStatus.LOCKED, locked.status());
        assertEquals(PlayerStatus.DELETED, deleted.status());
        assertThrows(IllegalStateException.class, () -> deleted.transition(PlayerStatusTransition.ACTIVATE));
    }

    @Test
    void authorizationWrapsDomainCollections() {
        PlayerAuthorization authorization = new PlayerAuthorization(
                PlayerRoles.of(PlayerRoleType.ADMIN, PlayerRoleType.PLAYER),
                PlayerAccesses.of(PlayerAccessType.MOCK_TESTS_ACCESS));

        assertTrue(authorization.hasRole(PlayerRoleType.ADMIN));
        assertTrue(authorization.hasAccess(PlayerAccessType.MOCK_TESTS_ACCESS));
        assertEquals(
                java.util.List.of(
                        PlayerRoleType.ADMIN.getRole(),
                        PlayerRoleType.PLAYER.getRole(),
                        PlayerAccessType.MOCK_TESTS_ACCESS.getPermission()),
                authorization.toStoredAuthorities());
    }
}
