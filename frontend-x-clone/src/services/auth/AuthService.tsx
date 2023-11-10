import axios from "axios";

const API_URL = "http://localhost:8080/api/auth";

class AuthService {
  login(loginDto: LoginDto) {
    return axios
      .post(API_URL + "/login", loginDto, {
        headers: {
          "Content-Type": "application/json",
        },
      })
      .then((response) => {
        if (response && response.data.token !== "") {
          const userAuth = {
            user: response.data.user,
            token: response.data.token,
          };
          localStorage.setItem("userAuth", JSON.stringify(userAuth));
        }

        return response.data;
      });
  }

  logout() {
    localStorage.removeItem("userAuth");
  }

  register(registrationDto: RegistrationDto) {
    return axios
      .post(API_URL + "/register", registrationDto)
      .then((response) => {
        return response;
      });
  }

  getCurrentUser() {
    const userAuth = localStorage.getItem("userAuth");
    if (userAuth) {
      const data = JSON.parse(userAuth);
      return data;
    }

    return null;
  }
}

export default new AuthService();
