import "./Home.css";
import PostItem from "../../component/home/PostItem";
import { Box, Tab } from "@mui/material";
import { useState, useEffect, useRef } from "react";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import PostForm from "../../component/home/PostForm";
import {
  useGetAuthUserTweetsQuery,
  useGetFollowingsTweetsQuery,
} from "../../store/tweets/tweets.api.endpoints";

const Home = () => {
  const [tab, setTab] = useState<string>("1");
  const { data, isLoading } = useGetAuthUserTweetsQuery();
  const { data: followingsTweets, isLoading: isFollowingsTweetsLoading } =
    useGetFollowingsTweetsQuery();

  const handleChange = (event: React.SyntheticEvent, newValue: string) => {
    setTab(newValue);
  };

  return (
    <div className="flex w-[100%]]">
      <div className="w-full border border-b-0">
        <p className="text-xl font-bold m-4">Home</p>

        <TabContext value={tab}>
          <Box
            sx={{
              borderBottom: 1,
              borderColor: "divider",
              justifyContent: "space-between",
            }}
          >
            <TabList onChange={handleChange}>
              <Tab label="For You" value="1" />
              <Tab label="Following" value="2" />
            </TabList>
          </Box>
          <TabPanel sx={{ padding: 0 }} value="1">
            <PostForm />
            <hr />
            <div className="w-[100%]">
              {isLoading ? (
                <div>Loading...</div>
              ) : data ? (
                data.map((item: TweetDto) => (
                  <PostItem key={item.id} tweetDto={item} />
                ))
              ) : (
                <div>Not found</div>
              )}
            </div>
          </TabPanel>
          <TabPanel sx={{ padding: 0 }} value="2">
            <div className="w-[100%]">
              {isFollowingsTweetsLoading ? (
                <div>Loading...</div>
              ) : followingsTweets ? (
                followingsTweets.map((item: TweetDto) => (
                  <PostItem key={item.id} tweetDto={item} />
                ))
              ) : (
                <div>Not found</div>
              )}
            </div>
          </TabPanel>
        </TabContext>

        <hr />
      </div>
    </div>
  );
};

export default Home;
