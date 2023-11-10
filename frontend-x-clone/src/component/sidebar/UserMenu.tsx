import { BsThreeDots } from "react-icons/bs";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { logout } from "../../store/auth/auth.slice";
import { Box, Button, Modal, Popover, Typography } from "@mui/material";
import { useState } from "react";

type Props = {
  user: User;
};

export const UserMenu: React.FC<Props> = ({ user }) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);
  const id = open ? "simple-popover" : undefined;

  const handleLogout = () => {
    dispatch(logout());
    navigate("/login");
    window.location.reload();
  };

  return (
    <>
      <Button
        aria-describedby={id}
        onClick={handleClick}
        sx={{ display: "flex",
        width: "100%",
        textTransform: "none",
        alignItems: "center",
        justifyContent: "space-between",
        // backgroundColor: "b",
        "&:hover": {
          backgroundColor: "lightgrey",
        },
        mt: 4,
        px: 2,
        borderRadius: "9999px",


      }}
      >
        <div className="flex">
          <img
            className="w-10 h-10 rounded-full m-auto"
            src="https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png"
            alt=""
          />
          <div className="pl-2 small-screen">
            <p className="font-bold text-black">{user.name}</p>
            <p className="text-grey">{user.login}</p>
          </div>
        </div>
        <BsThreeDots className="small-screen" />
      </Button>
      <Popover
        id={id}
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: "top",
          horizontal: "center",
        }} 
      >
        <Typography sx={{
          p: 1,
          borderRadius: "10%",
          cursor: "pointer",
          fontWeight: 'bold',
          "&:hover": {
            backgroundColor: "#eeeeee",
          },
          }} onClick={handleLogout}>Log out {user.login}</Typography>
      </Popover>
    </>
  );
};
export default UserMenu;
