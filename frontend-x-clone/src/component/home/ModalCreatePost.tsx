import React, { useEffect, useRef, useState } from "react";
import { XMarkIcon } from "@heroicons/react/24/outline";
import { BsCardImage } from "react-icons/bs";
// import TweetService from "../../services/tweets/TweetService";
import AuthService from "../../services/auth/AuthService";
import { useNavigate } from "react-router-dom";

type Props = {
  open: boolean;
  onClose: () => void;
};

export const ModalCreatePost: React.FC<Props> = ({ open, onClose }) => {
  const textAreaRef = useRef<HTMLTextAreaElement>(null);
  const navigate = useNavigate();
  const userAuth = AuthService.getCurrentUser();
  const [postCreate, setPostCreate] = useState<string>("");
  const formData = new FormData();
  const ref = useRef<HTMLInputElement>(null);

  const handleTextInput = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setPostCreate(e.target.value);
  };

  const handleClick = () => {
    ref.current?.click();
  };

  const onFileChangeHandler = (e: any) => {
    e.preventDefault();
    formData.append("image", e.target.files[0]);
  };

  useEffect(() => {
    if (textAreaRef.current) {
      textAreaRef.current.style.height = "auto";
      textAreaRef.current.style.height =
        textAreaRef.current.scrollHeight + "px";
    }
  }, [postCreate]);

  const closeModal = () => {
    setPostCreate("");
    onClose();
  };

  const savePost = async () => {
    // try {
    //   formData.append("content", postCreate);
    //   await TweetService.createTweet(formData).then(
    //     (response) => {
    //       closeModal();
    //     },
    //     (error) => {
    //       if (error.response.status === 401) {
    //         AuthService.logout();
    //         navigate("/login");
    //         window.location.reload();
    //       }
    //       console.log("Error: ", error);
    //     }
    //   );
    // } catch (err) {
    //   if (err) {
    //     console.log("error catched: ", err);
    //   }
    // }
    setPostCreate("");
  };

  return (
    <div
      className={`fixed top-10 left-[35%] w-[100%] justify-center items-center z-10 bg-white tounded-lg shadow p-5 transition-all max-w-md
         ${open ? "visible bg-black-20" : "invisible"}`}
    >
      <div
        className={`flex justify-between w-[100%]   ${
          open ? "scale-100 opacity-100" : "scale-110 opacity-0"
        }`}
      >
        <button
          className="p-2 rounded-full bg-white hover:bg-gray-300"
          onClick={() => closeModal()}
        >
          <XMarkIcon className="w-6" />
        </button>
        <button className="post">Drafts</button>
      </div>
      <div className="flex">
        <img
          className="w-10 h-10 rounded-full"
          src={`${
            userAuth.user.image
              ? `${userAuth.user.image}`
              : "https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png"
          }`}
          alt=""
        />
        <textarea
          id="my-textarea"
          className="border-none m-2 w-[90%] resize-none outline-none"
          name="postContent"
          placeholder="What is happening?"
          maxLength={230}
          value={postCreate}
          onChange={handleTextInput}
          ref={textAreaRef}
        />
      </div>
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
  );
};

export default ModalCreatePost;
