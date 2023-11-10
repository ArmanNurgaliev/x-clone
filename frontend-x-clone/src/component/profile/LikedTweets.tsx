import React from "react";
import { useGetLikedTweetsQuery } from "../../store/tweets/tweets.api.endpoints";
import PostItem from "../home/PostItem";

type Props = {
    user_id: number;
  };

const LikedTweets: React.FC<Props> = ({ user_id }) => {
    const { data: likedTweetDtos, isLoading: isLikedTweetsLoading } =
    useGetLikedTweetsQuery(user_id);

  return (
    <>
      {isLikedTweetsLoading ? (
        <div>Loading...</div>
      ) : likedTweetDtos ? (
        likedTweetDtos.map((item: TweetDto) => (
          <PostItem key={item.id} tweetDto={item} />
        ))
      ) : (
        <div>Not found</div>
      )}
    </>
  );
};

export default LikedTweets;
