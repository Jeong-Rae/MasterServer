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
import org.codequistify.master.domain.player.domain.vo.HashedPassword;
import org.codequistify.master.domain.player.domain.vo.OAuthAccessToken;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerDomainModelTest {

    @Test
    void 플레이어를_생성하면_프로필_인증_인가_상태를_분리해_보관한다() {
        Player player = Player.create(
                PlayerId.newId(),
                new PlayerProfile(
                        new PlayerName("pol"),
                        new PlayerEmail("pol@example.com"),
                        new PlayerExperience(0)),
                PlayerAuthentication.local(new HashedPassword("{bcrypt}encoded")),
                PlayerAuthorization.basicPlayer());

        assertThat(player.profile().name().value()).isEqualTo("pol");
        assertThat(player.profile().email().value()).isEqualTo("pol@example.com");
        assertThat(player.authentication().hashedPassword().value()).isEqualTo("{bcrypt}encoded");
        assertThat(player.authorization().hasRole(PlayerRoleType.PLAYER)).isTrue();
        assertThat(player.authorization().hasAccess(PlayerAccessType.BASIC_PROBLEMS_ACCESS)).isTrue();
        assertThat(player.status()).isEqualTo(PlayerStatus.ACTIVE);
    }

    @Test
    void 상태_전이를_요청하면_허용된_전이만_상태를_변경한다() {
        Player player = Player.create(
                PlayerId.newId(),
                new PlayerProfile(new PlayerName("pol"), new PlayerEmail("pol@example.com"), new PlayerExperience(1)),
                new PlayerAuthentication(
                        new HashedPassword("{bcrypt}encoded"),
                        OAuthIdentity.of(OAuthType.GOOGLE, "google-id"),
                        new RefreshToken("refresh"),
                        new OAuthAccessToken("access")),
                new PlayerAuthorization(
                        PlayerRoles.of(PlayerRoleType.PLAYER),
                        PlayerAccesses.of(PlayerAccessType.BASIC_PROBLEMS_ACCESS)));

        Player locked = player.transition(PlayerStatusTransition.LOCK);
        Player deleted = locked.transition(PlayerStatusTransition.DELETE);

        assertThat(locked.status()).isEqualTo(PlayerStatus.LOCKED);
        assertThat(deleted.status()).isEqualTo(PlayerStatus.DELETED);
        assertThatThrownBy(() -> deleted.transition(PlayerStatusTransition.ACTIVATE))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 저장된_RefreshToken은_Player에게_의미로_질의한다() {
        Player player = Player.create(
                PlayerId.newId(),
                new PlayerProfile(new PlayerName("pol"), new PlayerEmail("pol@example.com"), new PlayerExperience(1)),
                new PlayerAuthentication(
                        new HashedPassword("{bcrypt}encoded"),
                        OAuthIdentity.local(),
                        new RefreshToken("refresh"),
                        new OAuthAccessToken("")),
                PlayerAuthorization.basicPlayer());

        assertThat(player.hasRefreshToken(new RefreshToken("refresh"))).isTrue();
        assertThat(player.hasRefreshToken(new RefreshToken("other"))).isFalse();
    }

    @Test
    void 권한_컬렉션을_생성하면_역할과_접근권한을_저장용_문자열로_반환한다() {
        PlayerAuthorization authorization = new PlayerAuthorization(
                PlayerRoles.of(PlayerRoleType.ADMIN, PlayerRoleType.PLAYER),
                PlayerAccesses.of(PlayerAccessType.MOCK_TESTS_ACCESS));

        assertThat(authorization.hasRole(PlayerRoleType.ADMIN)).isTrue();
        assertThat(authorization.hasAccess(PlayerAccessType.MOCK_TESTS_ACCESS)).isTrue();
        assertThat(authorization.toStoredAuthorities())
                .isEqualTo(java.util.List.of(
                        PlayerRoleType.ADMIN.getRole(),
                        PlayerRoleType.PLAYER.getRole(),
                        PlayerAccessType.MOCK_TESTS_ACCESS.getPermission()));
    }
}
