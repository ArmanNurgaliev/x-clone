package ru.arman.backendxclone.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.dto.TweetDto;
import ru.arman.backendxclone.exception.CommentException;
import ru.arman.backendxclone.exception.TweetException;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.Comment;
import ru.arman.backendxclone.model.Tweet;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.CommentRepository;
import ru.arman.backendxclone.repository.TweetRepository;
import ru.arman.backendxclone.service.CommentService;
import ru.arman.backendxclone.service.TweetService;
import ru.arman.backendxclone.service.UserService;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final TweetService tweetService;

    @Override
    public Comment getCommentById(Long comment_id) throws CommentException {
        return commentRepository.findById(comment_id)
                .orElseThrow(() -> new CommentException("No comment with id: " + comment_id));
    }

    @Override
    public Comment createComment(Authentication authentication, TweetDto commentDto, Long tweet_id) throws UserException, TweetException, IOException {
        User user = userService.getUserByName(authentication.getName());
        Tweet tweet = tweetService.getTweetById(tweet_id);

        Comment comment = new Comment();
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        comment.setContent(commentDto.getContent());
        comment.setUser(user);
        comment.setTweet(tweet);

        if (commentDto.getImage() != null && !commentDto.getImage() .isEmpty()) {
            byte[] decoded = Base64.getDecoder().decode(commentDto.getImage().split(",")[1]);
            comment.setImageData(decoded);
        }

        return commentRepository.save(comment);
    }

    @Override
    public MessageResponseDto likeComment(Authentication authentication, Long comment_id) throws UserException, CommentException {
        User user = userService.getUserByName(authentication.getName());
        Comment comment = getCommentById(comment_id);

        comment.likeComment(user);
        commentRepository.save(comment);

        return new MessageResponseDto(String.format("User %s liked comment", user.getName()));
    }

    @Override
    public MessageResponseDto unLikeComment(Authentication authentication, Long comment_id) throws UserException, CommentException {
        User user = userService.getUserByName(authentication.getName());
        Comment comment = getCommentById(comment_id);

        comment.unLikeComment(user);
        commentRepository.save(comment);

        return new MessageResponseDto(String.format("User %s unliked comment", user.getName()));
    }

    @Override
    public MessageResponseDto deleteComment(Long comment_id) {
        commentRepository.deleteById(comment_id);

        return new MessageResponseDto("Comment has been deleted");
    }

    @Override
    public List<Comment> getTweetComments(Long tweet_id) throws TweetException {
        Tweet tweet = tweetService.getTweetById(tweet_id);

        return commentRepository.findByTweet(tweet, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public List<Comment> getComments(Authentication authentication) throws UserException {
        User user = userService.getUserByName(authentication.getName());

        List<Comment> comments = commentRepository.findByUser(user);
        comments.sort(Comparator.comparing(Comment::getCreatedAt).reversed());

        return comments;
    }
}
