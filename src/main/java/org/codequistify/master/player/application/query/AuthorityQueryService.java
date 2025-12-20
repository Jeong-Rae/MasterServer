package org.codequistify.master.player.application.query;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.player.application.port.AuthorityRepository;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.authority.Authority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorityQueryService {
  private final AuthorityRepository authorityRepository;

  public Optional<Authority> findAuthority(PlayerId playerId) {
    return authorityRepository.findByPlayerId(playerId);
  }
}
