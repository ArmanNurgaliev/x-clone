import React from "react";
import { useGetTweetsByLoginQuery } from "../../store/tweets/tweets.api.endpoints";
import PostItem from "../home/PostItem";

type Props = {
  login: string;
};

const Posts: React.FC<Props> = ({ login }) => {
  const { data: tweetDtos, isLoading } = useGetTweetsByLoginQuery(login);

  return (
    <>
      {isLoading ? (
        <div>Loading...</div>
      ) : tweetDtos ? (
        tweetDtos.map((item: TweetDto) => (
          <PostItem key={item.id} tweetDto={item} />
        ))
      ) : (
        <div>Not found</div>
      )}
    </>
  );
};

export default Posts;
