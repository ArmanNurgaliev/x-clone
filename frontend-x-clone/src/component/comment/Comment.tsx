import { BsDot } from "react-icons/bs";
import { useNavigate } from "react-router-dom";
import Logic from "../../services/config/Logic";
import { useGetAuthUserQuery } from "../../store/users/users.api.endpoints";
import {
  useDeleteCommentMutation,
  useLikeCommentMutation,
  useUnLikeCommentMutation,
} from "../../store/comments/comments.api.endpoints";
import toast, { Toaster } from "react-hot-toast";
import DeletePost from "../delete/DeletePost";
import { useState } from "react";
import { AiFillHeart, AiOutlineHeart } from "react-icons/ai";

type Props = {
  comment: CommentType;
};

const Comment: React.FC<Props> = ({ comment }) => {
  const navigate = useNavigate();
  const timeDiff = Logic.timeDifference(comment.createdAt);
  const [isLiked, setIsLiked] = useState<boolean>();
  const { data: user } = useGetAuthUserQuery();
  const [deleteComment] = useDeleteCommentMutation();
  const [likeCom] = useLikeCommentMutation();
  const [unlikeComment] = useUnLikeCommentMutation();

  const handleDelete = () => {
    deleteComment(comment.comment_id)
      .unwrap()
      .then((resp) => {
        toast("Comment deleted", {
          duration: 4000,
          position: "bottom-center",
          style: {
            color: "red",
          },
        });
      });
  };

  const likeComment = () => {
    setIsLiked(!isLiked);
    likeCom(comment.comment_id)
      .unwrap()
      .then((payload) => {
        console.log(payload);
      });
  };

  const unLikeComment = () => {
    setIsLiked(!isLiked);
    unlikeComment(comment.comment_id)
      .unwrap()
      .then((payload) => {
        console.log(payload);
      });
  };

  return (
    <div>
      <Toaster />
      <div className="flex m-2">
        <div>
          <img
            onClick={() => navigate("/profile")}
            className="w-10 h-10 rounded-full cursor-pointer"
            src={`${
              user && user.image
                ? `data:image/jpeg;base64,${user.image}`
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
                onClick={() => navigate("/profile")}
              >
                {comment.user.name}
              </p>
              <p
                className="text-gray-400 px-1 cursor-pointer"
                onClick={() => navigate("/profile")}
              >
                {comment.user.login}
              </p>
              <BsDot className="text-gray-400" />
              <p className="text-gray-400 px-1">{timeDiff}</p>
            </div>

            <div className="rounded-full hover:bg-blue-100">
              {(user?.user_id === comment.user.user_id || Logic.isAdmin(user!)) && (
                <DeletePost handleDelete={handleDelete} />
              )}
            </div>
          </div>

          <div>
            <p>{comment.content}</p>
            {comment.imageData && (
              <img
                className="block w-[400px] h-[400px]"
                src={`data:image/jpeg;base64,${comment.imageData}`}
              />
            )}
          </div>

          <div
            className="flex justify-end items-center cursor-pointer text-gray-500 hover:text-blue-400"
            onClick={isLiked ? unLikeComment : likeComment}
          >
            {isLiked ? (
              <AiFillHeart className="text-red-500" />
            ) : (
              <AiOutlineHeart />
            )}
            <p className={`px-2 ${isLiked ? "text-red-500" : ""}`}>
              {comment.likedByUsers.length}
            </p>
          </div>
        </div>
      </div>

      <hr />
    </div>
  );
};

export default Comment;
