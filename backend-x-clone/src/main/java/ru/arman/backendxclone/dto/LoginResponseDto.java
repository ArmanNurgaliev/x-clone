package ru.arman.backendxclone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.arman.backendxclone.model.User;

@AllArgsConstructor
@Getter
public class LoginResponseDto {
    private User user;
    private String accessToken;
    private String refreshToken;
}
