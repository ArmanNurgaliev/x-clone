import { Alert, Button, Popover, Snackbar, Typography } from "@mui/material";
import React, { useState } from "react";
import { BsThreeDots } from "react-icons/bs";
import Logic from "../../services/config/Logic";
import {
  useDeleteUserMutation,
  useMakeAdminMutation,
} from "../../store/users/users.api.endpoints";

type Props = {
  user: User;
};

const Action: React.FC<Props> = ({ user }) => {
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);
  const [openSnack, setOpenSnack] = useState<boolean>(false);
  const [message, setMessage] = useState<string>("");
  const [deleteUser] = useDeleteUserMutation();
  const [makeAdmin] = useMakeAdminMutation();

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    setAnchorEl(event.currentTarget);
  };

  const handleClose = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    setAnchorEl(null);
  };

  const handleDelete = async (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>
  ) => {
    e.stopPropagation();
    console.log("DELETE");
    await deleteUser(user.user_id)
      .unwrap()
      .then((response) => {
        const data = JSON.stringify(response);
        setMessage(JSON.parse(data).message);
        setOpenSnack(true);
      })
      .catch((error) => {
        if (error.status === 403) {
          console.log("You are not admin");
        }
      });
  };

  const handleMakeAdmin = async (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>
  ) => {
    e.stopPropagation();
    await makeAdmin(user.user_id)
      .unwrap()
      .then((response) => {
        const data = JSON.stringify(response);
        setMessage(JSON.parse(data).message);
        setOpenSnack(true);
      });
  };

  const open = Boolean(anchorEl);
  const id = open ? "simple-popover" : undefined;

  return (
    <>
      <Button
        aria-describedby={id}
        onClick={handleClick}
        sx={{
          display: "flex",
          minWidth: 0,
          p: 1,
          textTransform: "none",
          alignItems: "center",
          justifyContent: "space-between",
          "&:hover": {
            backgroundColor: "lightgrey",
          },
          borderRadius: "9999px",
        }}
      >
        <BsThreeDots />
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
        <Typography
          sx={{
            p: 1,
            cursor: "pointer",
            color: "red",
            "&:hover": {
              backgroundColor: "#eeeeee",
            },
          }}
          onClick={handleDelete}
        >
          Delete
        </Typography>
        <Typography
          sx={{
            p: 1,
            cursor: "pointer",
            color: "black",
            "&:hover": {
              backgroundColor: "#eeeeee",
            },
          }}
          onClick={handleMakeAdmin}
        >
          Make admin
        </Typography>
      </Popover>
      <Snackbar
        open={openSnack}
        autoHideDuration={3000}
        onClose={() => setOpenSnack(false)}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          onClose={() => setOpenSnack(false)}
          severity="success"
          sx={{ width: "100%" }}
        >
          {message}
        </Alert>
      </Snackbar>
    </>
  );
};

export default Action;
