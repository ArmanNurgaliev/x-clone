import React from "react";
import { XMarkIcon } from "@heroicons/react/24/outline";
import "./Auth.css";
import { useNavigate } from "react-router-dom";

const Auth = () => {
  const navigate = useNavigate();

  return (
    <div className="page">
      <div>
        <XMarkIcon className="sign" />
      </div>
      <div>
        <h1>В курсе</h1>
        <h1>происходящего</h1>
        <h3>Присоеденяйтесь сегодня</h3>
        <button
          onClick={() => navigate("/signup")}
          className="my-4 px-8 py-2 w-[250px] rounded-full text-white font-bold bg-blue-500 hover:bg-blue-700"
        >
          Зарегистрироваться
        </button>
        <p className="font-bold mt-4">Уже зарегистрированы?</p>
        <button
          onClick={() => navigate("/login")}
          className="my-4 px-8 py-2 w-[250px] border rounded-full text-blue-500 font-bold bg-white hover:bg-slate-100"
        >
          Войти
        </button>
      </div>
    </div>
  );
};

export default Auth;
