import React from "react";
import {
  useGetTweetByIdQuery,
  useGetTweetDtoByCommentIdQuery,
} from "../../store/tweets/tweets.api.endpoints";
import { BsDot } from "react-icons/bs";
import Logic from "../../services/config/Logic";
import PostAction from "../post/PostAction";

type Props = {
  comment: CommentType;
};

const Replies: React.FC<Props> = ({ comment }) => {
  const { data: tweetDto, isLoading: isTweetDtoLoading } =
    useGetTweetDtoByCommentIdQuery(comment.comment_id!);
  const { data: tweet, isLoading: isTweetLoading } = useGetTweetByIdQuery(
    tweetDto?.tweetId!
  );

  return (
    <div>
      {isTweetLoading ? (
        <div>Loading...</div>
      ) : tweet ? (
        <div className="flex m-2">
          <div>
            <img
              className="w-10 h-10 rounded-full cursor-pointer"
              src={`${
                tweet.user.image
                  ? `data:image/jpeg;base64,${tweet.user.image}`
                  : "https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png"
              }`}
              alt=""
            />
          </div>
          <div className="w-[90%] mx-2">
            <div className="flex justify-between items-center">
              <div className="flex items-center">
                <p className="font-bold cursor-pointer">{tweet.user.name}</p>
                <p className="text-gray-400 px-1 cursor-pointer">
                  {tweet.user.login}
                </p>
                <BsDot className="text-gray-400" />
                <p className="text-gray-400 px-1">
                  {Logic.timeDifference(tweet.createdAt)}
                </p>
              </div>
            </div>

            <div>
              <p>{tweet.content}</p>
              <PostAction tweetDto={tweetDto!} />
            </div>
          </div>
        </div>
      ) : (
        <div>Not found</div>
      )}
      <div className="flex m-2 ml-8">
        <div>
          <img
            className="w-10 h-10 rounded-full cursor-pointer"
            src={`${
              comment.user.image
                ? `data:image/jpeg;base64,${comment.user.image}`
                : "https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png"
            }`}
            alt=""
          />
        </div>
        <div className="w-[90%] mx-2">
          <div className="flex justify-between items-center">
            <div className="flex items-center">
              <p className="font-bold cursor-pointer">{comment.user.name}</p>
              <p className="text-gray-400 px-1 cursor-pointer">
                {comment.user.login}
              </p>
              <BsDot className="text-gray-400" />
              <p className="text-gray-400 px-1">
                {Logic.timeDifference(comment.createdAt)}
              </p>
            </div>
          </div>

          <div>
            <p>{comment.content}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Replies;
