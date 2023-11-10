import Sidebar from "../scenes/sidebar/Sidebar";
import Home from "../scenes/home/Home";
import { Routes, Route } from "react-router-dom";
import Explore from "../scenes/explore/Explore";
import Profile from "../scenes/profile/Profile";
import Post from "../scenes/post/Post";
import Auth from "../scenes/auth/Auth";
import Signup from "../component/register/Signup";
import Signin from "../component/register/Signin";
import Search from "../scenes/search/Search";
import Followers from "../scenes/followers/Followers";
import Users from "../scenes/users/Users";

const Router = () => {
  const authData = localStorage.getItem("authData");

  return (
    <div>
      {authData ? (
        <div className="app">
          <div className="sidebar">
            <Sidebar />
          </div>
          <div className="second">
            <div className="mid">
              <Routes>
                <Route path="/home" element={<Home />} />
                <Route path="/explore" element={<Explore />} />
                <Route path="/:login" element={<Profile />} />
                <Route path="/:login/post/:id" element={<Post />} />
                <Route path="/:login/following" element={<Followers />} />
                <Route path="/:login/followers" element={<Followers />} />
                <Route path="/users" element={<Users />} />
                <Route path="/*" element={<Home />} />
              </Routes>
            </div>
            <div className="right">
              <Search />
            </div>
          </div>
        </div>
      ) : (
        <div>
          <Routes>
            <Route path="/" element={<Auth />} />
            <Route path="/signup" element={<Signup />}></Route>
            <Route path="/login" element={<Signin />}></Route>
            <Route path="/*" element={<Auth />} />
          </Routes>
        </div>
      )}
    </div>
  );
};

export default Router;
