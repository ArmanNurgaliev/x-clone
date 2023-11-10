import { tweetsApi } from "../tweets/tweets.api";
import { commentsApi } from "./comment.api";

export const commentsApiEndpoints = commentsApi.injectEndpoints({
  endpoints: (builder) => ({
    getComments: builder.query<CommentType[], void>({
      query: () => ({
        url: `/comments`,
      }),
      providesTags: (result) =>
        result ?
            [
              ...result.map(({ comment_id }) => ({ type: 'Comments', comment_id } as const)),
              { type: 'Comments', id: 'LIST' },
            ] :
            [{ type: 'Comments', id: 'LIST' }],
    }),
    getCommentsByTweetId: builder.query<CommentType[], number>({
      query: (tweet_id) => ({
        url: `/comments/tweet/${tweet_id}`,
      }),
      providesTags: (result) =>
        result ?
            [
              ...result.map(({ comment_id }) => ({ type: 'Comments', comment_id } as const)),
              { type: 'Comments', id: 'LIST' },
            ] :
            [{ type: 'Comments', id: 'LIST' }],
    }),
    getCommentsById: builder.query<CommentType, number>({
      query: (id) => ({
        url: `/comments/id/${id}`,
      }),
      providesTags: (result, error, id) => [{ type: 'Comment', id }],
    }),
    createComment: builder.mutation<CommentType, { body: PostDto; tweet_id: number; }>({
      query: (data) => ({
        url: `/comments/create/${data.tweet_id}`,
        method: "POST",
        body: data.body
      }),
      invalidatesTags: [{ type: 'Comments', id: 'LIST' }],
      async onQueryStarted(_, { dispatch, queryFulfilled }) {
        await queryFulfilled;
        dispatch(tweetsApi.util.invalidateTags(["Tweet"]));
      },
    }),
    likeComment: builder.mutation<string, number>({
      query: (id) => ({
        url: `/comments/like/${id}`,
        method: "PUT",
      }),
      invalidatesTags: [{ type: 'Comments', id: 'LIST' }, { type: "Comment" }],
      // invalidatesTags: [{ type: "Comment" }]
    }),
    unLikeComment: builder.mutation<string, number>({
      query: (id) => ({
        url: `/comments/unlike/${id}`,
        method: "PUT",
      }),
      invalidatesTags: [{ type: 'Comments', id: 'LIST' }, { type: "Comment" }],
    }),
    deleteComment: builder.mutation<string, number>({
      query: (id) => ({
        url: `/comments/delete/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: [{ type: 'Comments', id: 'LIST' }],
      async onQueryStarted(_, { dispatch, queryFulfilled }) {
        await queryFulfilled;
        dispatch(tweetsApi.util.invalidateTags(["Tweet"]));
      },
    }),
  })
});

export const { useGetCommentsQuery, useGetCommentsByTweetIdQuery, useGetCommentsByIdQuery, useCreateCommentMutation, useLikeCommentMutation, useUnLikeCommentMutation, useDeleteCommentMutation } =
commentsApiEndpoints;