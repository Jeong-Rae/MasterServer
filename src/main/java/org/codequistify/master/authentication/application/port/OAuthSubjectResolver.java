package org.codequistify.master.authentication.application.port;

import java.util.Objects;
import org.codequistify.master.authentication.domain.model.OAuthProvider;
import org.codequistify.master.authentication.domain.vo.Email;

public interface OAuthSubjectResolver {
  OAuthSubject resolve(OAuthProvider provider, String authorizationCode);

  record OAuthSubject(String subject, Email email) {
    public OAuthSubject {
      Objects.requireNonNull(subject, "subject must not be null");
      Objects.requireNonNull(email, "email must not be null");
    }
  }
}
