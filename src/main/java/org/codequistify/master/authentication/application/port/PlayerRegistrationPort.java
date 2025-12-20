package org.codequistify.master.authentication.application.port;

import org.codequistify.master.player.domain.vo.PlayerId;

public interface PlayerRegistrationPort {
  void registerNewPlayer(PlayerId playerId, String nickname);
}
