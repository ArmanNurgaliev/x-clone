package ru.arman.backendxclone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "users_seq", allocationSize = 1, initialValue = 2)
    private Long user_id;

    @NotBlank(message = "Name can't be empty")
    private String name;

    @NotBlank(message = "Login can't be empty")
    private String login;

    @NotBlank(message = "email can't be empty")
    @Email
    private String email;

    @NotBlank(message = "password can't be empty")
    @Size(min = 4)
    @JsonIgnore
    private String password;

    private String bio;
    private String website;
    private Date dob;

    @Lob
    @JdbcTypeCode(Types.LONGVARBINARY)
    private byte[] image;

    @Lob
    @JdbcTypeCode(Types.LONGVARBINARY)
    private byte[] backgroundImage;

    private Timestamp createdAt;
    private String location;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    @JsonIgnoreProperties({"following", "followers"})
    private Set<User> followers = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "following",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    @JsonIgnoreProperties({"following", "followers"})
    private Set<User> following = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private List<TweetData> tweetData = new ArrayList<>();

    @ManyToMany(mappedBy = "likedByUsers", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Tweet> likedTweets = new HashSet<>();

    @ManyToMany(mappedBy = "likedByUsers", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Comment> likedComments = new HashSet<>();

    public void followUser(User following) {
        this.following.add(following);
        following.getFollowers().add(this);
    }

    public void unFollowUser(User following) {
        this.following.remove(following);
        following.getFollowers().remove(this);
    }
}
