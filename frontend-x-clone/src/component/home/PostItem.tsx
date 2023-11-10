import { BsThreeDots, BsDot } from "react-icons/bs";
import { BiRepost } from "react-icons/bi";
import { useNavigate } from "react-router-dom";
import Logic from "../../services/config/Logic";
import PostAction from "../post/PostAction";
import {
  useDeleteTweetMutation,
  useGetTweetByIdQuery,
} from "../../store/tweets/tweets.api.endpoints";
import { useGetAuthUserQuery } from "../../store/users/users.api.endpoints";
import { Button, Popover, Typography } from "@mui/material";
import { useState } from "react";
import toast, { Toaster } from "react-hot-toast";
import DeletePost from "../delete/DeletePost";

type Props = {
  tweetDto: TweetDto;
};

const PostItem: React.FC<Props> = ({ tweetDto }) => {
  const navigate = useNavigate();
  const { data: user } = useGetAuthUserQuery();
  const {
    data: currentTweet,
    error,
    isLoading,
  } = useGetTweetByIdQuery(tweetDto.tweetId);
  const [deleteTweet] = useDeleteTweetMutation();


  const handleDelete = () => {
    deleteTweet(tweetDto.id)
      .unwrap()
      .then(() => {
        toast("Tweet deleted", {
          duration: 4000,
          position: "bottom-center",
          style: {
            color: "red",
          },
        });
      });
  };

  return (
    <div className="pl-2">
      <Toaster />
      {tweetDto.isRetweet && user && (
        <div className="flex items-center mx-6 text-gray-500">
          <BiRepost />
          <p className="pl-2">{user.name} reposted</p>
        </div>
      )}
      {isLoading ? (
        <div>Loading...</div>
      ) : currentTweet ? (
        <div className="flex m-2">
          <div>
            <img
              onClick={() => navigate(`/${currentTweet.user.login}`)}
              className="w-10 h-10 rounded-full cursor-pointer"
              src={`${
                currentTweet?.user.image
                  ? `data:image/jpeg;base64,${currentTweet.user.image}`
                  : "https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png"
              }`}
              alt=""
            />
          </div>
          <div className="w-[90%] mx-2">
            <div className="flex justify-between items-center">
              <div className="flex items-center">
                <p
                  className="font-bold cursor-pointer"
                  onClick={() => navigate(`/${currentTweet.user.login}`)}
                >
                  {currentTweet?.user.name}
                </p>
                <p
                  className="text-gray-400 px-1 cursor-pointer"
                  onClick={() => navigate(`/${currentTweet.user.login}`)}
                >
                  {currentTweet?.user.login}
                </p>
                <BsDot className="text-gray-400" />
                <p className="text-gray-400 px-1">
                  {Logic.timeDifference(currentTweet.createdAt)}
                </p>
              </div>

              {(user?.user_id === currentTweet.user.user_id || Logic.isAdmin(user!)) && (
                <DeletePost handleDelete={handleDelete} />
              )}
            </div>

            <div
              onClick={() =>
                navigate(`/${currentTweet.user.login}/post/${tweetDto.id}`)
              }
            >
              <p>{currentTweet?.content}</p>
              {currentTweet?.imageData && (
                <img
                  className="block w-[400px] h-[400px]"
                  src={`data:image/jpeg;base64,${currentTweet.imageData}`}
                />
              )}
            </div>

            <PostAction tweetDto={tweetDto} />
          </div>
        </div>
      ) : (
        <div>
          {error && "message" in error ? (
            <p className="text-red-500">error.message</p>
          ) : (
            "Error"
          )}
        </div>
      )}
      <hr />
    </div>
  );
};

export default PostItem;
