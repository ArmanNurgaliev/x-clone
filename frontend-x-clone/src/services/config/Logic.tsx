import format from "date-fns/format";

class Logic {
  timeDifference = (createdAt: Date) => {
    const date = new Date(createdAt);
    const diff = Date.now() - date.getTime();

    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    const years = Math.floor(days / (7 * 4 * 12));

    if (years > 0) return format(date, "d MMM, yyyy");
    else if (days > 0) return format(date, "d MMM");
    else if (hours > 0) return hours + "h";
    else if (minutes > 0) return minutes + "m";
    else if (seconds > 0) return seconds + "s";
  };

  formatDate = (createdAt: Date) => {
    const date = new Date(createdAt);
    const formated = format(date, "HH:MM MMMM do, yyyy");
    return formated;
  };

  getBase64 = (file: any) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        resolve(reader.result);
      };
      reader.onerror = reject;
    });
  };

  isAdmin = (user: User) => {
    return user.roles.some((r: Role) => r.name === "ADMIN");
  };

  calculateAge = (dob: Date) => {
    const currDate = new Date();
    const msDiff = new Date().getTime() - new Date(dob).getTime();
    const years = Math.floor(msDiff / (1000 * 60 * 60 * 24 * 7 * 4 * 12));
    return years;
  };
}

export default new Logic();
