package org.codequistify.master.domain.player.domain.profile;

import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerName;

public record PlayerProfile(PlayerName name, PlayerEmail email, PlayerExperience experience) {
    public PlayerProfile increaseExperience(int point) {
        return new PlayerProfile(name, email, experience.increase(point));
    }

    public PlayerProfile clearPersonalInformation() {
        return new PlayerProfile(new PlayerName("deleted"), new PlayerEmail("deleted@example.invalid"), experience);
    }
}
