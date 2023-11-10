import { useState, useEffect, useRef } from "react";
import { BsCardImage } from "react-icons/bs";
import { useCreateCommentMutation } from "../../store/comments/comments.api.endpoints";
import { useGetAuthUserQuery } from "../../store/users/users.api.endpoints";
import Logic from "../../services/config/Logic";

type Props = {
  tweet: Tweet;
  onClose: () => void
};

const CommentPostFrom: React.FC<Props> = ({ tweet, onClose }) => {
  const initialComment: PostDto = {
    content: "",
    image: "",
  };

  const [newComment, setNewComment] = useState<PostDto>(initialComment);
  const textAreaRef = useRef<HTMLTextAreaElement>(null);
  const { data: user } = useGetAuthUserQuery();
  const ref = useRef<HTMLInputElement>(null);
  const [createComment] = useCreateCommentMutation();

  const handleTextInput = (e: any) => {
    setNewComment((prev) => ({
      ...newComment,
      [e.target.name]: e.target.value,
    }));
  };

  const handleClick = () => {
    ref.current?.click();
  };

  const onFileChangeHandler = (e: any) => {
    e.preventDefault();
    Logic.getBase64(e.target.files[0])
      .then((res: any) => {
        setNewComment(() => ({
          ...newComment,
          image: res,
        }));
      })
      .catch((err) => console.log(err));
  };

  useEffect(() => {
    if (textAreaRef.current) {
      textAreaRef.current.style.height = "auto";
      textAreaRef.current.style.height =
        textAreaRef.current.scrollHeight + "px";
    }
  }, [newComment.content]);

  const replyToPost = async () => {
    createComment({ body: newComment, tweet_id: tweet.tweet_id });
    setNewComment(initialComment);
    onClose();
  };

  return (
    <div className="m-2">
      <div className="flex">
        <img
          className="w-10 h-10 rounded-full cursor-pointer"
          src={`${
            user && user.image
              ? `data:image/jpeg;base64,${user.image}`
              : "https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png"
          }`}
          alt=""
        />
        <textarea
          id="my-textarea"
          className="border-none m-2 w-[90%] resize-none outline-none"
          name="content"
          placeholder="Add another post"
          maxLength={230}
          value={newComment.content}
          onChange={handleTextInput}
          ref={textAreaRef}
        />
      </div>
      <div className="flex justify-between items-center m-2">
        <div
          onClick={handleClick}
          className="px-2 py-2 rounded-full hover:bg-blue-100"
        >
          <BsCardImage />
          <input
            name="file"
            type="file"
            ref={ref}
            className="hidden"
            onChange={onFileChangeHandler}
          />
        </div>
        <button className="post py-2 px-4" onClick={replyToPost}>
          Reply
        </button>
      </div>
    </div>
  );
};

export default CommentPostFrom;
