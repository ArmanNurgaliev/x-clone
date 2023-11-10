import React from "react";
import { useGetTweetsMediaQuery } from "../../store/tweets/tweets.api.endpoints";
import PostItem from "../home/PostItem";

type Props = {
  user_id: number;
};

const Media: React.FC<Props> = ({ user_id }) => {
  const { data: tweetDtos, isLoading } = useGetTweetsMediaQuery(user_id);

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

export default Media;
