package ru.arman.backendxclone.service;

import org.springframework.security.core.Authentication;
import ru.arman.backendxclone.dto.TweetDto;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.model.TweetData;
import ru.arman.backendxclone.exception.TweetException;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.Tweet;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

public interface TweetService {
    List<Tweet> getAllTweets();

    List<Tweet> getUserTweets(Long user_id) throws UserException;

    Tweet getTweetById(Long tweet_id) throws TweetException;

    TweetData createTweet(Authentication authentication, TweetDto createTweetDto) throws UserException, IOException, DataFormatException;

    MessageResponseDto likeTweet(Authentication authentication, Long tweet_id) throws UserException, TweetException;

    MessageResponseDto unLikeTweet(Authentication authentication, Long tweet_id) throws TweetException, UserException;

    MessageResponseDto deleteTweet(Long tweet_id);

}
