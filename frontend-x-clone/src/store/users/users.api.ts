import { createApi } from "@reduxjs/toolkit/query/react";
import { baseQueryWithReauth } from "../baseQuery";

export const usersApi = createApi({
  reducerPath: "usersApi",
  tagTypes: ["Users", "User"],
  baseQuery: baseQueryWithReauth,
  endpoints: () => ({}),

});
