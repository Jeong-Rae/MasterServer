package org.codequistify.master.player.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codequistify.master.global.util.BaseTimeEntity;
import org.codequistify.master.player.domain.model.Authority;
import org.codequistify.master.player.domain.model.Permissions;
import org.codequistify.master.player.domain.model.Roles;
import org.codequistify.master.player.domain.vo.Permission;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.codequistify.master.player.domain.vo.Role;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "player_authority")
public class AuthorityEntity extends BaseTimeEntity {
  @Id
  @Column(name = "player_id", nullable = false, columnDefinition = "char(36)")
  private UUID playerId;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "player_authority_role", joinColumns = @JoinColumn(name = "player_uuid"))
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Set<Role> roles;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "player_authority_permission",
      joinColumns = @JoinColumn(name = "player_uuid"))
  @Enumerated(EnumType.STRING)
  @Column(name = "permission", nullable = false)
  private Set<Permission> permissions;

  public static AuthorityEntity from(Authority authority) {
    return AuthorityEntity.builder()
        .playerId(authority.playerId().value())
        .roles(authority.roles().asSet())
        .permissions(authority.permissions().asSet())
        .build();
  }

  public Authority toDomain() {
    return new Authority(
        PlayerId.of(playerId), Roles.fromNullable(roles), Permissions.fromNullable(permissions));
  }
}
