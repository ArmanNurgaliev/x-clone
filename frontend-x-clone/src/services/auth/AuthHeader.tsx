export default function AuthHeader() {
  const userAuth = localStorage.getItem("userAuth");

  if (userAuth) {
    const data = JSON.parse(userAuth);
    if (data.token) {
      return "Bearer " + data.token;
    }
  } else {
    return;
  }
}
