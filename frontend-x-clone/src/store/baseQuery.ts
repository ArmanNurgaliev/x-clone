import { BaseQueryApi, FetchArgs, createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { RootState } from "./store";
import { IUserState, logout, setCredentials } from "./auth/auth.slice";
import { QueryReturnValue } from "@reduxjs/toolkit/dist/query/baseQueryTypes";

const baseQuery = fetchBaseQuery({
    baseUrl: "http://localhost:8080/api",
    credentials: 'include',
    prepareHeaders: (headers, { getState, endpoint }) => {
        const token = (getState() as RootState).auth.accessToken
        if (token && endpoint !== "refresh") {
            headers.set("Authorization", `Bearer ${token}`);
        }
        return headers;
    }
})

export const baseQueryWithReauth = async (args: string | FetchArgs, api: BaseQueryApi, extraOptions: {}) => {
    let result = await baseQuery(args, api, extraOptions);

    if (result?.error?.status === 401) {
        console.log("sending refresh token");
        const authData = localStorage.getItem("authData");

        let refreshToken;
        let user;
        if (authData) {
            user = JSON.parse(authData).user;
            refreshToken = JSON.parse(authData).refreshToken;
        }

        const refreshResult  = await baseQuery(
            { url: "/auth/refresh-token", method: 'POST', headers: {authorization : `Bearer ${refreshToken}`} },
            { ...api, endpoint: "refresh" },
            extraOptions,
        ) as QueryReturnValue<RefreshTokenResponse, unknown, unknown>

        if (refreshResult?.data) {
            const newUser: IUserState = {
                user: user,
                accessToken: refreshResult.data.accessToken,
                refreshToken: refreshResult.data.refreshToken
            }

            //store new token
            api.dispatch(setCredentials({...newUser}));
            // retry original query with new access token
            result = await baseQuery(args, api, extraOptions);
        } else {
            api.dispatch(logout());
            window.location.reload();
        }
    } else if (result.error) {
        console.log("error: ", result.error);
    }

    return result;
}

export const apiSlice = createApi({
    baseQuery: baseQueryWithReauth,
    endpoints: () => ({})
})