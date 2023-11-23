package ru.arman.backendxclone.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.arman.backendxclone.dto.*;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.exception.ValidationException;
import ru.arman.backendxclone.model.Role;
import ru.arman.backendxclone.model.SecurityUser;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.RoleRepository;
import ru.arman.backendxclone.repository.UserRepository;
import ru.arman.backendxclone.service.AuthService;
import ru.arman.backendxclone.service.TokenService;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public MessageResponseDto registerUser(RegistrationDto registrationDto) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        Optional<User> isEmailExists = userRepository.findByEmail(registrationDto.getEmail());
        if (isEmailExists.isPresent())
            errors.put("email", "Email is already taken");

        Optional<User> isNameExists = userRepository.findByName(registrationDto.getName());
        if (isNameExists.isPresent())
            errors.put("name", "Name is already taken");

        Optional<User> isLoginExists = userRepository.findByLogin(registrationDto.getLogin());
        if (isLoginExists.isPresent())
            errors.put("login", "Login is already taken");

        if (!errors.isEmpty())
            throw new ValidationException(errors);

        User newUser = new User();
        newUser.setEmail(registrationDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setName(registrationDto.getName());
        newUser.setLogin(registrationDto.getLogin());
        newUser.setDob(registrationDto.getDob());
        newUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        Role role = roleRepository.findByName("USER");
        newUser.getRoles().add(role);

        User savedUser = userRepository.save(newUser);

        return new MessageResponseDto("User with name " + savedUser.getName() + " created");
    }

    @Override
    public LoginResponseDto login(LoginDto loginDto) throws UserException {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getName(), loginDto.getPassword())
            );

            SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(auth.getName());

            String accessToken = tokenService.generateAccessToken(user);
            String refreshToken = tokenService.generateRefreshToken(user);
            return new LoginResponseDto(userRepository.findByName(auth.getName()).get(), accessToken, refreshToken);

        } catch(AuthenticationException e){
            throw new UserException("Name or password not valid");
        }
    }

    @Override
    public RefreshTokenResponse refresh(String token) {
        String name = tokenService.parseToken(token);
        SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(name);

        String access_token = tokenService.generateAccessToken(user);
        String refresh_token = tokenService.generateRefreshToken(user);

        return new RefreshTokenResponse(access_token, refresh_token);
    }
}
