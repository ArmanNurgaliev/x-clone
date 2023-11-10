// import axios from "axios";
// import AuthHeader from "../auth/AuthHeader";

// const API_URL = "http://localhost:8080/api/comments";

// class CommentService {
//   getTweetComments(tweet_id: number) {
//     return axios
//       .get(API_URL + `/tweet/${tweet_id}`, { headers: AuthHeader() })
//       .then((response) => {
//         return response;
//       });
//   }

//   createComment(formData: FormData, tweet_id: number) {
//     return axios
//       .post(API_URL + `/create/${tweet_id}`, formData, { headers: AuthHeader() })
//       .then((response) => {
//         return response;
//       });
//   }
// }

// export default new CommentService();
