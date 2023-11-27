package ru.arman.backendxclone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.arman.backendxclone.dto.LoginDto;
import ru.arman.backendxclone.dto.LoginResponseDto;
import ru.arman.backendxclone.dto.RegistrationDto;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.RoleRepository;
import ru.arman.backendxclone.repository.UserRepository;
import ru.arman.backendxclone.service.AuthService;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @BeforeEach
    void initData() {
        objectMapper =  new ObjectMapper();

        user = new User();
        user.setUser_id(1L);
        user.setName("user1");
        user.setLogin("user123");
        user.setEmail("user1@mail.ru");
        user.setPassword(passwordEncoder.encode("pass"));
        user.getRoles().add(roleRepository.findByName("USER"));

        userRepository.save(user);
    }

    @Test
    void loginTest_shouldReturnLoginRespDto() throws Exception {
        LoginDto loginDto = new LoginDto(user.getName(), "pass");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value(user.getName()));
    }

    @Test
    void loginTest_shouldThrowException() throws Exception {
        LoginDto loginDto = new LoginDto("abc", "pass");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name or password not valid"));
    }

    @Test
    void registerTest_shouldReturnMessage() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto("user2",
                        "user233",
                        "user2@mail.ru",
                        "pass",
                        new Date(System.currentTimeMillis()));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User with name " + registrationDto.getName() + " created"));

        assertNotNull(userRepository.findByName(registrationDto.getName()).get());
    }

    @Test
    void registerTest_shouldThrowValidationException() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(user.getName(),
                user.getLogin(),
                user.getEmail(),
                "pass",
                new Date(System.currentTimeMillis()));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$.name").value("Name is already taken"));
    }

    @Test
    void refreshTokenTest_shouldReturnRefreshTokenResponse() throws Exception {
        LoginResponseDto login = authService.login(new LoginDto(user.getName(), "pass"));

        mockMvc.perform(post("/api/auth/refresh-token")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + login.getRefreshToken()))
                .andExpect(status().isOk());
    }
}