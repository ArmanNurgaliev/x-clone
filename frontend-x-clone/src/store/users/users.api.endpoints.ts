import { Args } from "../../scenes/users/Users";
import { IUserState, setCredentials } from "../auth/auth.slice";
import { usersApi } from "./users.api";

interface ListResponse {
  first: boolean,
  last: boolean,
  page: number,
  numberOfElements: number,
  size: number,
  total: number,
  totalPages: number,
  content: User[]
}

export const usersApiEndpoints = usersApi.injectEndpoints({
  endpoints: (builder) => ({
    getAuthUser: builder.query<User, void>({
      query: () => ({
        url: "/users/me",
      }),
      providesTags: (result, error, user_id) => [{ type: 'User', user_id }],
    }),
    getAllUsersByName: builder.query<ListResponse, Args>({
      query: (args) => ({
        url: `users/all?page=${args.page}&rowPerPage=${args.rowsPerPage}&name=${args.name}`,
      }),
      providesTags: [{ type: "Users" }]
    }),
    searchUsers: builder.query<User[], string>({
      query: (name) => ({
        url: `/users/search?q=${name}`,
      }),
      providesTags: (result) =>
        result ?
            [
              ...result.map(({ user_id }) => ({ type: 'Users', user_id } as const)),
              { type: 'Users', id: 'LIST' },
            ] :
            [{ type: 'Users', id: 'LIST' }],
    }),
    getUserByLogin: builder.query<User, string>({
      query: (login) => ({
        url: `/users/login/${login}`,
      }),
      providesTags: (result, error, user_id) => [{ type: 'User', user_id }],
    }),
    followUser: builder.mutation<string, number>({
      query: (id) => ({
        url: `/users/follow/${id}`,
        method: "PUT",
      }),
      invalidatesTags: [{ type: "User" }]
    }),
    unFollowUser: builder.mutation<string, number>({
      query: (id) => ({
        url: `/users/unfollow/${id}`,
        method: "PUT",
      }),
      invalidatesTags: [{ type: "User" }]
    }),
    editUser: builder.mutation<User, UserDto>({
      query: (userDto) => ({
        url: "/users/account/edit",
        method: "POST",
        body: userDto,
      }),
      invalidatesTags: [{ type: "User" }],
      async onQueryStarted(_args, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          const authData = localStorage.getItem("authData");

          let refreshToken;
          let accessToken;
          if (authData) {
              refreshToken = JSON.parse(authData).refreshToken;
              accessToken = JSON.parse(authData).accessToken;
          }

          const newUser: IUserState = {
            user: data,
            accessToken: accessToken,
            refreshToken: refreshToken
          }

          localStorage.setItem("authData", JSON.stringify(newUser));
          dispatch(setCredentials({...newUser}));
        } catch (error) {} 
      },
    }),
    deleteUser: builder.mutation<string, number>({
      query: (id) => ({
        url: `/users/delete/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: [{ type: "Users" }]
    }),
    makeAdmin: builder.mutation<string, number>({
      query: (id) => ({
        url: `/users/make-admin/${id}`,
        method: "PUT",
      }),
      invalidatesTags: [{ type: "Users" }, { type: "User" }]
    }),
  }),
});

export const { useSearchUsersQuery, useGetAllUsersByNameQuery, useGetAuthUserQuery, useGetUserByLoginQuery, useFollowUserMutation, 
  useUnFollowUserMutation, useEditUserMutation, useDeleteUserMutation, useMakeAdminMutation } =
  usersApiEndpoints;
