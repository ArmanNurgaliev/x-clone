package ru.arman.backendxclone.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.exception.CommentException;
import ru.arman.backendxclone.model.TweetData;
import ru.arman.backendxclone.exception.TweetDtoException;
import ru.arman.backendxclone.exception.TweetException;
import ru.arman.backendxclone.exception.UserException;

import java.util.List;

@Service
public interface TweetDtoService {
    List<TweetData> getAuthUserTweets(Authentication authentication) throws UserException;
    TweetData getTweetDtoById(Long tweet_dto_id) throws TweetDtoException;
    MessageResponseDto repostTweet(Authentication authentication, Long tweet_id) throws UserException, TweetException, TweetDtoException;

    MessageResponseDto undoRepostTweet(Authentication authentication, Long tweet_id) throws TweetException, UserException, TweetDtoException;

    List<TweetData> getTweetDtosByUserLogin(String login) throws UserException;

    Boolean isTweetReposted(Authentication authentication, Long tweet_id) throws UserException;

    MessageResponseDto deleteTweet(Authentication authentication, Long tweet_dto_id) throws TweetDtoException;

    List<TweetData> getFollowingsTweets(Authentication authentication) throws UserException;

    List<TweetData> getLikedTweets(Long user_id) throws UserException;

    TweetData getRepliedTweets(Authentication authentication, Long comment_id) throws CommentException;

    List<TweetData> getTweetsMedia(Long user_id) throws UserException;
}
