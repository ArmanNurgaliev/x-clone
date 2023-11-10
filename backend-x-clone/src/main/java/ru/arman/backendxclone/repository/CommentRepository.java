package ru.arman.backendxclone.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.arman.backendxclone.model.Comment;
import ru.arman.backendxclone.model.Tweet;
import ru.arman.backendxclone.model.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTweet(Tweet tweet, Sort createdAt);

    List<Comment> findByUser(User user);
}
