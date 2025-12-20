package org.codequistify.master.player.application.command;

import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.player.application.port.AuthorityRepository;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.authority.Authority;
import org.codequistify.master.player.domain.authority.Permission;
import org.codequistify.master.player.domain.authority.Role;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorityCommandService {
  private final AuthorityRepository authorityRepository;

  @Transactional
  public Authority createAuthority(
      PlayerId playerId, EnumSet<Role> roles, EnumSet<Permission> permissions) {
    return authorityRepository.save(new Authority(playerId, roles, permissions));
  }

  @Transactional
  public Authority grantRole(PlayerId playerId, Role role) {
    // TODO: session invalidation for role/permission changes is excluded from this refactor.
    Authority authority =
        authorityRepository
            .findByPlayerId(playerId)
            .orElseThrow(
                () -> new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND));
    authority.grantRole(role);
    return authorityRepository.save(authority);
  }

  @Transactional
  public Authority grantPermission(PlayerId playerId, Permission permission) {
    // TODO: session invalidation for role/permission changes is excluded from this refactor.
    Authority authority =
        authorityRepository
            .findByPlayerId(playerId)
            .orElseThrow(
                () -> new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND));
    authority.grantPermission(permission);
    return authorityRepository.save(authority);
  }
}
