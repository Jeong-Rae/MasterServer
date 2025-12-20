package org.codequistify.master.authentication.application.query;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.authentication.application.port.AuthenticationAccountRepository;
import org.codequistify.master.authentication.domain.vo.Email;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationQueryService {
  private final AuthenticationAccountRepository authenticationAccountRepository;

  public boolean isEmailAvailable(String email) {
    return !authenticationAccountRepository.existsByEmail(Email.of(email));
  }
}
