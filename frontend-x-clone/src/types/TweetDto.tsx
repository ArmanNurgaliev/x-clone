type TweetDto = {
    id: number
    tweetId: number,
    isRetweet: boolean,
    postedAt: Date,
    users: User[]
}