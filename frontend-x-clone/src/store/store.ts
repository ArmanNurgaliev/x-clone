import { configureStore } from "@reduxjs/toolkit";
import { tweetsApi } from "./tweets/tweets.api";
import { authApi } from "./auth/auth.api";
import { authSlice } from "./auth/auth.slice";
import { commentsApi } from "./comments/comment.api";
import { usersApi } from "./users/users.api";



export const store = configureStore({
  reducer: {
     auth: authSlice.reducer,
    [authApi.reducerPath]: authApi.reducer,
    [tweetsApi.reducerPath]: tweetsApi.reducer,
    [commentsApi.reducerPath]: commentsApi.reducer,
    [usersApi.reducerPath]: usersApi.reducer
  },

  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(authApi.middleware).concat(tweetsApi.middleware)
    .concat(commentsApi.middleware).concat(usersApi.middleware),
});

export type RootState = ReturnType<typeof store.getState>;

export type AppDispatch = typeof store.dispatch