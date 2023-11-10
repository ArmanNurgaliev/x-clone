import React, { useState } from "react";

type Props = {
  menu: {
    id: number;
    label: string;
    pressed: boolean;
  }[]
};

const MenuLine: React.FC<Props> = ({ menu }) => {
  const [position, setPosition] = useState(menu);

  const handleClick = (id: number) => {
    const newState = position.map((obj) => {
      if (obj.id === id) {
        return { ...obj, pressed: true };
      } else {
        return { ...obj, pressed: false };
      }
    });
    setPosition(newState);
  };

  return (
    <>
      {position.map((item) => {
        return (
          <div
            key={item.id}
            onClick={() => handleClick(item.id)}
            className="w-[100%] cursor-pointer hover:bg-gray-300"
          >
            <span
              className={`font-bold table mx-auto py-2 ${
                item.pressed
                  ? "text-black border-b-4 border-blue-500"
                  : "text-gray-500"
              }`}
            >
              {item.label}
            </span>
          </div>
        );
      })}
    </>
  );
};

export default MenuLine;
