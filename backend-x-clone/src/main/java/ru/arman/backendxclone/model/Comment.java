package ru.arman.backendxclone.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Comparable<Comment> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_generator")
    @SequenceGenerator(name = "comment_generator", sequenceName = "comments_seq", allocationSize = 1)
    private Long comment_id;

    private String content;
    private Timestamp createdAt;

    @Lob
    @JdbcTypeCode(Types.LONGVARBINARY)
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tweet_id")
    @JsonIgnore
    private Tweet tweet;

    @ManyToMany
    @JoinTable(name = "comment_likes",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likedByUsers = new HashSet<>();

    public void likeComment(User user) {
        this.likedByUsers.add(user);
        user.getLikedComments().add(this);
    }

    public void unLikeComment(User user) {
        this.likedByUsers.remove(user);
        user.getLikedComments().remove(this);
    }

    @Override
    public int compareTo(Comment o) {
        return 0;
    }
}
