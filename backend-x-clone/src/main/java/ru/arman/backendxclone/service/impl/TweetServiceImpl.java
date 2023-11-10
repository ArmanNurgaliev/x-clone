package ru.arman.backendxclone.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.arman.backendxclone.dto.TweetDto;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.model.TweetData;
import ru.arman.backendxclone.exception.TweetException;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.Tweet;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.TweetDtoRepository;
import ru.arman.backendxclone.repository.TweetRepository;
import ru.arman.backendxclone.repository.UserRepository;
import ru.arman.backendxclone.service.TweetService;
import ru.arman.backendxclone.service.UserService;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {
    private final TweetRepository tweetRepository;
    private final UserService userService;
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final TweetDtoRepository tweetDtoRepository;

    @Override
    public List<Tweet> getAllTweets() {
        return tweetRepository.findAll();
    }

    @Override
    public List<Tweet> getUserTweets(Long user_id) throws UserException {
        User user = userService.getUserById(user_id);

        return tweetRepository.findAllByUser(user, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Override
    public Tweet getTweetById(Long tweet_id) throws TweetException {
        Tweet tweet = tweetRepository.findById(tweet_id)
                .orElseThrow(() -> new TweetException("No tweet with id: " + tweet_id));

        tweet.getComments().sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

        return tweet;
    }

    @Override
    public TweetData createTweet(Authentication authentication,
                                 TweetDto createTweetDto) throws UserException, IOException {
        User user = userService.getUserByName(authentication.getName());

        Tweet tweet = new Tweet();
        tweet.setContent(createTweetDto.getContent());
        tweet.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        tweet.setUser(user);

        if (createTweetDto.getImage() != null && !createTweetDto.getImage().isEmpty()) {
            byte[] decoded = Base64.getDecoder().decode(createTweetDto.getImage().split(",")[1]);
            tweet.setImageData(decoded);
        }

        Tweet savedTweet = tweetRepository.save(tweet);

        TweetData tweetDto = new TweetData();
        tweetDto.setTweetId(savedTweet.getTweet_id());
        tweetDto.setIsRetweet(false);
        tweetDto.setPostedAt(savedTweet.getCreatedAt());
        tweetDto.addUser(user);

        return tweetDtoRepository.save(tweetDto);
    }

    @Override
    public MessageResponseDto likeTweet(Authentication authentication, Long tweet_id) throws UserException, TweetException {
        User user = userService.getUserByName(authentication.getName());

        Tweet tweet = getTweetById(tweet_id);
        tweet.likeTweet(user);
        tweetRepository.save(tweet);

        return new MessageResponseDto("Tweet liked by " + user.getName());
    }

    @Override
    public MessageResponseDto unLikeTweet(Authentication authentication, Long tweet_id) throws TweetException, UserException {
        User user = userService.getUserByName(authentication.getName());

        Tweet tweet = getTweetById(tweet_id);
        tweet.unLikeTweet(user);
        tweetRepository.save(tweet);

        return new MessageResponseDto("Tweet unliked by " + user.getName());
    }

    @Override
    public MessageResponseDto deleteTweet(Long tweet_id) {
        tweetRepository.deleteById(tweet_id);

        return new MessageResponseDto("Tweet was deleted");
    }
}
