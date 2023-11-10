import React, { useState } from "react";
import { BiArrowBack } from "react-icons/bi";
import { NavLink, useNavigate, useParams } from "react-router-dom";
import {
  useGetAuthUserQuery,
  useGetUserByLoginQuery,
} from "../../store/users/users.api.endpoints";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { Box, Button, Tab } from "@mui/material";
import UserList from "../../component/followers/UserList";

const Followers = () => {
  const { data: user } = useGetAuthUserQuery();
  const { login } = useParams();
  const path = window.location.pathname.split("/")[2];
  const navigate = useNavigate();

  const [tab, setTab] = useState<string>(path);

  const { data: profileUser, isLoading } = useGetUserByLoginQuery(login!);

  const handleChange = (event: React.SyntheticEvent, newValue: string) => {
    setTab(newValue);
    navigate(`/${login}/${newValue}`);
  };

  return (
    <div className="w-full h-full border border-b-0 border-l-0">
      <div className="flex items-center ml-2">
        <div
          className="px-2 py-2 rounded-full hover:bg-gray-200"
          onClick={() => navigate(-1)}
        >
          <BiArrowBack className="w-6 h-6" />
        </div>
        <div className="px-8">
          <p className="text-xl font-bold">{profileUser?.name}</p>
          <p className="text-sm text-gray-400">{profileUser?.login}</p>
        </div>
      </div>
      <TabContext value={tab}>
        <Box
          sx={{
            borderBottom: 1,
            borderColor: "divider",
            justifyContent: "space-between",
          }}
        >
          <TabList onChange={handleChange} centered>
            <Tab label="Followers" value="followers" />
            <Tab label="Following" value="following" />
          </TabList>
        </Box>
        <TabPanel sx={{ padding: 0 }} value="followers">
          {isLoading ? (
            <div>Loading...</div>
          ) : profileUser ? (
            profileUser.followers.map((item: User) => (
              <UserList profileUser={item} key={item.user_id} />
            ))
          ) : (
            <div>Not found</div>
          )}
        </TabPanel>
        <TabPanel value="following" sx={{ m: 0, p: 0 }}>
          {isLoading ? (
            <div>Loading...</div>
          ) : profileUser ? (
            profileUser.following.map((item: User) => (
              <UserList profileUser={item} key={item.user_id} />
            ))
          ) : (
            <div>Not found</div>
          )}
        </TabPanel>
      </TabContext>
    </div>
  );
};

export default Followers;
