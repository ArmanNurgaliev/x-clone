import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { XMarkIcon } from "@heroicons/react/24/outline";
import { useRegisterMutation } from "../../store/auth/auth.api";
import {
  Avatar,
  Box,
  Button,
  CssBaseline,
  Grid,
  Link,
  Paper,
  Snackbar,
  TextField,
  ThemeProvider,
  Typography,
  createTheme,
} from "@mui/material";
import { useToast } from "@chakra-ui/react";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";

const Signup = () => {
  const regUser = {
    name: "",
    login: "",
    email: "",
    password: "",
    dob: new Date(),
  };

  const [registrationDto, setRegistrationDto] =
    useState<RegistrationDto>(regUser);
  const [errors, setErrors] = useState<any>();
  const navigate = useNavigate();
  const toast = useToast();
  const [register] = useRegisterMutation();

  const handleChange = (e: any) => {
    setRegistrationDto((prev) => ({
      ...registrationDto,
      [e.target.name]: e.target.value,
    }));
  };

  const handleDateChange = (e: any) => {
    setRegistrationDto((prev) => ({
      ...registrationDto,
      dob: e,
    }));
  };

  const registerUser = async (e: any) => {
    e.preventDefault();
    await register(registrationDto)
      .unwrap()
      .then((payload: any) => {
        toast({
          title: payload.message,
          status: "success",
          duration: 3000,
          isClosable: true,
        });
        navigate("/login");
      })
      .catch((error: any) => {
        console.log("error: ", error);
        if (error.status === 401) {
          navigate("/login");
        } else {
          setErrors(error.data);
        }
      });
  };

  return (
    <ThemeProvider theme={createTheme()}>
      <Grid
        container
        component="main"
        sx={{ height: "100vh" }}
        alignSelf={"center"}
      >
        <CssBaseline />
        <Grid item xs={false} sm={4} md={7}>
          <XMarkIcon className="left-side" />
        </Grid>
        <Grid item xs={12} sm={8} md={5} component={Paper} elevation={6} square>
          <Box
            sx={{
              mx: 4,
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            <Avatar sx={{ m: 1, color: "black", bgcolor: "white" }}>
              <XMarkIcon className="mark" />
            </Avatar>
            <Typography component="h1" variant="h5">
              Sign in
            </Typography>
            <Box
              component="form"
              noValidate
              onSubmit={registerUser}
              sx={{ mt: 1 }}
            >
              <TextField
                margin="normal"
                required
                fullWidth
                id="name"
                label="name"
                name="name"
                autoComplete="name"
                autoFocus
                onChange={handleChange}
              />
              {errors && errors.name && (
                <p className="text-red-500">{errors.name}</p>
              )}
              <TextField
                margin="normal"
                required
                fullWidth
                id="login"
                label="login"
                name="login"
                autoComplete="login"
                autoFocus
                onChange={handleChange}
              />
              {errors && errors.login && (
                <p className="text-red-500">{errors.login}</p>
              )}
              <TextField
                margin="normal"
                required
                fullWidth
                id="email"
                label="email"
                name="email"
                autoComplete="email"
                autoFocus
                onChange={handleChange}
              />
              {errors && errors.email && (
                <p className="text-red-500">{errors.email}</p>
              )}
              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DemoContainer components={["DatePicker"]}>
                  <DatePicker
                    className="w-[100%]"
                    label="date of birth"
                    autoFocus
                    onChange={(e) => handleDateChange(e)}
                  />
                </DemoContainer>
              </LocalizationProvider>
              {errors && errors.dob && (
                <p className="text-red-500">{errors.dob}</p>
              )}
              <TextField
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                autoComplete="current-password"
                onChange={handleChange}
              />
              {errors && errors.password && (
                <p className="text-red-500">{errors.password}</p>
              )}
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
              >
                Sign up
              </Button>
              <Grid item container>
                <Link href="#" variant="body2">
                  {"Already have an account?"}
                </Link>
                <Button
                  type="submit"
                  fullWidth
                  variant="outlined"
                  sx={{ mt: 3, mb: 2 }}
                  onClick={() => navigate("/login")}
                >
                  Sign in
                </Button>
              </Grid>
            </Box>
          </Box>
        </Grid>
      </Grid>
    </ThemeProvider>
  );
};

export default Signup;
