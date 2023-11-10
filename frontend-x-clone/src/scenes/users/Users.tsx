import React, { useState } from "react";
import { styled } from "@mui/material/styles";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import {
  Paper,
  Table,
  TableBody,
  TableContainer,
  TableFooter,
  TableHead,
  TablePagination,
  TableRow,
  TextField,
} from "@mui/material";
import TablePaginationActions from "@mui/material/TablePagination/TablePaginationActions";
import { BiArrowBack } from "react-icons/bi";
import {
  useGetAllUsersByNameQuery,
  useGetAuthUserQuery,
} from "../../store/users/users.api.endpoints";
import Logic from "../../services/config/Logic";
import { useNavigate } from "react-router-dom";
import Action from "../../component/admin/Action";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: theme.palette.common.black,
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
  },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  "&:nth-of-type(odd)": {
    backgroundColor: theme.palette.action.hover,
  },
  // hide last border
  "&:last-child td, &:last-child th": {
    border: 0,
  },
}));

export type Args = {
  page: number;
  rowsPerPage: number;
  name: string;
};

const Users = () => {
  const navigate = useNavigate();
  const [page, setPage] = useState<number>(0);
  const [rowsPerPage, setRowsPerPage] = useState<number>(5);
  const [searchName, setSearchName] = useState<string>("");
  const [args, setArgs] = useState<Args>({
    page: page,
    rowsPerPage: rowsPerPage,
    name: searchName,
  });
  const {
    data: users,
    isLoading,
    isFetching,
  } = useGetAllUsersByNameQuery(args!);
  const { data: user } = useGetAuthUserQuery();

  const handleChangePage = (
    event: React.MouseEvent<HTMLButtonElement> | null,
    newPage: number
  ) => {
    setPage(newPage);
    setArgs(() => ({
      ...args,
      page: page,
    }));
  };

  const handleChangeRowsPerPage = (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
    setArgs({ page: page, rowsPerPage: rowsPerPage, name: searchName });
  };

  const handleSearchInput = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    setSearchName(e.target.value);
    setArgs(() => ({
      ...args,
      name: e.target.value,
    }));
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="justify-center w-full items-center">
      <div className="flex items-center ml-2">
        <div
          className="px-2 py-2 rounded-full hover:bg-gray-200"
          onClick={() => navigate(-1)}
        >
          <BiArrowBack className="w-6 h-6" />
        </div>
        <div className="px-8">
          <p className="text-xl font-bold">Admin page</p>
        </div>
      </div>
      <TextField
        sx={{ width: "100%" }}
        label="User"
        variant="filled"
        name="login"
        onChange={handleSearchInput}
      />

      <TableContainer component={Paper}>
        <Table aria-label="customized table">
          <TableHead>
            <TableRow>
              <StyledTableCell>Name</StyledTableCell>
              <StyledTableCell>Email</StyledTableCell>
              <StyledTableCell>Age</StyledTableCell>
              <StyledTableCell>Roles</StyledTableCell>
              <StyledTableCell>Action</StyledTableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users?.content.map((row) => (
              <TableRow
                sx={{
                  cursor: "pointer",
                  "&:hover": {
                    backgroundColor: "lightgrey",
                  },
                }}
                key={row.user_id}
                onClick={() => navigate(`/${row.login}`)}
              >
                <StyledTableCell component="th" scope="row">
                  {row.name}
                </StyledTableCell>
                <StyledTableCell>{row.email}</StyledTableCell>
                <StyledTableCell>{Logic.calculateAge(row.dob)}</StyledTableCell>
                <StyledTableCell>
                  {row.roles.map((role, i, arr) => (
                    <span key={role.id}>
                      {role.name}
                      {i !== arr.length - 1 ? ", " : ""}
                    </span>
                  ))}
                </StyledTableCell>
                <TableCell>
                  {user && user.user_id !== row.user_id && !Logic.isAdmin(row) && (
                    <Action key={row.user_id} user={row} />
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
          <TableFooter>
            <TableRow>
              <TablePagination
                rowsPerPageOptions={[5, 10, 25, { label: "All", value: -1 }]}
                colSpan={5}
                count={users?.numberOfElements!}
                rowsPerPage={rowsPerPage}
                page={page}
                SelectProps={{
                  inputProps: {
                    "aria-label": "Rows per page",
                  },
                  native: true,
                }}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
                ActionsComponent={TablePaginationActions}
              />
            </TableRow>
          </TableFooter>
        </Table>
      </TableContainer>
    </div>
  );
};

export default Users;
