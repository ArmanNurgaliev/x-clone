package ru.arman.backendxclone.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.arman.backendxclone.dto.*;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.exception.ValidationException;
import ru.arman.backendxclone.model.Role;
import ru.arman.backendxclone.model.SecurityUser;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.RoleRepository;
import ru.arman.backendxclone.repository.UserRepository;
import ru.arman.backendxclone.service.TokenService;

import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServerImpl userDetailsServer;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private Role role;

    private Authentication auth;

    private final String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYWRtaW4iLCJleHAiOjE2OTk1MDUyMzcsImlhdCI6MTY5OTUwMTYzNywic2NvcGUiOiJVU0VSIn0.EenB1If-IuPQXXizh72U6r7qBbqgwOICyMQvmXUom0fGwBRgiNJ3UZUdOUwMD_I54BfBh3rFFyAQx7QIFAEhsA";
    private final String refreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYWRtaW4iLCJleHAiOjE2OTk3MDUyMzcsImlhdCI6MTY5OTUwMTYzNywic2NvcGUiOiJVU0VSIn0.HEybYMvZY3qT7wqEo-t6_TR7Wc0aY_DikpdjElYV5_qryydmAPBs3KWJubVg6gxeOm1I8mOlE879rMHI9be1xw";


    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("USER");

        user = new User();
        user.setUser_id(1L);
        user.setName("user1");
        user.setLogin("user123");
        user.setEmail("user1@mail.ru");
        user.setPassword(passwordEncoder.encode("pass"));
        user.getRoles().add(role);

        auth = new UsernamePasswordAuthenticationToken(user, user.getPassword());
    }

    @Test
    void registerUserTest_shouldReturnMessage() throws ValidationException {
        when(userRepository.save(any())).thenReturn(user);

        RegistrationDto registrationDto =
                new RegistrationDto("user1", "user123", "user1@mail.ru", "pass", new Date(System.currentTimeMillis()));
        MessageResponseDto messageResponseDto = authService.registerUser(registrationDto);

        assertEquals("User with name " + registrationDto.getName() + " created", messageResponseDto.getMessage());
    }

    @Test
    void registerUserTest_shouldReturnErrors() {
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findByLogin(any())).thenReturn(Optional.ofNullable(user));

        RegistrationDto registrationDto =
                new RegistrationDto("user1", "user123", "user1@mail.ru", "pass", new Date(System.currentTimeMillis()));
        ValidationException thrown = assertThrows(ValidationException.class, () -> authService.registerUser(registrationDto));

        assertEquals(3, thrown.getErrors().size());
        assertEquals("Email is already taken", thrown.getErrors().get("email"));
        assertEquals("Name is already taken", thrown.getErrors().get("name"));
        assertEquals("Login is already taken", thrown.getErrors().get("login"));
    }

    @Test
    void loginTest_shouldReturnLoginResponseDto() throws UserException {
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(user));
        when(tokenService.generateAccessToken(any())).thenReturn(accessToken);
        when(tokenService.generateRefreshToken(any())).thenReturn(refreshToken);

        LoginDto loginDto = new LoginDto(user.getName(), user.getPassword());
        LoginResponseDto login = authService.login(loginDto);

        assertEquals(user, login.getUser());
        assertEquals(accessToken, login.getAccessToken());
        assertEquals(refreshToken, login.getRefreshToken());
    }

    @Test
    void refreshTokenTest_shouldReturnRefreshToken() {
        when(tokenService.parseToken(any())).thenReturn(user.getName());
        when(userDetailsServer.loadUserByUsername(any())).thenReturn(new SecurityUser(user));
        when(tokenService.generateAccessToken(any())).thenReturn(accessToken);
        when(tokenService.generateRefreshToken(any())).thenReturn(refreshToken);

        RefreshTokenResponse refresh = authService.refresh(refreshToken);

        assertEquals(accessToken, refresh.getAccessToken());
    }
}