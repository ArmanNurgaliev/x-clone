package ru.arman.backendxclone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
