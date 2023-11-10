// import axios from "axios";
// import AuthHeader from "../auth/AuthHeader";

// const API_URL = "http://localhost:8080/api/tweets";

// class TweetService {
//   getAuthUserTweets() {
//     return axios.get(API_URL, { headers: AuthHeader() }).then((response) => {
//       return response;
//     });
//   }

//   getUserTweets(user_id: number) {
//     return axios
//       .get(API_URL + `/user/${user_id}`, {
//         headers: AuthHeader(),
//       })
//       .then((response) => {
//         return response.data;
//       });
//   }

//   getTweetById(tweet_id: number) {
//     return axios
//       .get(API_URL + `/id/${tweet_id}`, {
//         headers: AuthHeader(),
//       })
//       .then((response) => {
//         return response.data;
//       });
//   }

//   createTweet(tweetDto: FormData) {
//     const userAuth = localStorage.getItem("userAuth");
//     const data = JSON.parse(userAuth!);
//     return axios.post(API_URL + "/create", tweetDto, {
//       headers: {
//         Authorization: "Bearer " + data.token,
//         "Content-type": "multipart/form-data",
//       },
//     });
//   }

//   likeTweet(tweet_id: number) {
//     return axios.put(API_URL + `/like/${tweet_id}`, null, {
//       headers: AuthHeader(),
//     });
//   }

//   unLikeTweet(tweet_id: number) {
//     return axios.put(API_URL + `/unlike/${tweet_id}`, null, {
//       headers: AuthHeader(),
//     });
//   }

//   repostTweet(tweet_id: number) {
//     return axios.put(API_URL + `/repost/${tweet_id}`, null, {
//       headers: AuthHeader(),
//     });
//   }

//   undoRepostTweet(tweet_id: number) {
//     return axios.put(API_URL + `/undoRepost/${tweet_id}`, null, {
//       headers: AuthHeader(),
//     });
//   }

//   deleteTweet(tweet_id: number) {
//     return axios.delete(API_URL + `/delete/${tweet_id}`, {
//       headers: AuthHeader(),
//     });
//   }
// }
// export default new TweetService();
