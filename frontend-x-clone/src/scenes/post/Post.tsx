import { useNavigate, useParams } from "react-router-dom";
import { BiArrowBack } from "react-icons/bi";
import Comment from "../../component/comment/Comment";
import {
  useGetTweetByIdQuery,
  useGetTweetDtoByIdQuery,
} from "../../store/tweets/tweets.api.endpoints";
import PostData from "../../component/post/PostData";
import CommentPostFrom from "../../component/comment/CommentPostFrom";
import { useGetCommentsByTweetIdQuery } from "../../store/comments/comments.api.endpoints";

const Post = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const { data: tweetDto, isLoading } = useGetTweetDtoByIdQuery(
    parseInt(id as string)
  );

  const { data: tweet } = useGetTweetByIdQuery(tweetDto?.tweetId!!, {
    skip: !tweetDto?.tweetId,
  });

  const { data: comments, isLoading: isCommentsLoading } =
    useGetCommentsByTweetIdQuery(tweetDto?.tweetId!!, {
      skip: !tweetDto?.tweetId,
    });

  return (
    <div className="border border-l-0 border-b-0 border-t-0">
      <div className="flex items-center">
        <div className="px-2 py-2 rounded-full hover:bg-gray-200" onClick={() => navigate(-1)}>
          <BiArrowBack className="w-6 h-6" />
        </div>
        <div className="px-8">
          <p className="text-xl font-bold">Post</p>
        </div>
      </div>

      {isLoading ? (
        <div>Loading...</div>
      ) : tweetDto ? (
        <div className="m-2">
          <PostData tweetDto={tweetDto} />
          {tweet && <CommentPostFrom tweet={tweet} onClose={() => {}} />}
          <hr />
          <div>
            {/* Comments to post */}
            {isCommentsLoading ? (
              <div>Comments loading...</div>
            ) : comments ? (
              comments.map((item) => (
                <Comment key={item.comment_id} comment={item} />
              ))
            ) : (
              <div>Not found...</div>
            )}
          </div>
        </div>
      ) : (
        <div>Not found</div>
      )}
    </div>
  );
};

export default Post;
