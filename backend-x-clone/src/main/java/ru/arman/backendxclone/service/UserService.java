package ru.arman.backendxclone.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.arman.backendxclone.dto.*;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.exception.ValidationException;
import ru.arman.backendxclone.model.User;

import java.io.IOException;
import java.util.List;

public interface UserService {
    Page<User> getAllUsers(Integer page, Integer rows, String name);

    User getUserById(Long user_id) throws UserException;

    User getUserByName(String name) throws UserException;

    User getUserByLogin(String login) throws UserException;

    MessageResponseDto followUser(Authentication authentication, Long following_id) throws UserException;

    MessageResponseDto unFollowUser(Authentication authentication, Long following_id) throws UserException;

    List<User> searchUsers(String query);

    User editAccount(Authentication authentication, UserDto userDto) throws UserException;

    User getUserByToken(String token) throws UserException;

    MessageResponseDto deleteUser(Long user_id) throws UserException;

    MessageResponseDto makeAdmin(Long user_id) throws UserException;
}
