package ru.arman.backendxclone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.dto.TweetDto;
import ru.arman.backendxclone.exception.TweetException;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.Tweet;
import ru.arman.backendxclone.model.TweetData;
import ru.arman.backendxclone.service.TweetService;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/api/tweets")
@RequiredArgsConstructor
public class TweetController {
    private final TweetService tweetService;

    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<Tweet>> getUserTweets(@PathVariable Long user_id) throws UserException {
        return ResponseEntity.ok(tweetService.getUserTweets(user_id));
    }

    @GetMapping("/id/{tweet_id}")
    public ResponseEntity<Tweet> getTweetById(@PathVariable Long tweet_id) throws TweetException {
        return ResponseEntity.ok(tweetService.getTweetById(tweet_id));
    }

    @PostMapping(value = "/create")
    public ResponseEntity<TweetData> createTweet(Authentication authentication,
                                                 @RequestBody TweetDto createTweetDto
                                                ) throws UserException, DataFormatException {
        return ResponseEntity.ok(tweetService.createTweet(authentication, createTweetDto));
    }

    @PutMapping("/like/{tweet_id}")
    public ResponseEntity<MessageResponseDto> likeTweet(Authentication authentication,
                                                        @PathVariable Long tweet_id) throws UserException, TweetException {
        return ResponseEntity.ok(tweetService.likeTweet(authentication, tweet_id));
    }

    @PutMapping("/unlike/{tweet_id}")
    public ResponseEntity<MessageResponseDto> unLikeTweet(Authentication authentication,
                                                        @PathVariable Long tweet_id) throws UserException, TweetException {
        return ResponseEntity.ok(tweetService.unLikeTweet(authentication, tweet_id));
    }

    @DeleteMapping("/delete/{tweet_id}")
    public ResponseEntity<MessageResponseDto> deleteTweet(@PathVariable Long tweet_id) {
        return ResponseEntity.ok(tweetService.deleteTweet(tweet_id));
    }
}
