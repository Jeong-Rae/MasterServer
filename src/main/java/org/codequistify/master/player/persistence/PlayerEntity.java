package org.codequistify.master.domain.player.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.global.uuid.UUIDv7Generator;
import org.codequistify.master.shared.persistence.BaseTimeEntity;
import org.hibernate.annotations.ColumnDefault;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "player")
public class PlayerEntity extends BaseTimeEntity {
    @Id
    @Column(name = "player_id")
    private UUID id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Builder.Default
    @Column(name = "oauth_type", length = 30)
    @ColumnDefault("'POL'")
    @Enumerated(EnumType.STRING)
    private OAuthType oAuthType = OAuthType.POL;

    @Column(name = "oauth_id")
    private String oAuthId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "player_authority",
            joinColumns = @JoinColumn(name = "player_id"),
            foreignKey = @ForeignKey(name = "fk_player_authority_player"),
            uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_player_authority",
                        columnNames = {"player_id", "authority"})
            })
    @Column(name = "authority", nullable = false, length = 80)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Builder.Default
    @Column(name = "locked")
    @ColumnDefault("false")
    private Boolean locked = false;

    @Column(name = "oauth_access_token")
    private String oAuthAccessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Builder.Default
    @Column(name = "exp")
    @ColumnDefault("0")
    private Integer exp = 0;

    @PrePersist
    protected void onPrePersist() {
        if (id == null) {
            id = UUID.fromString(UUIDv7Generator.generate());
        }
    }
}
