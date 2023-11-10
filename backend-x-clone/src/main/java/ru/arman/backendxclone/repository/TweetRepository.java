package ru.arman.backendxclone.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.arman.backendxclone.model.Tweet;
import ru.arman.backendxclone.model.User;

import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findAllByUser(User user, PageRequest createdAt);

    void deleteAllByUser(User user);
}
