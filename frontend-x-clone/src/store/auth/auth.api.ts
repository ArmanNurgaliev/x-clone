import { setCredentials } from "./auth.slice";
import { apiSlice } from "../baseQuery";

export interface UserResponse {
    user: User
    accessToken: string
    refreshToken: string
}
  
export const authApi = apiSlice.injectEndpoints({
    endpoints: (builder) => ({
        login: builder.mutation<UserResponse, LoginDto>({
            query: (loginDto) => ({
                url: '/auth/login',
                method: 'POST',
                body: loginDto,
            }),
            async onQueryStarted(_args, { dispatch, queryFulfilled }) {
                try {
                    const { data } = await queryFulfilled;
                    dispatch(setCredentials(data));
                } catch (error) {} 
            }
        }),
        register: builder.mutation<string, RegistrationDto>({
            query: (registrationDto) => ({
                url: '/auth/register',
                method: 'POST',
                body: registrationDto,
            })
        }) 
    })
})

export const { useLoginMutation, useRegisterMutation } = authApi