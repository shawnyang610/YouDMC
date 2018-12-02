var userCookie = {authToken:"", uid:"", uname:"", upic:""}; //contains

function setSessionStorage(loginResponse) {
  console.log(loginResponse);
  sessionStorage.clear();
  sessionStorage.setItem("authToken",loginResponse.access_token);
  sessionStorage.setItem("uid",loginResponse.id);
  sessionStorage.setItem("uname",loginResponse.username);
  sessionStorage.setItem("upic",loginResponse.profile_img);
  sessionStorage.setItem("email",loginResponse.email);
  sessionStorage.setItem("regDate",loginResponse.reg_date);
  getSessionStorage();
}

function getSessionStorage() {
  userCookie.authToken = sessionStorage.authToken || "";
  userCookie.uid = sessionStorage.uid || "";
  userCookie.uname = sessionStorage.uname || "";
  userCookie.upic = sessionStorage.upic || "";
}

function resetSession() {
  sessionStorage.clear();
  getSessionStorage();
}
