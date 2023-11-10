import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useSearchUsersQuery } from "../../store/users/users.api.endpoints";

const Search = () => {
  const [username, setUsername] = useState<string>("");
  const navigate = useNavigate();
  const { data: users } = useSearchUsersQuery(username);

  const handleChange = (e: any) => {
    setUsername(e.target.value);
  };

  return (
    <div className="w-full text-center my-6">
      <input
        type="text"
        onChange={handleChange}
        name="username"
        placeholder="Search"
        className="w-[80%] p-2 pl-5 my-2 first-line:border rounded-full bg-gray-200 focus:bg-white"
      />
      <div className="ml-[35px]">
        {users?.map((item) => {
          return (
            <div key={item.user_id} className="flex items-center">
              <img
                onClick={() => navigate(`/${item.login}`)}
                className="w-10 h-10 rounded-full cursor-pointer"
                src={`${
                  item.image
                    ? `data:image/jpeg;base64,${item.image}`
                    : "https://simg.nicepng.com/png/small/933-9332131_profile-picture-default-png.png"
                }`}
                alt=""
              />
              <div className="flex w-full">
                <div className="text-left pl-2">
                  <p
                    className="font-bold cursor-pointer"
                    onClick={() => navigate(`/${item.login}`)}
                  >
                    {item.name}
                  </p>
                  <p
                    className="text-gray-400 cursor-pointer"
                    onClick={() => navigate(`/${item.login}`)}
                  >
                    @{item.login}
                  </p>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default Search;
