package org.codequistify.master.player.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codequistify.master.global.util.BaseTimeEntity;
import org.codequistify.master.player.domain.model.Profile;
import org.codequistify.master.player.domain.vo.Nickname;
import org.codequistify.master.player.domain.vo.PlayerId;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "player_profile",
    indexes = {@Index(name = "idx_player_profile_nickname", columnList = "nickname", unique = true)
    })
public class ProfileEntity extends BaseTimeEntity {
  @Id
  @Column(name = "player_id", nullable = false, columnDefinition = "char(36)")
  private UUID playerId;

  @Column(name = "nickname", nullable = false, unique = true, length = 50)
  private String nickname;

  public static ProfileEntity from(Profile profile) {
    return ProfileEntity.builder()
        .playerId(profile.playerId().value())
        .nickname(profile.nickname().value())
        .build();
  }

  public Profile toDomain() {
    return new Profile(PlayerId.of(playerId), new Nickname(nickname));
  }
}
