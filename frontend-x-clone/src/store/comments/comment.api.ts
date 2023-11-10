import { createApi } from "@reduxjs/toolkit/query/react";
import { baseQueryWithReauth } from "../baseQuery";

export const commentsApi = createApi({
  reducerPath: "commentsApi",
  tagTypes: ["Comment", "Comments"],
  baseQuery: baseQueryWithReauth,
  endpoints: () => ({}),

});
