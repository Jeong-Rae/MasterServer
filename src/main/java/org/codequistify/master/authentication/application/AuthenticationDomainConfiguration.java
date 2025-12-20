package org.codequistify.master.authentication.application;

import org.codequistify.master.authentication.domain.service.RegistrationDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationDomainConfiguration {
  @Bean
  public RegistrationDomainService registrationDomainService() {
    return new RegistrationDomainService();
  }
}
