import { createApi } from "@reduxjs/toolkit/query/react";
import { baseQueryWithReauth } from "../baseQuery";

export const tweetsApi = createApi({
  reducerPath: "tweetsApi",
  tagTypes: ["TweetDtos", "TweetDto", "LikedTweetDtos", "Tweet", "Repost"],
  baseQuery: baseQueryWithReauth,
  endpoints: () => ({}),
});
