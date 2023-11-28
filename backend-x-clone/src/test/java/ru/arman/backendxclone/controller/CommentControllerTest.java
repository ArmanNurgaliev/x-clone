package ru.arman.backendxclone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.arman.backendxclone.dto.TweetDto;
import ru.arman.backendxclone.model.Comment;
import ru.arman.backendxclone.model.SecurityUser;
import ru.arman.backendxclone.model.Tweet;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.CommentRepository;
import ru.arman.backendxclone.repository.TweetRepository;
import ru.arman.backendxclone.repository.UserRepository;
import ru.arman.backendxclone.service.TokenService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class CommentControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TweetRepository tweetRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TokenService tokenService;

    private User user;
    private Tweet tweet;
    private Comment comment;
    private Comment comment2;
    private String token;
    private ObjectMapper objectMapper;

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

        tweet = new Tweet();
        tweet.setUser(user);
        tweet.setContent("some tweet");
        tweetRepository.save(tweet);

        comment = new Comment();
        comment.setContent("some comment");
        comment.setUser(user);
        comment.setTweet(tweet);
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        comment2 = new Comment();
        comment2.setContent("some comment2");
        comment2.setUser(user);
        comment2.setTweet(tweet);
        comment2.setCreatedAt(new Timestamp(System.currentTimeMillis() - 50000));
        commentRepository.save(comment);
        commentRepository.save(comment2);

        token = "Bearer " + tokenService.generateAccessToken(new SecurityUser(user));
    }

    @Test
    void getCommentsTest_shouldReturnComments() throws Exception {
        mockMvc.perform(get("/api/comments")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getCommentsTest_shouldThrowUnauthorized() throws Exception {
        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTweetCommentsTest_shouldReturnComments() throws Exception {
        mockMvc.perform(get("/api/comments/tweet/" + tweet.getTweet_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getTweetCommentsTest_shouldThrowTweetException() throws Exception {
        mockMvc.perform(get("/api/comments/tweet/" + 3L)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No tweet with id: " + 3));
    }

    @Test
    void getCommentByIdTest_shouldReturnComment() throws Exception {
        mockMvc.perform(get("/api/comments/id/" + comment.getComment_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(comment.getContent()));
    }

    @Test
    void getCommentByIdTest_shouldThrowCommentException() throws Exception {
        mockMvc.perform(get("/api/comments/id/" + 5L)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No comment with id: " + 5));
    }

    @Test
    void createCommentTest_shouldReturnComment() throws Exception {
        TweetDto commentDto = new TweetDto("created comment", null);

        mockMvc.perform(post("/api/comments/create/" + tweet.getTweet_id())
                .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(commentDto.getContent()));

        List<Comment> comments = commentRepository.findByTweet(tweet, Sort.by(Sort.Direction.DESC, "createdAt"));
        assertEquals(3, comments.size());
    }

    @Test
    void createCommentTest_shouldThrowException() throws Exception {
        TweetDto commentDto = new TweetDto("created comment", null);

        mockMvc.perform(post("/api/comments/create/" + 15L)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No tweet with id: " + 15));
    }

    @Test
    void likeCommentTest_shouldReturnMessage() throws Exception {
        mockMvc.perform(put("/api/comments/like/" + comment.getComment_id())
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User " + user.getName() + " liked comment" ));

        assertTrue(comment.getLikedByUsers().contains(user));
    }

    @Test
    void unLikeCommentTest_shouldReturnMessage() throws Exception {
        comment.likeComment(user);
        commentRepository.save(comment);

        mockMvc.perform(put("/api/comments/unlike/" + comment.getComment_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User " + user.getName() + " unliked comment" ));

        assertFalse(comment.getLikedByUsers().contains(user));
    }

    @Test
    void deleteCommentTest_shouldReturnMessage() throws Exception {
        mockMvc.perform(delete("/api/comments/delete/" + comment.getComment_id())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comment has been deleted"));

        Optional<Comment> commentById = commentRepository.findById(comment.getComment_id());
        assertTrue(commentById.isEmpty());
    }
}