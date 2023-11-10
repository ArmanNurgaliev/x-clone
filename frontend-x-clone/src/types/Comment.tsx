type CommentType = {
  comment_id: number;
  content: string;
  imageData: string;
  createdAt: Date;
  user: User;
  tweet: Tweet;
  likedByUsers: User[];
};
