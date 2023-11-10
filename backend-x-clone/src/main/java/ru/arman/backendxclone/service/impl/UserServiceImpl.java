package ru.arman.backendxclone.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.arman.backendxclone.dto.*;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.exception.ValidationException;
import ru.arman.backendxclone.model.Role;
import ru.arman.backendxclone.model.SecurityUser;
import ru.arman.backendxclone.model.TweetData;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.RoleRepository;
import ru.arman.backendxclone.repository.TweetDtoRepository;
import ru.arman.backendxclone.repository.TweetRepository;
import ru.arman.backendxclone.repository.UserRepository;
import ru.arman.backendxclone.service.TokenService;
import ru.arman.backendxclone.service.UserService;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RoleRepository roleRepository;
    private TweetDtoRepository tweetDtoRepository;

    private final ModelMapper mapper;
    private final TweetRepository tweetRepository;

    @Override
    public Page<User> getAllUsers(Integer page, Integer rows, String name) {
        return userRepository.findByQuery(PageRequest.of(page, rows, Sort.by("name")), name);
    }

    @Override
    public MessageResponseDto registerUser(RegistrationDto registrationDto) throws UserException, ValidationException {
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
    public User getUserById(Long user_id) throws UserException {
        return userRepository.findById(user_id)
                .orElseThrow(() -> new UserException("User not found with id: " + user_id));
    }

    @Override
    public User getUserByName(String name) throws UserException {
        return userRepository.findByName(name)
                .orElseThrow(() -> new UserException("User not found with name: " + name));
    }

    @Override
    public User getUserByLogin(String login) throws UserException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserException("User not found with login: " + login));
    }

    @Override
    public MessageResponseDto followUser(Authentication authentication, Long following_id) throws UserException {
        User user = getUserByName(authentication.getName());
        User following = getUserById(following_id);

        if (user.getFollowing().contains(following) || user.equals(following))
            throw new UserException("You are already followed user " + following.getName());

        user.followUser(following);
        userRepository.save(user);

        return new MessageResponseDto(
                String.format("You are following %s", following.getName()));
    }

    @Override
    public MessageResponseDto unFollowUser(Authentication authentication, Long following_id) throws UserException {
        User user = getUserByName(authentication.getName());
        User following = getUserById(following_id);

        if (!user.getFollowing().contains(following))
            throw new UserException("You are not following user " + following.getName());

        user.unFollowUser(following);
        userRepository.save(user);

        return new MessageResponseDto(
                String.format("You unfollowed %s", following.getName()));
    }

    @Override
    public List<User> searchUsers(String query) {
        return userRepository.findByQuery(PageRequest.of(0, 10), query).stream().toList();
    }

    @Override
    public User editAccount(Authentication authentication, UserDto userDto) throws UserException {
        User userFromDb = getUserByName(authentication.getName());

        if (userDto.getBio() != null)
            userFromDb.setBio(userDto.getBio());
        if (userDto.getName() != null)
            userFromDb.setName(userDto.getName());
        if (userDto.getWebsite() != null)
            userFromDb.setWebsite(userDto.getWebsite());
        if (userDto.getLocation() != null)
            userFromDb.setLocation(userDto.getLocation());

        if (userDto.getImage() != null && !userDto.getImage().isEmpty() && userDto.getImage().split(",").length > 1) {
            byte[] decoded = Base64.getDecoder().decode( userDto.getImage().split(",")[1]);
            userFromDb.setImage(decoded);
        }
        if (userDto.getBackgroundImage() != null && !userDto.getBackgroundImage().isEmpty() && userDto.getBackgroundImage().split(",").length > 1 ) {
            byte[] decoded = Base64.getDecoder().decode(userDto.getBackgroundImage().split(",")[1]);
            userFromDb.setBackgroundImage(decoded);
        }

        return userRepository.save(userFromDb);
    }

    @Override
    public RefreshTokenResponse refresh(String token) {
        String name = tokenService.parseToken(token);
        SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(name);

        String access_token = tokenService.generateAccessToken(user);
        String refresh_token = tokenService.generateRefreshToken(user);

        return new RefreshTokenResponse(access_token, refresh_token);
    }

    @Override
    public User getUserByToken(String token) throws UserException {
        return getUserByName(tokenService.parseToken(token));
    }

    @Override
    @Transactional
    public MessageResponseDto deleteUser(Long user_id) throws UserException {
        User user = getUserById(user_id);
        user.getRoles().clear();
        for (User u: user.getFollowers())
            u.unFollowUser(user);

        for (User u: user.getFollowing())
            user.unFollowUser(u);

        tweetRepository.deleteAllByUser(user);

        for (TweetData td: user.getTweetData())
            td.removeUser(user);

        userRepository.deleteById(user_id);
        return new MessageResponseDto("User deleted");
    }

    @Override
    public MessageResponseDto makeAdmin(Long user_id) throws UserException {
        User user = getUserById(user_id);

        user.getRoles().add(roleRepository.findByName("ADMIN"));

        userRepository.save(user);
        return new MessageResponseDto("User " + user.getName() + " is admin now");
    }
}
