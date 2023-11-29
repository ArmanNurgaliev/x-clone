package ru.arman.backendxclone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.arman.backendxclone.exception.TweetDtoException;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.*;
import ru.arman.backendxclone.repository.CommentRepository;
import ru.arman.backendxclone.repository.TweetDtoRepository;
import ru.arman.backendxclone.repository.TweetRepository;
import ru.arman.backendxclone.repository.UserRepository;
import ru.arman.backendxclone.service.TokenService;

import java.sql.Timestamp;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class TweetDtoControllerTest {

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
    private User following;
    private Tweet tweet1;
    private Tweet tweet2;
    private Comment comment;

    private TweetData tweetData1;
    private TweetData tweetData2;
    private TweetData tweetData3;
    private ObjectMapper objectMapper;
    private String token;
    @Autowired
    private TweetDtoRepository tweetDtoRepository;
    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void initData() {
        objectMapper =  new ObjectMapper();

        following = new User();
        following.setName("following1");
        following.setLogin("following123");
        following.setPassword("pass");
        following.setEmail("following@mail.ru");
        userRepository.save(following);

        user = new User();
        user.setName("user1");
        user.setLogin("user123");
        user.setPassword("pass");
        user.setEmail("user1@mail.ru");
        user.followUser(following);
        userRepository.save(user);

        tweet1 = new Tweet();
        tweet1.setUser(user);
        tweet1.setContent("tweet number 1");
        tweet1.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        tweet1.likeTweet(user);
        tweetRepository.save(tweet1);

        tweet2 = new Tweet();
        tweet2.setUser(user);
        tweet2.setContent("tweet number 1");
        tweet2.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        tweetRepository.save(tweet2);

        comment = new Comment();
        comment.setTweet(tweet1);
        comment.setUser(user);
        comment.setContent("Nice tweet");
        commentRepository.save(comment);

        tweetData1 = new TweetData();
        tweetData1.setTweetId(tweet1.getTweet_id());
        tweetData1.setPostedAt(tweet1.getCreatedAt());
        tweetData1.setIsRetweet(false);
        tweetData1.addUser(user);
        tweetDtoRepository.save(tweetData1);

        tweetData2 = new TweetData();
        tweetData2.setTweetId(tweet1.getTweet_id());
        tweetData2.setPostedAt(tweet1.getCreatedAt());
        tweetData2.setIsRetweet(true);
        tweetData2.addUser(user);
        tweetData2.addUser(following);
        tweetDtoRepository.save(tweetData2);

        tweetData3 = new TweetData();
        tweetData3.setTweetId(tweet2.getTweet_id());
        tweetData3.setPostedAt(tweet2.getCreatedAt());
        tweetData3.setIsRetweet(false);
        tweetData3.addUser(user);
        tweetDtoRepository.save(tweetData3);

        token = "Bearer " + tokenService.generateAccessToken(new SecurityUser(user));
    }

    @Test
    void getAuthUserTweetsTest_shouldReturnTweetData() throws Exception {
        mockMvc.perform(get("/api/tweetDto")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3));
    }

    @Test
    void getTweetDtoByIdTest_shouldReturnTweetData() throws Exception {
        mockMvc.perform(get("/api/tweetDto/id/" + tweetData1.getId())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tweetId").value(tweetData1.getTweetId()))
                .andExpect(jsonPath("$.isRetweet").value(false));
    }

    @Test
    void getTweetDtoByIdTest_shouldThrowTweetDtoException() throws Exception {
        mockMvc.perform(get("/api/tweetDto/id/" + 5L)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("TweetDto not found with id: " + 5));
    }

    @Test
    void getTweetDtosByUserLoginTest_shouldReturnTweetData() throws Exception {
        mockMvc.perform(get("/api/tweetDto/user/" + user.getLogin())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3));
    }

    @Test
    void getTweetDtosByUserLoginTest_shouldThrowUserException() throws Exception {
        mockMvc.perform(get("/api/tweetDto/user/" + "login")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserException))
                .andExpect(result -> assertEquals("User not found with login: login",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getFollowingTweetsTest_shouldReturnTweetData() throws Exception {
        mockMvc.perform(get("/api/tweetDto/following")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(tweetData2.getId()))
                .andExpect(jsonPath("$[0].tweetId").value(tweetData2.getTweetId()));
    }

    @Test
    void getLikedTweetsTest_shouldReturnTweetData() throws Exception {
        mockMvc.perform(get("/api/tweetDto/liked/" + user.getUser_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getRepliedTweetsTest_shouldReturnTweetData() throws Exception {
        mockMvc.perform(get("/api/tweetDto/replies/" + comment.getComment_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tweetData1.getId()))
                .andExpect(jsonPath("$.tweetId").value(tweet1.getTweet_id()));
    }

    @Test
    void getTweetsMediaTest_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/tweetDto/media/" + user.getUser_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void repostTweetTest_shouldReturnMessage() throws Exception {
        mockMvc.perform(put("/api/tweetDto/repost/" + tweet2.getTweet_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tweet reposted by " + user.getName()));
    }

    @Test
    void repostTweetTest_shouldThrowTweetDtoException() throws Exception {
        tweetData3.setIsRetweet(true);
        tweetDtoRepository.save(tweetData3);
        mockMvc.perform(put("/api/tweetDto/repost/" + tweet2.getTweet_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof TweetDtoException))
                .andExpect(result -> assertEquals("Tweet dto not found with tweet id: " + tweet2.getTweet_id(),
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void undoRepostTweetTest_shouldReturnMessage() throws Exception {
        mockMvc.perform(put("/api/tweetDto/undoRepost/" + tweet1.getTweet_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tweet unreposted by " + user.getName()));
    }

    @Test
    void isTweetRepostedTest_shouldReturnTrue() throws Exception {
        mockMvc.perform(get("/api/tweetDto/reposted/" + tweet1.getTweet_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void isTweetRepostedTest_shouldReturnFalse() throws Exception {
        mockMvc.perform(get("/api/tweetDto/reposted/" + tweet2.getTweet_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void deleteTest_shouldReturnMessageAndDontDeleteTweet() throws Exception {
        mockMvc.perform(delete("/api/tweetDto/delete/" + tweetData2.getId())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tweet with id " + tweetData2.getTweetId() + " deleted"));

        assertEquals(2, tweetDtoRepository.findAll().size());
        assertEquals(2, tweetRepository.findAll().size());
    }
}