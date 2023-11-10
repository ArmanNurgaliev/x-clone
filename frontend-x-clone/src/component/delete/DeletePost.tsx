import { Button, Popover, Typography } from "@mui/material";
import React from "react";
import { BsThreeDots } from "react-icons/bs";
import { useState } from "react";

type Props = {
  handleDelete: () => void;
};

const DeletePost: React.FC<Props> = ({ handleDelete }) => {
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
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
      </Popover>
    </>
  );
};

export default DeletePost;
