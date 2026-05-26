package org.codequistify.master.domain.authentication.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.codequistify.master.shared.persistence.BaseTimeEntity;
import org.hibernate.annotations.ColumnDefault;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "email_verification",
        indexes = {@Index(name = "idx_email_verification_lookup", columnList = "email, used, type, created_at")})
public class EmailVerificationEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 64)
    private String code;

    @Builder.Default
    @Column(name = "verified", nullable = false)
    @ColumnDefault("false")
    private Boolean verified = false;

    @Builder.Default
    @Column(name = "used", nullable = false)
    @ColumnDefault("false")
    private Boolean used = false;

    @Column(name = "type", nullable = false, length = 30)
    @Enumerated(value = EnumType.STRING)
    private EmailVerificationType emailVerificationType;
}
