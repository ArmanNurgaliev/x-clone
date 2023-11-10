package ru.arman.backendxclone.service.impl;

import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import ru.arman.backendxclone.model.SecurityUser;
import ru.arman.backendxclone.service.TokenService;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final JwtEncoder jwtEncoder;
    private final int jwtExpiration = 1;
    private final int refreshTokenExpiration = 8;

    private String generateToken(SecurityUser user, int expiration) {
        Instant now = Instant.now();

        String scope = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> !auth.startsWith("ROLE"))
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expiration, ChronoUnit.HOURS))
                .subject(user.getUsername())
                .claim("scope", scope)
                .build();

        JwtEncoderParameters encodedParameters =
                JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claims);
        return jwtEncoder.encode(encodedParameters).getTokenValue();
    }

    @Override
    public String generateAccessToken(SecurityUser user) {
        return generateToken(user, jwtExpiration);
    }

    @Override
    public String generateRefreshToken(SecurityUser user) {
        return generateToken(user, refreshTokenExpiration);
    }

    @Override
    public String parseToken(String token) {
        token = token.substring(7);
        try {
            SignedJWT decodedJWT = SignedJWT.parse(token);
            return decodedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
