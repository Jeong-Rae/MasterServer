package org.codequistify.master.global.jwt;

final class TokenConstants {
    static final String AUTHORITIES_CLAIM = "role";
    static final String ISSUER = "api.pol.or.kr";
    static final String AUTHORIZATION_HEADER = "Authorization";
    static final String BEARER_PREFIX = "Bearer ";

    private TokenConstants() {}
}
