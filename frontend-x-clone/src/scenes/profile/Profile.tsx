import { BiArrowBack } from "react-icons/bi";
import { format } from "date-fns";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import PostItem from "../../component/home/PostItem";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { Alert, Box, Button, Modal, Snackbar, Tab } from "@mui/material";
import { useGetTweetsByLoginQuery } from "../../store/tweets/tweets.api.endpoints";
import {
  useFollowUserMutation,
  useGetAuthUserQuery,
  useGetUserByLoginQuery,
  useUnFollowUserMutation,
} from "../../store/users/users.api.endpoints";
import EditProfile from "../../component/profile/EditProfile";
import { useGetCommentsQuery } from "../../store/comments/comments.api.endpoints";
import Replies from "../../component/reply/Replies";
import Posts from "../../component/profile/Posts";
import LikedTweets from "../../component/profile/LikedTweets";
import Media from "../../component/profile/Media";

const styleButton = {
  px: 2,
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

const style = {
  position: "absolute" as "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  height: 500,
  width: 600,
  bgcolor: "background.paper",
  // border: "2px solid #000",
  borderRadius: "10px",
  boxShadow: 24,
  overflowY: "scroll",
};

const Profile = () => {
  const navigate = useNavigate();
  const [tab, setTab] = useState<string>("1");
  const { login } = useParams();
  const [isFollowing, setIsFollowing] = useState<boolean>();
  const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);
  const [openEditForm, setOpenEditForm] = useState<boolean>(false);
  const [message, setMessage] = useState<string>("");
  const { data: user } = useGetAuthUserQuery();
  const { data: tweetDtos, isLoading } = useGetTweetsByLoginQuery(login!);
  const { data: comments, isLoading: isCommentsLoading } =
    useGetCommentsQuery();
  const [follow] = useFollowUserMutation();
  const [unFollow] = useUnFollowUserMutation();

  const { data: profileUser } = useGetUserByLoginQuery(login!);

  const handleChange = (event: React.SyntheticEvent, newValue: string) => {
    setTab(newValue);
  };

  const followUser = async () => {
    await follow(profileUser?.user_id!)
      .unwrap()
      .then((response) => {
        const data = JSON.stringify(response);
        setMessage(JSON.parse(data).message);
        setOpenSnackbar(true);
      });
  };

  const unFollowUser = async () => {
    await unFollow(profileUser?.user_id!)
      .unwrap()
      .then((response) => {
        const data = JSON.stringify(response);
        setMessage(JSON.parse(data).message);
        setOpenSnackbar(true);
      });
  };

  useEffect(() => {
    const isFol = profileUser?.followers?.some((u: User) => {
      return u.login === user?.login;
    });
    setIsFollowing(isFol);
  }, [login, profileUser]);

  return (
    <div className="flex">
      <div className="w-full border border-b-0 border-l-0">
        <div className="flex items-center ml-2">
          <div className="px-2 py-2 rounded-full hover:bg-gray-200" onClick={() => navigate(-1)}>
            <BiArrowBack className="w-6 h-6" />
          </div>
          <div className="px-8">
            <p className="text-xl font-bold">{login}</p>
            <p className="text-sm text-gray-400">{tweetDtos?.length} posts</p>
          </div>
        </div>
        <div
          className="w-full h-[200px]"
          style={{
            backgroundImage: profileUser?.backgroundImage
              ? `url(data:image/jpeg;base64,${profileUser?.backgroundImage})`
              : `url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRg1c5wCvtzGCuFfQ4KZxkTzd46DYsI7KmuEa8WfE8&s")`,
          }}
        ></div>
        {user && profileUser && (
          <div className="ml-4">
            <div className="flex justify-between items-start p-4">
              <div></div>
              {user.login === login ? (
                <>
                  <Button
                    sx={{
                      px: 2,
                      color: "black",
                      fontWeight: "bold",
                      textTransform: "none",
                      borderColor: "black",
                      border: "solid 1px",
                      borderRadius: "9999px",
                      "&:hover": {
                        backgroundColor: "#e0e0e0",
                      },
                    }}
                    onClick={() => setOpenEditForm(true)}
                  >
                    Edit profile
                  </Button>
                  <Modal
                    open={openEditForm}
                    onClose={() => setOpenEditForm(false)}
                    aria-labelledby="modal-modal-title"
                    aria-describedby="modal-modal-description"
                  >
                    <Box sx={style}>
                      <EditProfile
                        user={profileUser}
                        open={openEditForm}
                        onClose={() => setOpenEditForm(false)}
                      />
                    </Box>
                  </Modal>
                </>
              ) : isFollowing ? (
                <Button sx={styleButton} onClick={unFollowUser}>
                  Unfollow
                </Button>
              ) : (
                <Button sx={styleButton} onClick={followUser}>
                  Follow
                </Button>
              )}
            </div>
            <div className="absolute cursor-pointer top-[170px]">
              <img
                className="w-[150px] h-[150px] rounded-full border-4 border-white"
                src={`${
                  profileUser.image
                    ? `data:image/jpeg;base64,${profileUser.image}`
                    : "https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png"
                }`}
                alt=""
              />
            </div>
            <div className="mt-1">
              <p className="text-xl font-bold">{profileUser.name}</p>
              <p className="text-gray-400">{profileUser.login}</p>
            </div>
            <div className="my-2">
              <p>{profileUser.bio}</p>
            </div>
            <div className="flex items-center text-lg">
              <p className="text-gray-400 mr-3">
                {profileUser.location ? profileUser.location : "Here"}
              </p>
              <p className="text-gray-400">
                Joined {format(new Date(profileUser.createdAt), "MMMM yyyy")}
              </p>
            </div>
            <div className="flex items-center mt-1">
              <Link
                to={`/${login}/following`}
                className="flex cursor-pointer hover:underline"
              >
                <p>{profileUser.following?.length}</p>
                <p className="text-gray-400 ml-1 mr-3">Following</p>
              </Link>
              <Link
                className="flex cursor-pointer hover:underline"
                to={`/${login}/followers`}
              >
                <p>{profileUser.followers?.length}</p>
                <p className="text-gray-400 ml-1 mr-3">Followers</p>
              </Link>
            </div>
          </div>
        )}
        <TabContext value={tab}>
          <Box
            sx={{
              borderBottom: 1,
              borderColor: "divider",
              justifyContent: "space-between",
            }}
          >
            <TabList onChange={handleChange} centered>
              <Tab label="Posts" value="1" />
              <Tab label="Replies" value="2" />
              <Tab label="Media" value="3" />
              <Tab label="Likes" value="4" />
            </TabList>
          </Box>
          <TabPanel sx={{ padding: 0 }} value="1">
            {login && <Posts login={login} />}
          </TabPanel>
          <TabPanel sx={{ padding: 0 }} value="2">
            {isCommentsLoading ? (
              <div>Loading...</div>
            ) : comments ? (
              comments.map((item: CommentType) => (
                <>
                  <Replies key={item.comment_id} comment={item} />
                  <hr />
                </>
              ))
            ) : (
              <div>Not found</div>
            )}
          </TabPanel>
          <TabPanel sx={{ padding: 0 }} value="3">
            { profileUser && <Media user_id={profileUser.user_id} /> }
          </TabPanel>
          <TabPanel sx={{ padding: 0 }} value="4">
            {profileUser && <LikedTweets user_id={profileUser.user_id} />}
          </TabPanel>
        </TabContext>
      </div>
      <Snackbar
        open={openSnackbar}
        autoHideDuration={3000}
        onClose={() => setOpenSnackbar(false)}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          onClose={() => setOpenSnackbar(false)}
          severity="success"
          sx={{ width: "100%" }}
        >
          {message}
        </Alert>
      </Snackbar>
    </div>
  );
};

export default Profile;
