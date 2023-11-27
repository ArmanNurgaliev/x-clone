package ru.arman.backendxclone.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.dto.TweetDto;
import ru.arman.backendxclone.exception.CommentException;
import ru.arman.backendxclone.exception.TweetException;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.Comment;
import ru.arman.backendxclone.model.Tweet;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.CommentRepository;
import ru.arman.backendxclone.service.TweetService;
import ru.arman.backendxclone.service.UserService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private TweetService tweetService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user1;
    private Authentication auth;
    private Comment comment;
    private Comment comment2;
    private Tweet tweet;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUser_id(1L);
        user1.setName("user1");
        user1.setLogin("user123");
        user1.setEmail("user1@mail.ru");

        tweet = new Tweet();
        tweet.setTweet_id(1L);
        tweet.setUser(user1);
        tweet.setContent("some tweet");

        comment = new Comment();
        comment.setComment_id(1L);
        comment.setContent("some comment");
        comment.setUser(user1);
        comment.setTweet(tweet);
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        comment2 = new Comment();
        comment2.setComment_id(2L);
        comment2.setContent("some comment2");
        comment2.setUser(user1);
        comment2.setTweet(tweet);
        comment2.setCreatedAt(new Timestamp(System.currentTimeMillis() - 50000));

        auth = new UsernamePasswordAuthenticationToken(user1, "pass");
    }

    @Test
    void getCommentByIdTest_shouldThrowException() throws CommentException {
        when(commentRepository.findById(comment.getComment_id())).thenReturn(Optional.ofNullable(comment));

        Comment commentById = commentService.getCommentById(comment.getComment_id());

        assertEquals(comment, commentById);
    }

    @Test
    void getCommentByIdTest_shouldReturnComment() {
        CommentException commentException = assertThrows(CommentException.class, () -> commentService.getCommentById(comment.getComment_id()));

        assertEquals("No comment with id: " + comment.getComment_id(), commentException.getMessage());
    }

    @Test
    void createCommentTest_shouldReturnCreatedComment() throws UserException, TweetException {
        when(userService.getUserByName(any())).thenReturn(user1);
        when(tweetService.getTweetById(any())).thenReturn(tweet);
        when(commentRepository.save(any())).thenReturn(comment);

        TweetDto commentDto = new TweetDto(comment.getContent(), null);
        Comment createdComment = commentService.createComment(auth, commentDto, tweet.getTweet_id());

        assertEquals(comment.getContent(), createdComment.getContent());
    }

    @Test
    void likeComment_shouldLikeComment() throws UserException, CommentException {
        when(commentRepository.findById(comment.getComment_id())).thenReturn(Optional.ofNullable(comment));
        when(userService.getUserByName(any())).thenReturn(user1);

        MessageResponseDto messageResponseDto = commentService.likeComment(auth, comment.getComment_id());

        assertEquals(String.format("User %s liked comment", user1.getName()), messageResponseDto.getMessage());
    }

    @Test
    void unLikeComment_shouldUnLikeComment() throws UserException, CommentException {
        when(commentRepository.findById(comment.getComment_id())).thenReturn(Optional.ofNullable(comment));
        when(userService.getUserByName(any())).thenReturn(user1);

        comment.likeComment(user1);
        MessageResponseDto messageResponseDto = commentService.unLikeComment(auth, comment.getComment_id());

        assertEquals(String.format("User %s unliked comment", user1.getName()), messageResponseDto.getMessage());
    }

    @Test
    void deleteCommentTest_shouldReturnMessage() {
        MessageResponseDto messageResponseDto = commentService.deleteComment(comment.getComment_id());

        assertEquals("Comment has been deleted", messageResponseDto.getMessage());
    }

    @Test
    void getTweetCommentsTest_shouldReturnComments() throws TweetException {
        when(tweetService.getTweetById(any())).thenReturn(tweet);
        when(commentRepository.findByTweet(any(), any())).thenReturn(List.of(comment, comment2));

        List<Comment> tweetComments = commentService.getTweetComments(tweet.getTweet_id());

        assertEquals(2, tweetComments.size());
    }

    @Test
    void getComments_shouldReturnUserComments() throws UserException {
        List<Comment> list = new ArrayList<>();
        list.add(comment);
        list.add(comment2);
        when(userService.getUserByName(any())).thenReturn(user1);
        when(commentRepository.findByUser(any())).thenReturn(list);

        List<Comment> comments = commentService.getComments(auth);

        assertEquals(2, comments.size());
    }
}