package ru.arman.backendxclone.service;

import ru.arman.backendxclone.dto.*;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.exception.ValidationException;

public interface AuthService {
    MessageResponseDto registerUser(RegistrationDto registrationDto) throws ValidationException;

    LoginResponseDto login(LoginDto loginDto) throws UserException;
    RefreshTokenResponse refresh(String authorization);
}
