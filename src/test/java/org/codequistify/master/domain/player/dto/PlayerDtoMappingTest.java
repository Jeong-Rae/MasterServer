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
import org.codequistify.master.domain.player.domain.vo.HashedPassword;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerDtoMappingTest {

    @Test
    void 플레이어_도메인을_응답_객체로_변환하면_프로필과_권한을_반환한다() {
        Player player = Player.create(
                new PlayerId("018f2fcb-7c0a-7cc0-8a2a-111111111111"),
                new PlayerProfile(new PlayerName("pol"), new PlayerEmail("pol@example.com"), new PlayerExperience(7)),
                PlayerAuthentication.local(new HashedPassword("{bcrypt}encoded")),
                PlayerAuthorization.basicPlayer());

        org.codequistify.master.domain.player.dto.PlayerProfile dto =
                org.codequistify.master.domain.player.dto.PlayerProfile.from(player);

        assertThat(dto.id()).isEqualTo(player.id().value());
        assertThat(dto.name()).isEqualTo(player.profile().name().value());
        assertThat(dto.email()).isEqualTo(player.profile().email().value());
        assertThat(dto.level()).isEqualTo(player.profile().experience().value());
        assertThat(dto.roles()).isEqualTo(player.authorization().toStoredAuthorities());
    }

    @Test
    void 회원가입_요청을_플레이어로_변환하면_로컬_인증_도메인을_반환한다() {
        SignUpRequest request = new SignUpRequest("pol", "pol@example.com", "raw-password");

        Player player = request.toPlayer();

        assertThat(player.profile().name().value()).isEqualTo(request.name());
        assertThat(player.profile().email().value()).isEqualTo(request.email());
        assertThat(player.authentication().hashedPassword().value()).isEmpty();
        assertThat(player.authentication().oauthIdentity().type()).isEqualTo(OAuthType.POL);
    }

    @Test
    void 외부_인증_데이터를_플레이어로_변환하면_외부_인증_도메인을_반환한다() {
        OAuthData oAuthData = OAuthData.of(
                new OAuthToken("access", "refresh", 1L, 2L, "scope", "bearer", "id-token"),
                new OAuthResource("oauth-id", "oauth@example.com", "oauthname"));

        Player player = oAuthData.toPlayer(OAuthType.GOOGLE);

        assertThat(player.profile().name().value()).isEqualTo("oauthname");
        assertThat(player.profile().email().value()).isEqualTo("oauth@example.com");
        assertThat(player.authentication().oauthIdentity().type()).isEqualTo(OAuthType.GOOGLE);
        assertThat(player.authentication().oauthIdentity().id()).isEqualTo("oauth-id");
    }
}
