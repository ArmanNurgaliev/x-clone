package ru.arman.backendxclone.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.dto.UserDto;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.TweetData;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.RoleRepository;
import ru.arman.backendxclone.repository.TweetRepository;
import ru.arman.backendxclone.repository.UserRepository;
import ru.arman.backendxclone.service.TokenService;
import ru.arman.backendxclone.service.UserService;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final TweetRepository tweetRepository;

    @Override
    public Page<User> getAllUsers(Integer page, Integer rows, String name) {
        return userRepository.findByQuery(PageRequest.of(page, rows, Sort.by("name")), name);
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
