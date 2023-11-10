import { XMarkIcon } from "@heroicons/react/24/outline";
import { Box, Button, TextField } from "@mui/material";
import React, { useRef, useState } from "react";
import { MdOutlineAddAPhoto } from "react-icons/md";
import "./EditProfile.css";
import { useEditUserMutation } from "../../store/users/users.api.endpoints";
import Logic from "../../services/config/Logic";

type Props = {
  user: User;
  open: boolean;
  onClose: () => void;
};

const EditProfile: React.FC<Props> = ({ user, open, onClose }) => {
  const updateDateInitial: UserDto = {
    name: user.name,
    bio: user.bio,
    location: user.location,
    website: user.website,
    image: user.image,
    backgroundImage: user.backgroundImage,
  };

  const [updateData, setUpdateData] = useState<UserDto>(updateDateInitial);
  const refImage = useRef<HTMLInputElement>(null);
  const refBackImage = useRef<HTMLInputElement>(null);
  const [editUser] = useEditUserMutation();

  const handleChange = (e: any) => {
    setUpdateData((prev) => ({
      ...updateData,
      [e.target.name]: e.target.value,
    }));
  };

  const handleImageClick = () => {
    refImage.current?.click();
  };

  const handleBackImageClick = () => {
    refBackImage.current?.click();
  };

  const onImageChangeHandler = (e: any) => {
    e.preventDefault();
    Logic.getBase64(e.target.files[0])
      .then((res: any) => {
        setUpdateData(() => ({
          ...updateData,
          image: res,
        }));
      })
      .catch((err) => console.log(err));
  };

  const onBackImageChangeHandler = (e: any) => {
    e.preventDefault();
    Logic.getBase64(e.target.files[0])
      .then((res: any) => {
        setUpdateData(() => ({
          ...updateData,
          backgroundImage: res,
        }));
      })
      .catch((err) => console.log(err));
  };

  const updateUser = async () => {
    await editUser(updateData)
      .unwrap()
      .then(() => {
        onClose();
      });
  };

  return (
    <div>
      <div className="flex items-center justify-between py-2 px-4">
        <div className="flex">
          <div
            className="hover:bg-gray-300 rounded-full p-1 cursor-pointer"
            onClick={onClose}
          >
            <XMarkIcon className="w-6" />
          </div>
          <p className="font-bold text-xl px-8">Edit profile</p>
        </div>

        <Button
          sx={{
            px: 2,
            textTransform: "none",
            fontWeight: "bold",
            color: "white",
            backgroundColor: "black",
            borderRadius: "9999px",
            "&:hover": {
              backgroundColor: "#424242",
            },
          }}
          onClick={updateUser}
        >
          Save
        </Button>
      </div>

      <div>
        <div
          className="w-full h-[200px] items-center flex"
          style={{
            backgroundImage: updateData.backgroundImage
              ? `url(${updateData.backgroundImage})`
              : `url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRg1c5wCvtzGCuFfQ4KZxkTzd46DYsI7KmuEa8WfE8&s")`,
          }}
        >
          <div className="close-sign" onClick={handleBackImageClick}>
            <MdOutlineAddAPhoto className="w-6 h-6 m-auto" />
            <input
              name="file"
              type="file"
              ref={refBackImage}
              className="hidden"
              onChange={onBackImageChangeHandler}
            />
          </div>
        </div>

        <div
          className="image"
          style={{
            backgroundImage: updateData.image
              ? `url(${updateData.image})`
              : `url("https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png")`,
          }}
        >
          <div className="close-sign" onClick={handleImageClick}>
            <MdOutlineAddAPhoto className="w-6 h-6 m-auto" />
            <input
              name="file"
              type="file"
              ref={refImage}
              className="hidden"
              onChange={onImageChangeHandler}
            />
          </div>
        </div>
        <Box
          component="form"
          encType="multipart/form-data"
          noValidate
          sx={{ mx: 2, my: 6 }}
        >
          <TextField
            margin="normal"
            fullWidth
            label="name"
            name="name"
            defaultValue={updateData.name}
            autoFocus
            error={updateData.name?.length === 0}
            helperText={
              updateData.name?.length === 0 ? "Name can't be blank" : ""
            }
            onChange={handleChange}
          />
          <TextField
            margin="normal"
            fullWidth
            label="Bio"
            name="bio"
            defaultValue={updateData.bio}
            autoFocus
            onChange={handleChange}
          />
          <TextField
            margin="normal"
            fullWidth
            name="location"
            label="Location"
            defaultValue={updateData.location}
            type="text"
            onChange={handleChange}
          />
          <TextField
            margin="normal"
            fullWidth
            name="website"
            label="Website"
            defaultValue={updateData.website}
            type="text"
            onChange={handleChange}
          />
        </Box>
      </div>
    </div>
  );
};

export default EditProfile;
