import { useEffect, useState } from "react";

import { FaRegComment } from "react-icons/fa6";
import { BiRepost } from "react-icons/bi";
import { AiOutlineHeart, AiFillHeart } from "react-icons/ai";
import CommentPost from "../comment/CommentPost";
import {
  useGetTweetByIdQuery,
  useIsTweetRepostedQuery,
  useLikeTweetMutation,
  useRetweetMutation,
  useUnLikeTweetMutation,
  useUndoRetweetMutation,
} from "../../store/tweets/tweets.api.endpoints";
import { Box, Modal } from "@mui/material";
import { useGetAuthUserQuery } from "../../store/users/users.api.endpoints";

type Props = {
  tweetDto: TweetDto;
};

const style = {
  position: "absolute" as "absolute",
  top: "40%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 600,
  bgcolor: "background.paper",
  borderRadius: "10px",
  boxShadow: 24,
  p: 4,
};

const PostAction: React.FC<Props> = ({ tweetDto }) => {
  const [isLiked, setIsLiked] = useState<boolean>();
  const { data: user } = useGetAuthUserQuery();
  const [open, setOpen] = useState<boolean>(false);
  const [likeTweet] = useLikeTweetMutation();
  const [unLikeTweet] = useUnLikeTweetMutation();
  const [retweet] = useRetweetMutation();
  const [undoRetweet] = useUndoRetweetMutation();

  const { data: isReposted } = useIsTweetRepostedQuery(tweetDto.tweetId);
  const { data: currentTweet, isLoading: isTweetLoaded } = useGetTweetByIdQuery(
    tweetDto.tweetId
  );

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const likePost = () => {
    if (currentTweet) {
      setIsLiked(!isLiked);
      likeTweet(currentTweet.tweet_id)
        .unwrap()
        .then((payload) => {
          console.log(payload);
        });
    }
  };

  const unLikePost = () => {
    if (currentTweet) {
      setIsLiked(!isLiked);
      unLikeTweet(currentTweet.tweet_id)
        .unwrap()
        .then((payload) => {
          console.log(payload);
        });
    }
  };

  const repost = () => {
    if (currentTweet) {
      retweet(currentTweet.tweet_id)
        .unwrap()
        .then((payload) => {
          console.log(payload);
        });
    }
  };

  const undoRepost = () => {
    if (currentTweet) {
      undoRetweet(currentTweet.tweet_id)
        .unwrap()
        .then((payload) => {
          console.log(payload);
        });
    }
  };

  useEffect(() => {
    if (currentTweet) {
      const liked = currentTweet.likedByUsers.some(
        (v: User) => v.user_id === user?.user_id
      );
      setIsLiked(liked);
    }
  }, [isLiked, currentTweet, user]);

  return (
    <div className="flex items-center justify-between my-2">
      <div
        onClick={handleOpen}
        className="flex items-center cursor-pointer text-gray-500 hover:text-blue-400"
      >
        <FaRegComment />
        <p className="px-2">{currentTweet?.comments.length}</p>
      </div>
      <Modal open={open} onClose={handleClose}>
        <Box sx={style}>
          <CommentPost tweet={currentTweet!} onClose={handleClose} />
        </Box>
      </Modal>
      <div
        className="flex items-center cursor-pointer text-gray-500 hover:text-blue-400"
        onClick={isReposted ? undoRepost : repost}
      >
        {isReposted ? <BiRepost className="text-green-400" /> : <BiRepost />}
        <p className={`px-2 ${isReposted ? "text-green-400" : ""}`}>
          {currentTweet?.repostedNumber}
        </p>
      </div>
      <div
        className="flex items-center cursor-pointer text-gray-500 hover:text-blue-400"
        onClick={isLiked ? unLikePost : likePost}
      >
        {isLiked ? (
          <AiFillHeart className="text-red-500" />
        ) : (
          <AiOutlineHeart />
        )}
        <p className={`px-2 ${isLiked ? "text-red-500" : ""}`}>
          {currentTweet?.likedByUsers.length}
        </p>
      </div>
    </div>
  );
};

export default PostAction;
