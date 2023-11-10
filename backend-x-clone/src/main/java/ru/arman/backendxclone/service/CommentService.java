package ru.arman.backendxclone.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.dto.TweetDto;
import ru.arman.backendxclone.exception.CommentException;
import ru.arman.backendxclone.exception.TweetException;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.Comment;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

public interface CommentService {
    Comment getCommentById(Long comment_id) throws CommentException;

    Comment createComment(Authentication authentication, TweetDto commentDto, Long tweet_id) throws UserException, TweetException, IOException;

    MessageResponseDto likeComment(Authentication authentication, Long comment_id) throws UserException, CommentException;

    MessageResponseDto unLikeComment(Authentication authentication, Long comment_id) throws UserException, CommentException;

    MessageResponseDto deleteComment(Long comment_id);

    List<Comment> getTweetComments(Long tweet_id) throws TweetException;

    List<Comment> getComments(Authentication authentication) throws UserException;
}
