package ru.arman.backendxclone.service;

import org.springframework.security.core.Authentication;
import ru.arman.backendxclone.model.SecurityUser;

public interface TokenService {
    String generateAccessToken(SecurityUser user);
    String generateRefreshToken(SecurityUser user);
    String parseToken(String token);
}
