import format from "date-fns/format";
import React from "react";
import { useNavigate } from "react-router-dom";
import PostAction from "./PostAction";
import { BiRepost } from "react-icons/bi";
import { useDeleteTweetMutation, useGetTweetByIdQuery } from "../../store/tweets/tweets.api.endpoints";
import { useGetAuthUserQuery } from "../../store/users/users.api.endpoints";
import DeletePost from "../delete/DeletePost";
import toast from "react-hot-toast";

type Props = {
  tweetDto: TweetDto;
};

const PostData: React.FC<Props> = ({ tweetDto }) => {
  const navigate = useNavigate();
  const { data: user } = useGetAuthUserQuery();
  const { data, isLoading } = useGetTweetByIdQuery(tweetDto.tweetId);
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
    <div className="w-full p-2">
      {tweetDto.isRetweet && (
        <div className="flex items-center mx-6 text-gray-500">
          <BiRepost />
          <p className="pl-2">{user?.name} reposted</p>
        </div>
      )}
      {isLoading ? (
        <div>Loading...</div>
      ) : data ? (
        <>
          <div className="flex items-center">
            <img
              onClick={() => navigate(`/${data.user.login}`)}
              className="w-10 h-10 rounded-full cursor-pointer"
              src={`${
                data.user.image
                  ? `data:image/jpeg;base64,${data.user.image}`
                  : "https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png"
              }`}
              alt=""
            />
            <div className="flex w-full items-center justify-between">
              <div className="items-center pl-2">
                <p
                  className="font-bold cursor-pointer"
                  onClick={() => navigate(`/${data.user.login}`)}
                >
                  {data.user.name}
                </p>
                <p
                  className="text-gray-400 cursor-pointer"
                  onClick={() => navigate(`/${data.user.login}`)}
                >
                  {data.user.login}
                </p>
              </div>
              <div className="px-2 py-2 rounded-full hover:bg-blue-100">
              {user?.user_id === data.user.user_id && (
                <DeletePost handleDelete={handleDelete} />
              )}
              </div>
            </div>
          </div>

          <div className="my-2">
            <p>{data.content}</p>
            {data.imageData && (
              <img
                className="block w-[400px] h-[400px]"
                src={`data:image/jpeg;base64,${data.imageData}`}
              />
            )}
          </div>

          <div className="mb-2">
            <p className="text-gray-400">
              {format(new Date(data.createdAt), "HH:MM MMMM do, yyyy")}
            </p>
          </div>

          <div>
            <hr />
            <PostAction tweetDto={tweetDto} />
            <hr />
          </div>
        </>
      ) : (
        <div>Tweet not found</div>
      )}
    </div>
  );
};

export default PostData;
