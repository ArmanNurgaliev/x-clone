import { BiHomeCircle, BiSolidHomeCircle } from "react-icons/bi";
import { GoSearch } from "react-icons/go";
import { IoIosNotificationsOutline, IoIosNotifications } from "react-icons/io";
import { FaRegEnvelope, FaEnvelope } from "react-icons/fa";
import { RiFileList2Line, RiFileList2Fill } from "react-icons/ri";
import { BsPeople, BsPeopleFill } from "react-icons/bs";
import { MdPersonOutline, MdPerson } from "react-icons/md";
import { CiCircleMore } from "react-icons/ci";
import { ImSearch } from "react-icons/im";
import { RxCross1, RxCross2 } from "react-icons/rx";
import { HiOutlineUsers, HiUsers } from "react-icons/hi";

import { ReactNode } from "react";

export let menu: {
  id: number;
  title: string;
  icon: ReactNode;
  activeIcon: ReactNode;
}[] = [
  {
    id: 0,
    title: "Home",
    icon: <BiHomeCircle className="icon" />,
    activeIcon: <BiSolidHomeCircle className="icon" />,
  },
  {
    id: 1,
    title: "Explore",
    icon: <GoSearch className="icon" />,
    activeIcon: <ImSearch className="icon" />,
  },
  {
    id: 2,
    title: "Notifications",
    icon: <IoIosNotificationsOutline className="icon" />,
    activeIcon: <IoIosNotifications className="icon" />,
  },
  {
    id: 3,
    title: "Messages",
    icon: <FaRegEnvelope className="icon" />,
    activeIcon: <FaEnvelope className="icon" />,
  },
  {
    id: 4,
    title: "Lists",
    icon: <RiFileList2Line className="icon" />,
    activeIcon: <RiFileList2Fill className="icon" />,
  },
  {
    id: 5,
    title: "Communities",
    icon: <BsPeople className="icon" />,
    activeIcon: <BsPeopleFill className="icon" />,
  },
  {
    id: 6,
    title: "Verified",
    icon: <RxCross2 className="w-8 h-8" />,
    activeIcon: <RxCross1 className="w-8 h-8 font-bold" />,
  },
  {
    id: 7,
    title: "Profile",
    icon: <MdPersonOutline className="icon" />,
    activeIcon: <MdPerson className="icon" />,
  },
  {
    id: 8,
    title: "More",
    icon: <CiCircleMore className="icon" />,
    activeIcon: <CiCircleMore className="icon" />,
  },
  {
    id: 9,
    title: "Users",
    icon: <HiOutlineUsers className="icon" />,
    activeIcon: <HiUsers className="icon" />,
  },
];
