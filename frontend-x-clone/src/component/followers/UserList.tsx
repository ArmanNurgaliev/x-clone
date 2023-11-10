import { Alert, Button, Snackbar } from "@mui/material";
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  useFollowUserMutation,
  useGetAuthUserQuery,
  useUnFollowUserMutation,
} from "../../store/users/users.api.endpoints";

const style = {
  px: 2,
  zIndex: 10,
  color: "white",
  fontWeight: "bold",
  textTransform: "none",
  borderColor: "black",
  border: "solid 1px",
  borderRadius: "9999px",
  backgroundColor: "black",
  "&:hover": {
    backgroundColor: "#424242",
  },
};

type Props = {
  profileUser: User;
};

const UserList: React.FC<Props> = ({ profileUser }) => {
  const navigate = useNavigate();
  const [message, setMessage] = useState<string>("");
  const [open, setOpen] = useState<boolean>(false);
  const [isFollowing, setIsFollowing] = useState<boolean>();
  const [unFollow] = useUnFollowUserMutation();
  const [follow] = useFollowUserMutation();
  const { data: user } = useGetAuthUserQuery();

  const unFollowUser = async (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>
  ) => {
    e.stopPropagation();
    await unFollow(profileUser.user_id)
      .unwrap()
      .then((response) => {
        const data = JSON.stringify(response);
        setMessage(JSON.parse(data).message);
        setOpen(true);
      });
  };

  const followUser = async (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>
  ) => {
    e.stopPropagation();
    await follow(profileUser.user_id)
      .unwrap()
      .then((response) => {
        const data = JSON.stringify(response);
        setMessage(JSON.parse(data).message);
        setOpen(true);
      });
  };

  useEffect(() => {
    const isFol = profileUser.followers?.some((u: User) => {
      return u.login === user?.login;
    });
    setIsFollowing(isFol);
  }, [user, profileUser]);

  return (
    <div
      key={profileUser.user_id}
      className="p-2 z-0 cursor-pointer hover:bg-gray-300"
      onClick={() => navigate(`/${profileUser.login}`)}
    >
      <div className="flex">
        <div className="m-auto">
          <img
            className="w-10 h-10 rounded-full cursor-pointer"
            src={`${
              profileUser.image
                ? `data:image/jpeg;base64,${profileUser.image}`
                : "https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png"
            }`}
            alt=""
          />
        </div>
        <div className="w-[90%] flex justify-between items-center">
          <div className="text-left pl-2">
            <p className="font-bold cursor-pointer">{profileUser.name}</p>
            <p className="text-gray-400 cursor-pointer">{profileUser.login}</p>
          </div>
          <div className="rounded-full hover:bg-blue-100">
            {user?.user_id === profileUser.user_id ? (
              <></>
            ) : !isFollowing ? (
              <Button sx={style} onClick={unFollowUser}>
                Unfollow
              </Button>
            ) : (
              <Button sx={style} onClick={followUser}>
                Follow
              </Button>
            )}
            <Snackbar
              open={open}
              autoHideDuration={3000}
              onClose={() => setOpen(false)}
              anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
            >
              <Alert
                onClose={() => setOpen(false)}
                severity="success"
                sx={{ width: "100%" }}
              >
                {message}
              </Alert>
            </Snackbar>
          </div>
        </div>
      </div>
      <div className="ml-[4rem] z-10 text-gray-500">
        <p>{profileUser.bio}</p>
      </div>
    </div>
  );
};

export default UserList;
