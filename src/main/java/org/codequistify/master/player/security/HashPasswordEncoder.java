package org.codequistify.master.domain.player.security;

import org.codequistify.master.domain.player.domain.vo.HashedPassword;
import org.codequistify.master.domain.player.domain.vo.RawPassword;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class HashPasswordEncoder {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public HashedPassword encode(RawPassword rawPassword) {
        return HashedPassword.bcrypt(passwordEncoder.encode(rawPassword.value()));
    }

    public boolean matches(RawPassword rawPassword, HashedPassword hashedPassword) {
        return passwordEncoder.matches(rawPassword.value(), hashedPassword.encodedBody());
    }
}
