import axios from "axios";
import AuthService from "../auth/AuthService";
import AuthHeader from "../auth/AuthHeader";

const API_URL = "http://localhost:8080/api/users";

// class UserService {
//   getUserById(user_id: number) {
//     return axios.get(API_URL + `/id/${user_id}`, {
//       headers:  AuthHeader(),
//     });
//   }

//   searchUser(name: string) {
//     return axios.get(API_URL + `/search?q=${name}`, {
//       headers: AuthHeader(),
//     });
//   }
// }

// export default new UserService();
