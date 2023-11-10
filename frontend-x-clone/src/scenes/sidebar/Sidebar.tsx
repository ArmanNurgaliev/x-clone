import { useState } from "react";
import { menu } from "./SidebarConfig";
import { XMarkIcon } from "@heroicons/react/24/outline";
import { GiFeather } from "react-icons/gi";
import "./Sidebar.css";
import { UserMenu } from "../../component/sidebar/UserMenu";

import { useNavigate } from "react-router-dom";
import { Box, Button, Modal } from "@mui/material";
import PostForm from "../../component/home/PostForm";
import { useGetAuthUserQuery } from "../../store/users/users.api.endpoints";
import Logic from "../../services/config/Logic";

const style = {
  position: "absolute" as "absolute",
  top: "20%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 600,
  bgcolor: "background.paper",
  // border: "2px solid #000",
  borderRadius: "10px",
  boxShadow: 24,
  p: 4,
};

const Sidebar = () => {
  const [activeTab, setActiveTab] = useState<string>("Home");
  const [open, setOpen] = useState<boolean>(false);
  const navigate = useNavigate();
  const { data: user } = useGetAuthUserQuery();

  const handleTabClick = (title: string) => {
    setActiveTab(title);
    if (title === "Home") {
      navigate("/home");
    } else if (title === "Explore") {
      navigate("/explore");
    } else if (title === "Profile") {
      if (user) {
        navigate(`/${user.login}`);
      }
    } else if (title === "Users") {
      navigate("/users");
    }
  };

  const handleClose = () => setOpen(false);

  return (
    <div className="nav">
      <div
        className="m-auto inline-block hover:bg-gray-300 rounded-full"
        onClick={() => handleTabClick("Home")}
      >
        <XMarkIcon className="h-10 w-10" />
      </div>

      {menu.map((item) => (
        <ul className="m-auto" key={item.id}>
          <li
            onClick={() => handleTabClick(item.title)}
            className={
              user && !Logic.isAdmin(user) && item.id === 9
                ? "hidden"
                : `my-2 flex items-center justify-start w-fit cursor-pointer text-xl hover:bg-gray-300 rounded-full p-2`
            }
          >
            {activeTab === item.title ? item.activeIcon : item.icon}
            <p
              className={
                activeTab === item.title
                  ? "font-bold small-screen"
                  : "small-screen"
              }
            >
              {item.title}
            </p>
          </li>
        </ul>
      ))}

      <div className="small-screen">
        <Button
          sx={{
            mx: "auto",
            width: "100%",
            backgroundColor: "rgb(59, 130, 246)",
            color: "white",
            borderRadius: "9999px",
            "&:hover": {
              backgroundColor: "rgb(29, 78, 216)",
            },
          }}
          onClick={() => setOpen(true)}
        >
          POST
        </Button>
      </div>
      <div className="big-screen">
        <Button
          sx={{
            mx: "auto",
            minWidth: "auto",
            backgroundColor: "rgb(59, 130, 246)",
            color: "white",
            borderRadius: "9999px",
            "&:hover": {
              backgroundColor: "rgb(29, 78, 216)",
            },
          }}
          className="post"
          onClick={() => setOpen(true)}
        >
          <GiFeather className="text-xl" />
        </Button>
      </div>

      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <PostForm />
        </Box>
      </Modal>
      {user && <UserMenu user={user} />}
    </div>
  );
};

export default Sidebar;
