package ru.arman.backendxclone.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.arman.backendxclone.dto.*;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.exception.ValidationException;
import ru.arman.backendxclone.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) throws UserException {
        return ResponseEntity.ok(userService.login(loginDto));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDto> register(@Valid @RequestBody RegistrationDto registrationDto) throws UserException, ValidationException {
        return ResponseEntity.ok(userService.registerUser(registrationDto));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return ResponseEntity.ok(userService.refresh(refreshToken));
    }
}
