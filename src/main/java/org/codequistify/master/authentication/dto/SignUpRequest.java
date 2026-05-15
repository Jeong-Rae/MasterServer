package org.codequistify.master.domain.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.HashedPassword;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;

public record SignUpRequest(
        @NotBlank String name,
        @NotBlank @Email(message = "4101") String email,
        @NotBlank String password
) {
    public Player toPlayer() {
        return Player.create(
                PlayerId.newId(),
                new PlayerProfile(
                        new PlayerName(name),
                        new PlayerEmail(email),
                        new PlayerExperience(0)),
                PlayerAuthentication.local(new HashedPassword("")),
                PlayerAuthorization.basicPlayer());
    }
}
