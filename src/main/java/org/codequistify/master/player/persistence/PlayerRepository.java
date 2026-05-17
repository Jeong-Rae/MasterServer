package org.codequistify.master.domain.player.persistence;

import java.util.List;
import java.util.Optional;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;

public interface PlayerRepository {
    Player save(Player player);

    List<Player> findAllPlayers();

    Optional<Player> findPlayerById(PlayerId id);

    Optional<Player> findPlayerByEmail(PlayerEmail email);

    Optional<OAuthType> findAuthenticationTypeByEmail(PlayerEmail email);

    boolean existsPlayerByEmail(PlayerEmail email);

    boolean existsProfileName(PlayerName name);

    void saveRefreshToken(PlayerId id, RefreshToken refreshToken);
}
