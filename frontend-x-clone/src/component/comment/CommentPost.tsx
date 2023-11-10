import { BsDot } from "react-icons/bs";
import CommentPostFrom from "./CommentPostFrom";
import Logic from "../../services/config/Logic";

type Props = {
  tweet: Tweet;
  onClose: () => void
};

const CommentPost: React.FC<Props> = ({ tweet, onClose }) => {
  return (
    <div>
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
              <p className="text-gray-400 px-1">{Logic.timeDifference(tweet.createdAt)}</p>
            </div>
          </div>

          <div>
            <p>{tweet.content}</p>
          </div>
        </div>
      </div>
      <div className="ml-7 border-l-2 border-l-gray-400 h-[20px] border-r-0  border-b-0  border-t-0"></div>
      <CommentPostFrom tweet={tweet} onClose={onClose}/>
    </div>
  );
};

export default CommentPost;
