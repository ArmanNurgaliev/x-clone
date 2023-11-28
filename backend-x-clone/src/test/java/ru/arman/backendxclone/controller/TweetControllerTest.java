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
import ru.arman.backendxclone.dto.TweetDto;
import ru.arman.backendxclone.model.SecurityUser;
import ru.arman.backendxclone.model.Tweet;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.TweetRepository;
import ru.arman.backendxclone.repository.UserRepository;
import ru.arman.backendxclone.service.TokenService;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class TweetControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TweetRepository tweetRepository;
    @Autowired
    private TokenService tokenService;

    private User user;
    private Tweet tweet1;
    private Tweet tweet2;
    private ObjectMapper objectMapper;
    private String token;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void initData() {
        objectMapper =  new ObjectMapper();

        user = new User();
        user.setName("user1");
        user.setLogin("user123");
        user.setPassword("pass");
        user.setEmail("user1@mail.ru");
        userRepository.save(user);

        tweet1 = new Tweet();
        tweet1.setUser(user);
        tweet1.setContent("tweet number 1");
        tweet1.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        tweetRepository.save(tweet1);

        tweet2 = new Tweet();
        tweet2.setUser(user);
        tweet2.setContent("tweet number 2");
        tweet2.setCreatedAt(new Timestamp(System.currentTimeMillis()-50000));
        tweetRepository.save(tweet2);

        token = "Bearer " + tokenService.generateAccessToken(new SecurityUser(user));
    }

    @Test
    void getUserTweetsTest_shouldReturnTweets() throws Exception {
        mockMvc.perform(get("/api/tweets/user/" + user.getUser_id())
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].content").value(tweet1.getContent()));
    }

    @Test
    void getUserTweetsTest_shouldThrowUserException() throws Exception {
        mockMvc.perform(get("/api/tweets/user/" + 7L)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found with id: " + 7));
    }

    @Test
    void getTweetByIdTest_shouldReturnTweet() throws Exception {
        mockMvc.perform(get("/api/tweets/id/" + tweet1.getTweet_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(tweet1.getContent()));
    }

    @Test
    void getTweetByIdTest_shouldThrowTweetException() throws Exception {
        mockMvc.perform(get("/api/tweets/id/" + 7L)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No tweet with id: " + 7));
    }

    @Test
    void createTest_shouldReturnTweetData() throws Exception {
        TweetDto tweetDto = new TweetDto("new tweet", null);
        mockMvc.perform(post("/api/tweets/create")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tweetDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRetweet").value(false));
    }

    @Test
    void likeTweetTest_shouldReturnMessage() throws Exception {
        mockMvc.perform(put("/api/tweets/like/" + tweet1.getTweet_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tweet liked by " + user.getName()));

        assertTrue(tweet1.getLikedByUsers().contains(user));
    }

    @Test
    void unLikeTweetTest_shouldReturnMessage() throws Exception {
        tweet1.likeTweet(user);
        tweetRepository.save(tweet1);

        mockMvc.perform(put("/api/tweets/unlike/" + tweet1.getTweet_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tweet unliked by " + user.getName()));

        assertFalse(tweet1.getLikedByUsers().contains(user));
    }

    @Test
    void deleteTweetTest_shouldDeleteTweet() throws Exception {
        mockMvc.perform(delete("/api/tweets/delete/" + tweet2.getTweet_id())
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tweet was deleted"));

        List<Tweet> tweets = tweetRepository.findAll();
        assertEquals(1, tweets.size());
    }
}