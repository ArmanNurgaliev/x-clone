import React from "react";
import { BiArrowBack } from "react-icons/bi";
import { useNavigate } from "react-router-dom";
import Search from "../search/Search";

const Explore = () => {
  const navigate = useNavigate();

  return (
    <div className="h-[100vh] border border-r-gray">
      <div className="flex items-center ml-2">
        <div
          className="px-2 py-2 rounded-full hover:bg-gray-200"
          onClick={() => navigate(-1)}
        >
          <BiArrowBack className="w-6 h-6" />
        </div>
        <div className="px-8">
          <p className="text-xl font-bold">Explore</p>
        </div>
      </div>
      <Search />
    </div>
  );
};

export default Explore;
