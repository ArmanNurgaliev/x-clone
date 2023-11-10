import { useState, useRef, useEffect } from "react";
import { BsCardImage } from "react-icons/bs";
import { useCreateTweetMutation } from "../../store/tweets/tweets.api.endpoints";
import Logic from "../../services/config/Logic";
import { useGetAuthUserQuery } from "../../store/users/users.api.endpoints";

export type ICreateTweet = {
  content: string,
  image: string
}

const PostForm = () => {
  const tweetInitial = {
    content: "",
    image: ""
  }
  const textAreaRef = useRef<HTMLTextAreaElement>(null);
  const [postCreate, setPostCreate] = useState<ICreateTweet>(tweetInitial);
  const ref = useRef<HTMLInputElement>(null);
  const { data: user } = useGetAuthUserQuery();
  const [createTweet] = useCreateTweetMutation();

  const handleClick = () => {
    ref.current?.click();
  };

  const onFileChangeHandler = (e: any) => {
    e.preventDefault();
    const file = e.target.files[0];
    
    Logic.getBase64(file)
      .then((res: any) => {
        setPostCreate(() => ({
          ...postCreate,
          image: res,
        }));
      })
      .catch((err) => console.log(err));
  };

  const handleChange = (e: any) => {
    setPostCreate((prev) => ({
      ...postCreate,
      [e.target.name]: e.target.value,
    }));
  }

  useEffect(() => {
    if (textAreaRef.current) {
      textAreaRef.current.style.height = "auto";
      textAreaRef.current.style.height =
        textAreaRef.current.scrollHeight + "px";
    }
  }, [postCreate]);

  const savePost = async () => {
    if (postCreate.content.trim().length > 0 || postCreate.image.length > 0) {
      await createTweet(postCreate);
    }

    setPostCreate(tweetInitial);
  };

  return (
    <div className="w-[100%] justify-center items-center bg-white">
      <div className="m-2 flex">
        <div>
          <img
            className="w-10 h-10 rounded-full"
            src={`${
              user && user.image
                ? `data:image/jpeg;base64,  ${user.image}`
                : "https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png"
            }`}
            alt=""
          />
        </div>
        <div className="w-[100%]">
          <textarea
            id="my-textarea"
            className="text-xl border-none m-2 w-[90%] resize-none outline-none"
            name="content"
            placeholder="What is happening?"
            value={postCreate.content}
            maxLength={230}
            onChange={handleChange}
            ref={textAreaRef}
          />

          <hr />
          <div className="flex justify-between items-center mt-2">
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
            <button className="post py-2 px-4" onClick={savePost}>
              Post
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PostForm;
