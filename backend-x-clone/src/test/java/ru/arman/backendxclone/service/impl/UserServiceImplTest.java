package ru.arman.backendxclone.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.dto.UserDto;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.Role;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.RoleRepository;
import ru.arman.backendxclone.repository.TweetRepository;
import ru.arman.backendxclone.repository.UserRepository;
import ru.arman.backendxclone.service.TokenService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private TweetRepository tweetRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void beforeAll() {
        user1 = new User();
        user1.setUser_id(1L);
        user1.setName("user1");
        user1.setLogin("user123");
        user1.setEmail("user1@mail.ru");

        user2 = new User();
        user2.setUser_id(2L);
        user2.setName("user2");
        user2.setLogin("user223");
        user2.setEmail("user2@mail.ru");

        auth = new UsernamePasswordAuthenticationToken(user1, "pass");
    }

    @Test
    void getAllUsers_shouldReturnTwoUsers() {
        PageRequest pageRequest = PageRequest.of(1, 5);
        List<User> users = List.of(user1, user2);
        Page<User> usersPage = new PageImpl<>(users, pageRequest, users.size());

        when(userRepository.findByQuery(any(PageRequest.class), any(String.class))).thenReturn(usersPage);

        Page<User> page = userService.getAllUsers(0, 5, "user");
        assertEquals(users.size(), page.getContent().size());
    }

    @Test
    void getUserById_shouldReturnUser() throws UserException {
        when(userRepository.findById(user1.getUser_id())).thenReturn(Optional.ofNullable(user1));

        User userFoundById = userService.getUserById(user1.getUser_id());

        assertEquals(user1, userFoundById);
    }

    @Test
    void getUserById_shouldThrowException() {
        UserException thrown = assertThrows(UserException.class, () -> userService.getUserById(3L));

        assertEquals("User not found with id: 3", thrown.getMessage());
    }

    @Test
    void getUserByName_shouldReturnUser() throws UserException {
        when(userRepository.findByName(user2.getName())).thenReturn(Optional.ofNullable(user2));

        User userFoundById = userService.getUserByName(user2.getName());

        assertEquals(user2, userFoundById);
    }

    @Test
    void getUserByName_shouldThrowException() {
        UserException thrown = assertThrows(UserException.class, () -> userService.getUserByName("username"));

        assertEquals("User not found with name: username", thrown.getMessage());
    }

    @Test
    void getUserByLogin_shouldReturnUser() throws UserException {
        when(userRepository.findByLogin(user2.getLogin())).thenReturn(Optional.ofNullable(user2));

        User userFoundById = userService.getUserByLogin(user2.getLogin());

        assertEquals(user2, userFoundById);
    }

    @Test
    void getUserByLogin_shouldThrowException() {
        UserException thrown = assertThrows(UserException.class, () -> userService.getUserByLogin("login"));

        assertEquals("User not found with login: login", thrown.getMessage());
    }

    @Test
    void followUserTest_shouldFollowUser() throws UserException {
        doReturn(Optional.ofNullable(user1)).when(userRepository).findByName(any());
        doReturn(Optional.ofNullable(user2)).when(userRepository).findById(any());

        when(userRepository.save(any())).thenReturn(user1);
        MessageResponseDto messageResponseDto = userService.followUser(auth, user2.getUser_id());

        assertTrue(user1.getFollowing().contains(user2));
        assertEquals("You are following user2", messageResponseDto.getMessage());
    }

    @Test
    void followUserTest_ShouldThrowException() {
        doReturn(Optional.ofNullable(user1)).when(userRepository).findByName(any());
        doReturn(Optional.ofNullable(user2)).when(userRepository).findById(any());

        user1.followUser(user2);
        assertThrows(UserException.class, () -> userService.followUser(auth, user2.getUser_id()));
    }

    @Test
    void unFollowUserTest_shouldUnFollowUser() throws UserException {
        doReturn(Optional.ofNullable(user1)).when(userRepository).findByName(any());
        doReturn(Optional.ofNullable(user2)).when(userRepository).findById(any());

        user1.followUser(user2);
        when(userRepository.save(any())).thenReturn(user1);
        MessageResponseDto messageResponseDto = userService.unFollowUser(auth, user2.getUser_id());

        assertTrue(user1.getFollowing().isEmpty());
        assertEquals("You unfollowed user2", messageResponseDto.getMessage());
    }

    @Test
    void unFollowUserTest_shouldThrowException() {
        doReturn(Optional.ofNullable(user1)).when(userRepository).findByName(any());
        doReturn(Optional.ofNullable(user2)).when(userRepository).findById(any());

        assertThrows(UserException.class, () -> userService.unFollowUser(auth, user2.getUser_id()));
    }

    @Test
    void searchUsersTest_shouldReturnUsers() {
        PageRequest pageRequest = PageRequest.of(1, 5);
        List<User> users = List.of(user1, user2);
        Page<User> usersPage = new PageImpl<>(users, pageRequest, users.size());

        when(userRepository.findByQuery(any(PageRequest.class), any(String.class))).thenReturn(usersPage);

        List<User> returnedUsers = userService.searchUsers( "user");
        assertEquals(users.size(), returnedUsers.size());
    }

    @Test
    void editAccountTest_shouldReturnEditedUser() throws UserException {
        UserDto userDto = new UserDto();
        userDto.setWebsite("website.ru");

        doReturn(Optional.ofNullable(user1)).when(userRepository).findByName(any());
        doReturn(user1).when(userRepository).save(any());

        User user = userService.editAccount(auth, userDto);
        assertEquals(userDto.getWebsite(), user.getWebsite());
    }

    @Test
    void getUserByTokenTest_shouldReturnUser() throws UserException {
        when(tokenService.parseToken(any())).thenReturn(user2.getName());
        when(userRepository.findByName(user2.getName())).thenReturn(Optional.ofNullable(user2));

        User userByToken = userService.getUserByToken("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoidXNlciIsImV4cCI6MTY5OTUwNTIzNywiaWF0IjoxNjk5NTAxNjM3LCJzY29wZSI6IlVTRVIgQURNSU4ifQ.dbtIeS0rFCZYcWx3eYnrqF4mgyck8K3qiaQzScGAz-u3G8Io2G_tuC1T-AXxVZWhCVElq5z0pilfu00UyMBSmw");

        assertEquals(user2, userByToken);
    }

    @Test
    void deleteUserTest_shouldReturnMessage() throws UserException {
        when(userRepository.findById(user1.getUser_id())).thenReturn(Optional.ofNullable(user1));

        MessageResponseDto messageResponseDto = userService.deleteUser(user1.getUser_id());

        assertEquals("User deleted", messageResponseDto.getMessage());
    }

    @Test
    void makeAdmitTest_shouldReturnUserWithAdminRole() throws UserException {
        Role role = new Role();
        role.setName("ADMIN");
        role.setId(1L);

        when(userRepository.findById(user1.getUser_id())).thenReturn(Optional.ofNullable(user1));
        when(roleRepository.findByName("ADMIN")).thenReturn(role);

        MessageResponseDto messageResponseDto = userService.makeAdmin(user1.getUser_id());

        assertTrue(user1.getRoles().contains(role));
        assertEquals("User user1 is admin now", messageResponseDto.getMessage());
    }
}