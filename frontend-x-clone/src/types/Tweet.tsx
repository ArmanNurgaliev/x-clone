type Tweet = {
    tweet_id: number,
    content: string,
    imageData: string,
    repostedNumber: number,
    createdAt: Date,
    user: User,
    comments: CommentType[],
    likedByUsers: User[]
};