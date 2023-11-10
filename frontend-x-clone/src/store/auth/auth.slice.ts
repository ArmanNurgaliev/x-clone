import { PayloadAction, createSlice } from "@reduxjs/toolkit";

export interface IUserState {
    user: User | null;
    accessToken: string | null,
    refreshToken: string | null
  }
  
  const initialState: IUserState = {
    user: null,
    accessToken: null,
    refreshToken: null
  };


export const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setCredentials: (
            state,
            { payload: { user, accessToken, refreshToken } }: PayloadAction<IUserState>
          ) => {
            localStorage.setItem("authData", JSON.stringify({
              user: user,
              accessToken: accessToken,
              refreshToken: refreshToken
            }));
            state.user = user
            state.accessToken = accessToken
            state.refreshToken = refreshToken
          },
        logout: (state) => {
          state = initialState;
          localStorage.removeItem("authData");
        }
    },
})

export default authSlice.reducer;

export const { setCredentials, logout } = authSlice.actions;

export const selectCurrentUser = (state: { auth: { user: any; }; }) => state.auth.user
export const selectCurrentAccessToken = (state: { auth: { accessToken: any; }; }) => state.auth.accessToken
export const selectCurrentRefreshToken = (state: { auth: { refreshToken: any; }; }) => state.auth.refreshToken
