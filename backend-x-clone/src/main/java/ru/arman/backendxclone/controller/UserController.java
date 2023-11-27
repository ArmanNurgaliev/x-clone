package ru.arman.backendxclone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.dto.UserDto;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.service.UserService;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam("page") Integer page,
            @RequestParam("rowPerPage") Integer rows,
            @RequestParam("name") String name
    ) {
        return ResponseEntity.ok(userService.getAllUsers(page, rows, name));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) throws UserException {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<User> getUserByName(@PathVariable String name) throws UserException {
        return ResponseEntity.ok(userService.getUserByName(name));
    }

    @GetMapping("/login/{login}")
    public ResponseEntity<User> getUserByLogin(@PathVariable String login) throws UserException {
        return ResponseEntity.ok(userService.getUserByLogin(login));
    }

    @PutMapping("/follow/{following_id}")
    public ResponseEntity<MessageResponseDto> followUser(@PathVariable Long following_id,
                                                         Authentication authentication) throws UserException {
        return ResponseEntity.ok(userService.followUser(authentication, following_id));
    }

    @PutMapping("/unfollow/{following_id}")
    public ResponseEntity<MessageResponseDto> unFollowUser(@PathVariable Long following_id,
                                                           Authentication authentication) throws UserException {
        return ResponseEntity.ok(userService.unFollowUser(authentication, following_id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam("q") String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @PostMapping("/account/edit")
    public ResponseEntity<User> editAccount(@RequestBody UserDto userDto,
                                            Authentication authentication) throws UserException {
        return ResponseEntity.ok(userService.editAccount(authentication, userDto));
    }

    @GetMapping("/me")
    public ResponseEntity<User> authorizedUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws UserException, ParseException {
        return ResponseEntity.ok(userService.getUserByToken(token));
    }

    @DeleteMapping("/delete/{user_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponseDto> deleteUser(@PathVariable Long user_id) throws UserException {
        return ResponseEntity.ok(userService.deleteUser(user_id));
    }

    @PutMapping("/make-admin/{user_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponseDto> makeAdmin(@PathVariable Long user_id) throws UserException {
        return ResponseEntity.ok(userService.makeAdmin(user_id));
    }
}