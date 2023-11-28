package ru.arman.backendxclone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.arman.backendxclone.dto.UserDto;
import ru.arman.backendxclone.model.Role;
import ru.arman.backendxclone.model.SecurityUser;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.UserRepository;
import ru.arman.backendxclone.service.TokenService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    private User user1;
    private User user2;
    private String token;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void dataInit() {
        user1 = new User();
        user1.setName("user1");
        user1.setLogin("user123");
        user1.setEmail("user1@mail.ru");
        user1.setPassword("pass");
        user1.getRoles().add(new Role(2L, "USER"));

        user2 = new User();
        user2.setName("user2");
        user2.setLogin("user223");
        user2.setPassword("pass");
        user2.setEmail("user2@mail.ru");
        user2.getRoles().add(new Role(2L, "USER"));

        userRepository.saveAll(List.of(user1, user2));

        token = "Bearer " + tokenService.generateAccessToken(new SecurityUser(user1));
    }

    @Test
    void getAllUsersTest_shouldReturnUsers() throws Exception {
        mockMvc.perform(get("/api/users/all?page=0&rowPerPage=10&name=user")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2));
    }

    @Test
    void getUserByIdTest_shouldReturnUser() throws Exception {
        User user = userRepository.findByName("user1").get();

        mockMvc.perform(get("/api/users/id/" + user.getUser_id())
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user1"));
    }

    @Test
    void getUserByIdTest_shouldThrowException() throws Exception {
        mockMvc.perform(get("/api/users/id/10")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found with id: 10"));
    }

    @Test
    void getUserByNameTest_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/name/user1")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user1"));
    }

    @Test
    void getUserByNameTest_shouldThrowException() throws Exception {
        mockMvc.perform(get("/api/users/name/xyz")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found with name: xyz"));
    }

    @Test
    void getUserByLoginTest_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/login/user223")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user2"));
    }

    @Test
    void getUserByLoginTest_shouldThrowException() throws Exception {
        mockMvc.perform(get("/api/users/login/xyz")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found with login: xyz"));
    }

    @Test
    void followUserTest_shouldFollowUser() throws Exception {
        User userToFollow = userRepository.findByName(user2.getName()).get();

        mockMvc
                .perform(put("/api/users/follow/" + userToFollow.getUser_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You are following user2"));

        User user = userRepository.findByName(user1.getName()).get();
        assertEquals(1, user.getFollowing().size());
    }

    @Test
    void followUserTest_shouldThrowExceptionAlreadyFollow() throws Exception {
        User userToFollow = followUser();

        mockMvc
                .perform(put("/api/users/follow/" + userToFollow.getUser_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You are already followed user user2"));
    }

    private User followUser() {
        User follower = userRepository.findByName("user1").get();
        User userToFollow = userRepository.findByName("user2").get();

        follower.followUser(userToFollow);
        userRepository.save(follower);

        return userToFollow;
    }

    @Test
    void followUserTest_shouldThrowExceptionUnauthorized() throws Exception {
        mockMvc
                .perform(put("/api/users/follow/" + user2.getUser_id()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unFollowUserTest_shouldUnFollowUser() throws Exception {
        User userToUnFollow = followUser();
        mockMvc
                .perform(put("/api/users/unfollow/" + userToUnFollow.getUser_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You unfollowed user2"));

        User user = userRepository.findByName(user1.getName()).get();
        assertTrue(user.getFollowing().isEmpty());
    }

    @Test
    void unFollowUserTest_shouldThrowExceptionDoesNotFollow() throws Exception {
        mockMvc
                .perform(put("/api/users/unfollow/" + user2.getUser_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You are not following user user2"));
    }

    @Test
    void unFollowUserTest_shouldThrowExceptionUnauthorized() throws Exception {
        mockMvc
                .perform(put("/api/users/unfollow/" + user2.getUser_id()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void searchUserTest_shouldReturnUser() throws Exception {
        mockMvc
                .perform(get("/api/users/search?q=" + user1.getName())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(user1.getName()));
    }

    @Test
    void searchUserTest_shouldReturnAllUsers() throws Exception {
        mockMvc
                .perform(get("/api/users/search?q=")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3));
    }

    @Test
    void editUserTest_shouldEditUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setWebsite("website.ru");
        userDto.setName("updatedUser");
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(post("/api/users/account/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.website").value(userDto.getWebsite()));
    }

    @Test
    void authorizedUserTest_shouldReturnUser() throws Exception {
        mockMvc
                .perform(get("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user1.getName()));
    }

    @Test
    void deleteUserTest_shouldDeleteUser() throws Exception {
        String newToken = getToken();

        User userToDelete = userRepository.findByName("user2").get();

        mockMvc
                .perform(delete("/api/users/delete/" + userToDelete.getUser_id())
                        .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted"));

        Optional<User> deletedUser = userRepository.findByName("user2");
        assertTrue(deletedUser.isEmpty());
    }

    private String getToken() {
        User user = userRepository.findByName("user1").get();
        user.getRoles().add(new Role(1L, "ADMIN"));
        String newToken = "Bearer " + tokenService.generateAccessToken(new SecurityUser(user));
        return newToken;
    }

    @Test
    void deleteUserTest_forbidden() throws Exception {
        mockMvc
                .perform(delete("/api/users/delete/" + user2.getUser_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isForbidden());
    }

    @Test
    void makeAdminTest_shouldMakeUserAdmin() throws Exception {
        String newToken = getToken();

        User userToMakeAdmin = userRepository.findByName("user2").get();

        mockMvc
                .perform(put("/api/users/make-admin/" + userToMakeAdmin.getUser_id())
                        .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User " + userToMakeAdmin.getName() + " is admin now"));

        User admin = userRepository.findByName("user2").get();
        assertEquals(2, admin.getRoles().size());
    }

    @Test
    void makeAdminTest_forbidden() throws Exception {
        User userToMakeAdmin = userRepository.findByName("user2").get();

        mockMvc
                .perform(put("/api/users/make-admin/" + userToMakeAdmin.getUser_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isForbidden());
    }
}