package org.codequistify.master.global.jwt.dto;

import java.util.List;

public record TokenInfoResponse(String aud, List<String> roles, String iat, String exp, String iss, Boolean isValid) {}
