create sequence comments_seq start with 1 increment by 1;
create sequence roles_seq start with 1 increment by 1;
create sequence tweet_data_seq start with 1 increment by 1;
create sequence tweets_seq start with 1 increment by 1;
create sequence users_seq start with 1 increment by 1;

create table comment_likes (
    comment_id bigint not null,
    user_id bigint not null,
    primary key (comment_id, user_id));

create table comments (
    comment_id bigint not null,
    created_at timestamp(6),
    tweet_id bigint,
    user_id bigint,
    image_data bytea,
    content varchar(255),
    primary key (comment_id));

create table followers (
    follower_id bigint not null,
    user_id bigint not null,
    primary key (follower_id, user_id));

create table following (
    following_id bigint not null,
    user_id bigint not null,
    primary key (following_id, user_id));

create table roles (
    role_id bigint not null,
    name varchar(255),
    primary key (role_id));

create table tweet_likes (
    tweet_id bigint not null,
    user_id bigint not null,
    primary key (tweet_id, user_id));

create table tweet_data (
    id bigint not null,
    tweet_id bigint,
    posted_at timestamp(6),
    is_retweet boolean,
    primary key (id));

create table tweets (
    tweet_id bigint not null,
    user_id bigint,
    reposted_number integer not null,
    created_at timestamp(6),
    image_data bytea,
    content varchar(255),
    primary key (tweet_id));

create table user_tweets (
    tweet_dto_id bigint not null,
    user_id bigint not null);

create table users (
    user_id bigint not null,
    login varchar(255),
    name varchar(255),
    password varchar(255),
    email varchar(255),
    background_image bytea,
    image bytea,
    bio varchar(255),
    location varchar(255),
    dob date,
    created_at timestamp(6),
    website varchar(255),
    primary key (user_id));

create table users_roles (
    role_id bigint not null,
    user_id bigint not null,
    primary key (role_id, user_id));

alter table if exists comment_likes
    add constraint comment_likes_user_id
    foreign key (user_id) references users;

alter table if exists comment_likes
    add constraint comment_likes_comment_id
    foreign key (comment_id) references comments;

alter table if exists comments
    add constraint comments_tweet_id
    foreign key (tweet_id) references tweets;

alter table if exists comments
    add constraint comments_user_id
    foreign key (user_id) references users;

alter table if exists followers
    add constraint followers_follower_id
    foreign key (follower_id) references users;

alter table if exists followers
    add constraint followers_user_id
    foreign key (user_id) references users;

alter table if exists following
    add constraint following_following_id
    foreign key (following_id) references users;

alter table if exists following
    add constraint following_user_id
    foreign key (user_id) references users;

alter table if exists tweet_likes
    add constraint tweet_likes_user_id
    foreign key (user_id) references users;

alter table if exists tweet_likes
    add constraint tweet_likes_tweet_id
    foreign key (tweet_id) references tweets;

alter table if exists tweets
    add constraint tweets_user_id
    foreign key (user_id) references users;

alter table if exists user_tweets
    add constraint user_tweets_user_id
    foreign key (user_id) references users;

alter table if exists user_tweets
    add constraint user_tweets_tweet_dto_id
    foreign key (tweet_dto_id) references tweet_data;

alter table if exists users_roles
    add constraint users_roles_role_id
    foreign key (role_id) references roles;

alter table if exists users_roles
    add constraint users_roles_user_id
    foreign key (user_id) references users;