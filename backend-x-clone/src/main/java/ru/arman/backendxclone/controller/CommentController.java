package ru.arman.backendxclone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.dto.TweetDto;
import ru.arman.backendxclone.exception.CommentException;
import ru.arman.backendxclone.exception.TweetException;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.Comment;
import ru.arman.backendxclone.service.CommentService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(Authentication authentication) throws UserException {
        return ResponseEntity.ok(commentService.getComments(authentication));
    }

    @GetMapping("/tweet/{tweet_id}")
    public ResponseEntity<List<Comment>> getTweetComments(@PathVariable Long tweet_id) throws TweetException {
        return ResponseEntity.ok(commentService.getTweetComments(tweet_id));
    }

    @GetMapping("/id/{comment_id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long comment_id) throws CommentException {
        return ResponseEntity.ok(commentService.getCommentById(comment_id));
    }

    @PostMapping("/create/{tweet_id}")
    public ResponseEntity<Comment> createComment(Authentication authentication,
                                                 @RequestBody TweetDto commentDto,
                                                 @PathVariable Long tweet_id) throws TweetException, UserException, IOException {
        return ResponseEntity.ok(commentService.createComment(authentication, commentDto, tweet_id));
    }

    @PutMapping("/like/{comment_id}")
    public ResponseEntity<MessageResponseDto> likeComment(Authentication authentication,
                                                          @PathVariable Long comment_id) throws UserException, CommentException {
        return ResponseEntity.ok(commentService.likeComment(authentication, comment_id));
    }

    @PutMapping("/unlike/{comment_id}")
    public ResponseEntity<MessageResponseDto> unLikeComment(Authentication authentication,
                                                          @PathVariable Long comment_id) throws UserException, CommentException {
        return ResponseEntity.ok(commentService.unLikeComment(authentication, comment_id));
    }

    @DeleteMapping("/delete/{comment_id}")
    public ResponseEntity<MessageResponseDto> deleteComment(@PathVariable Long comment_id) {
        return ResponseEntity.ok(commentService.deleteComment(comment_id));
    }
}
