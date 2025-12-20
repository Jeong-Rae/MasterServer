package org.codequistify.master.player.application.command;

import java.util.EnumSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.player.application.port.AuthorityRepository;
import org.codequistify.master.player.domain.model.Authority;
import org.codequistify.master.player.domain.model.Permissions;
import org.codequistify.master.player.domain.model.Roles;
import org.codequistify.master.player.domain.vo.Permission;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.codequistify.master.player.domain.vo.Role;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorityCommandService {
  private final AuthorityRepository authorityRepository;

  @Transactional
  public Authority createAuthority(
      UUID playerUuid, EnumSet<Role> roles, EnumSet<Permission> permissions) {
    return authorityRepository.save(
        new Authority(PlayerId.of(playerUuid), Roles.from(roles), Permissions.from(permissions)));
  }

  @Transactional
  public Authority grantRole(UUID playerUuid, Role role) {
    // TODO: session invalidation for role/permission changes is excluded from this refactor.
    PlayerId playerId = PlayerId.of(playerUuid);
    Authority authority = authorityRepository
        .findByPlayerId(playerId)
        .orElseThrow(() -> new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND));
    authority.grantRole(role);
    return authorityRepository.save(authority);
  }

  @Transactional
  public Authority grantPermission(UUID playerUuid, Permission permission) {
    // TODO: session invalidation for role/permission changes is excluded from this refactor.
    PlayerId playerId = PlayerId.of(playerUuid);
    Authority authority = authorityRepository
        .findByPlayerId(playerId)
        .orElseThrow(() -> new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND));
    authority.grantPermission(permission);
    return authorityRepository.save(authority);
  }
}
