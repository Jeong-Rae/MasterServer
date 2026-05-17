package org.codequistify.master.domain.player.security;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.PlayerStatusTransition;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.RawPassword;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;
import org.codequistify.master.domain.player.dto.UpdatePasswordRequest;
import org.codequistify.master.domain.player.persistence.PlayerRepository;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerDetailsService implements UserDetailsService {
    private final PlayerRepository playerRepository;
    private final PlayerPrincipalMapper playerPrincipalMapper;
    private final HashPasswordEncoder hashPasswordEncoder;

    private final Logger LOGGER = LoggerFactory.getLogger(PlayerDetailsService.class);

    @Override
    @Transactional
    @LogExecutionTime
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("[loadUserByUsername] loadUserByUsername: {}", username);
        return playerPrincipalMapper.toPrincipal(findOnePlayerById(username));
    }

    @Transactional
    public UserDetails loadUserByEmail(String email) {
        return playerPrincipalMapper.toPrincipal(findOnePlayerByEmail(email));
    }

    @Transactional
    @LogMonitoring
    public void resetPassword(PlayerPrincipal principal, UpdatePasswordRequest request) {
        RawPassword rawPassword = middleLevelPassword(request.password());
        Player player =
                findOnePlayerById(principal.id().value()).withHashedPassword(hashPasswordEncoder.encode(rawPassword));
        playerRepository.save(player);
        LOGGER.info("[resetPassword] Player: {}, 비밀번호 초기화 성공", player.id().value());
    }

    @Transactional
    @LogMonitoring
    public void updatePassword(PlayerPrincipal principal, UpdatePasswordRequest request) {
        RawPassword newPassword = middleLevelPassword(request.password());
        Player player = findOnePlayerById(principal.id().value());
        if (!matchesPassword(request.rawPassword(), player)) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_OR_PASSWORD, HttpStatus.BAD_REQUEST);
        }

        playerRepository.save(player.withHashedPassword(hashPasswordEncoder.encode(newPassword)));
        LOGGER.info("[updatePassword] Player: {}, 비밀번호 재설정 성공", player.id().value());
    }

    @Transactional
    public Player save(Player player) {
        return playerRepository.save(player);
    }

    @Transactional
    public boolean isExistPlayer(String email) {
        return playerRepository.existsPlayerByEmail(new PlayerEmail(email));
    }

    @Transactional
    public Optional<OAuthType> checkOAuthType(String email) {
        return playerRepository.findAuthenticationTypeByEmail(new PlayerEmail(email));
    }

    @Transactional
    public Player findOnePlayerByEmail(String email) {
        return playerRepository.findPlayerByEmail(new PlayerEmail(email)).orElseThrow(() -> {
            LOGGER.info("[findOnePlayerByEmail] 존재하지 않는 email 입니다. email: {}", email);
            return new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        });
    }

    @Transactional
    public Player findOnePlayerById(String id) {
        return playerRepository.findPlayerById(new PlayerId(id)).orElseThrow(() -> {
            LOGGER.info("[findOnePlayerById] 존재하지 않는 player 입니다. id: {}", id);
            return new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        });
    }

    @Transactional
    public void updateRefreshToken(String id, String refreshToken) {
        playerRepository.saveRefreshToken(new PlayerId(id), new RefreshToken(refreshToken));
    }

    @Transactional
    @LogMonitoring
    public void deletePlayer(PlayerPrincipal principal) {
        Player player = findOnePlayerById(principal.id().value()).transition(PlayerStatusTransition.DELETE);
        playerRepository.save(player);
    }

    public boolean matchesPassword(String rawPassword, Player player) {
        return hashPasswordEncoder.matches(
                new RawPassword(rawPassword), player.authentication().hashedPassword());
    }

    private RawPassword middleLevelPassword(String rawPassword) {
        try {
            return RawPassword.middleLevel(rawPassword);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.PASSWORD_POLICY_VIOLATION, HttpStatus.BAD_REQUEST);
        }
    }
}
