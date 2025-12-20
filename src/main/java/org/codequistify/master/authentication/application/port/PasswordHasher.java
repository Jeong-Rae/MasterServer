package org.codequistify.master.authentication.application.port;

import org.codequistify.master.authentication.domain.vo.PasswordHash;

public interface PasswordHasher {
  PasswordHash hash(String rawPassword);

  boolean matches(String rawPassword, PasswordHash passwordHash);
}
