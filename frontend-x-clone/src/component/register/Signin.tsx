import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { XMarkIcon } from "@heroicons/react/24/outline";
import "../../scenes/auth/Auth.css";
import { UserResponse, useLoginMutation } from "../../store/auth/auth.api";
import {
  Avatar,
  Box,
  Button,
  Checkbox,
  CssBaseline,
  FormControlLabel,
  Grid,
  Link,
  Paper,
  TextField,
  ThemeProvider,
  Typography,
  createTheme,
} from "@mui/material";

const Signin = () => {
  const loginForm = {
    name: "",
    password: "",
  };

  const [loginDto, setLoginDto] = useState<LoginDto>(loginForm);
  const [loginError, setLoginError] = useState<string>();
  const navigate = useNavigate();
  const [login] = useLoginMutation();

  const handleChange = (e: any) => {
    setLoginDto((prev) => ({
      ...loginDto,
      [e.target.name]: e.target.value,
    }));
  };

  const loginUser = (e: any) => {
    e.preventDefault();
    login(loginDto)
      .unwrap()
      .then((payload: UserResponse) => {
        navigate("/home");
        window.location.reload();
      })
      .catch((error: any) => {
        console.log("Error: ", error);
        setLoginError(error.data.message);
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
              onSubmit={loginUser}
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
              {loginError && <p className="text-red-500">{loginError}</p>}
              <Grid item xs my={"auto"}>
                <FormControlLabel
                  control={<Checkbox value="remember" color="primary" />}
                  label="Remember me"
                />

                <Link href="#" variant="body2">
                  Forgot password?
                </Link>
              </Grid>
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
              >
                Sign In
              </Button>
              <Grid item container>
                <Link href="#" variant="body2">
                  {"Don't have an account?"}
                </Link>
                <Button
                  type="submit"
                  fullWidth
                  variant="outlined"
                  sx={{ mt: 3, mb: 2 }}
                  onClick={() => navigate("/signup")}
                >
                  Sign up
                </Button>
              </Grid>
            </Box>
          </Box>
        </Grid>
      </Grid>
    </ThemeProvider>
  );
};

export default Signin;
