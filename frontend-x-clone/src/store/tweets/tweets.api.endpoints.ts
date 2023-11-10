import { ICreateTweet } from "../../component/home/PostForm";
import { tweetsApi } from "./tweets.api";

export const tweetsApiEndpoints = tweetsApi.injectEndpoints({
  endpoints: (builder) => ({
    getTweetById: builder.query<Tweet, number>({
      query: (id) => ({
        url: `/tweets/id/${id}`,
      }),
      providesTags: [{ type: "Tweet" }]
    }),
    likeTweet: builder.mutation<string, number>({
      query: (id) => ({
        url: `/tweets/like/${id}`,
        method: "PUT",
      }),
      invalidatesTags: [{ type: 'LikedTweetDtos', id: 'LIST' }, { type: "Tweet" }],
    }),
    unLikeTweet: builder.mutation<string, number>({
      query: (id) => ({
        url: `/tweets/unlike/${id}`,
        method: "PUT",
      }),
      invalidatesTags: [{ type: 'LikedTweetDtos', id: 'LIST' }, { type: "Tweet" }],
    }),
    getAuthUserTweets: builder.query<TweetDto[], void>({
      query: () => ({
        url: `/tweetDto`,
      }),
      providesTags: (result) =>
        result ?
            [
              ...result.map(({ id }) => ({ type: 'TweetDtos', id } as const)),
              { type: 'TweetDtos', id: 'LIST' },
            ] :
            [{ type: 'TweetDtos', id: 'LIST' }],
    }),
    getTweetsByLogin: builder.query<TweetDto[], string>({
      query: (login) => ({
        url: `/tweetDto/user/${login}`,
      }),
      providesTags: (result) =>
        result ?
            [
              ...result.map(({ id }) => ({ type: 'TweetDtos', id } as const)),
              { type: 'TweetDtos', id: 'LIST' },
            ] :
            [{ type: 'TweetDtos', id: 'LIST' }],
    }),
    getFollowingsTweets: builder.query<TweetDto[], void>({
      query: () => ({
        url: `/tweetDto/following`,
      }),
      providesTags: (result) =>
        result ?
            [
              ...result.map(({ id }) => ({ type: 'TweetDtos', id } as const)),
              { type: 'TweetDtos', id: 'LIST' },
            ] :
            [{ type: 'TweetDtos', id: 'LIST' }],
    }),
    getLikedTweets: builder.query<TweetDto[], number>({
      query: (user_id) => ({
        url: `/tweetDto/liked/${user_id}`,
      }),
      providesTags: (result) =>
        result ?
            [
              ...result.map(({ id }) => ({ type: 'LikedTweetDtos', id } as const)),
              { type: 'LikedTweetDtos', id: 'LIST' },
            ] :
            [{ type: 'LikedTweetDtos', id: 'LIST' }],
    }),
    getTweetsMedia: builder.query<TweetDto[], number>({
      query: (user_id) => ({
        url: `/tweetDto/media/${user_id}`,
      }),
    }),
    getTweetDtoById: builder.query<TweetDto, number>({
      query: (id) => ({
        url: `/tweetDto/id/${id}`,
      }),
      providesTags: (result, error, id) => [{ type: 'TweetDto', id }],
    }),
    getTweetDtoByCommentId: builder.query<TweetDto, number>({
      query: (comment_id) => ({
        url: `/tweetDto/replies/${comment_id}`,
      }),
      providesTags: (result, error, id) => [{ type: 'TweetDto', id }],
    }),
    createTweet: builder.mutation<TweetDto, ICreateTweet>({
      query: (tweet) => ({
        url: "/tweets/create",
        method: "POST",
        body: tweet,
      }),
      invalidatesTags: [{ type: 'TweetDtos', id: 'LIST' }],
      
    }),
    retweet: builder.mutation<string, number>({
      query: (id) => ({
        url: `/tweetDto/repost/${id}`,
        method: "PUT",
      }),
      invalidatesTags: [{ type: "TweetDtos" }, {type: "Tweet"}, { type: "Repost" }]
    }),
    isTweetReposted: builder.query<boolean, number>({
      query: (id) => ({
        url: `/tweetDto/reposted/${id}`,
      }),
      providesTags: [{ type: "Repost" }]
    }),
    undoRetweet: builder.mutation<string, number>({
      query: (id) => ({
        url: `/tweetDto/undoRepost/${id}`,
        method: "PUT",
      }),
      invalidatesTags: [{ type: "TweetDtos" }, {type: "Tweet"}, { type: "Repost" }]
    }),
    deleteTweet: builder.mutation<string, number>({
      query: (id) => ({
        url: `/tweetDto/delete/${id}`,
        method: "DELETE"
      }),
      // invalidatesTags: [{ type: 'TweetDtos' }],
      invalidatesTags: (result, error, id) => [{ type: 'TweetDtos', id }],
    })
  }),
});

export const { useGetTweetByIdQuery, useLikeTweetMutation, useUnLikeTweetMutation, useGetAuthUserTweetsQuery, useGetTweetsByLoginQuery,
useCreateTweetMutation, useGetTweetDtoByIdQuery, useRetweetMutation, useUndoRetweetMutation, useIsTweetRepostedQuery, useLazyGetAuthUserTweetsQuery,
useDeleteTweetMutation, useGetFollowingsTweetsQuery, useGetLikedTweetsQuery, useGetTweetDtoByCommentIdQuery, useGetTweetsMediaQuery } =
  tweetsApiEndpoints;
