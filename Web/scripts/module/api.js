function API_getServerTime(callback, innerCallBack) {
  var request = new XMLHttpRequest();
  request.open('GET', 'https://youcmt.com/api/datetime', true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var responseTime = JSON.parse(this.response).datetime;
      console.log("set current time = " + responseTime);
      currentTime = responseTime;
      callback(innerCallBack); //guarantees time is loaded
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.send();
}

function API_getVideoInfo(callback) {
  var request = new XMLHttpRequest();
  request.open('GET', 'https://youcmt.com/api/video/info?vid=' + videoID, true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      console.log("Fetched Video Info");
      videoInfo = JSON.parse(this.response);
      callback(); //ready to display video info
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.send();
}

function API_getRootComments(callback) { //when page is first loaded, get all root comments and render them
  var url = "https://youcmt.com/api/comment/get/comments?vid=" + videoID;
  var request = new XMLHttpRequest();
  request.open('GET', url, true);
  if (userCookie.authToken != "") {
    request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  }
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var serverResponse = JSON.parse(this.response).comments;
      callback(serverResponse);
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send();
}

function API_getReplyComments(rootCommentID, callback) {
  var url = "https://youcmt.com/api/comment/get/comments?top_comment_id=" + rootCommentID;
  var request = new XMLHttpRequest();
  request.open('GET', url, true);
  if (userCookie.authToken != "") {
    request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  }
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var serverResponse = JSON.parse(this.response).comments;
      console.log("Loaded " + serverResponse.length + " reply comment(s) for replyComment " + rootCommentID);
      callback(rootCommentID, serverResponse);
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send();
}

function API_registerUser(callback) {
  var url = "https://youcmt.com/api/user/register";
  var userNameInput = document.getElementById("userNameInput").value;
  var passwordInput = document.getElementById("passwordInput").value;
  var emailInput = document.getElementById("emailInput").value;

  var data = new FormData(); //body
  data.append('username', userNameInput);
  data.append('password', passwordInput);
  data.append('email', emailInput);
  console.log(data.username);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 201) { //success
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse);
    } else if (request.status >= 400) { //bad request
      var serverResponse = JSON.parse(this.response).message;
      callback(serverResponse);
    } else { //server crash/error
      var serverResponse = "Unknown Error";
      callback(serverResponse);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_logInUser(userNameInput, passwordInput, callback) {
  var url = "https://youcmt.com/api/user/login";
  var data = new FormData(); //body
  if (userNameInput.includes("@")) {
    data.append('email', userNameInput);
  } else {
    data.append('username', userNameInput);
  }
  data.append('password', passwordInput);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //login success
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //this will pass a bunch of info like
      /**
       "message": "Succesfully logged in",
       "role": "USER",
       "id": 4,
       "username": "user7007",
       "email": "email@mail.com",
       "profile_img": "0",
       "reg_date": "2018-11-12 20:50:32.869619",
       "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
       "refresh_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
       */
    } else { //server crash/error
      var serverResponse = "Invalid Username / Password";
      callback(serverResponse);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_logOutUser(callback) {
  var url = "https://youcmt.com/api/user/logout_access";
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status >= 200 && request.status < 400) {
      var serverResponse = JSON.parse(this.response); //success 200: "message": "Access Token has been revoked."
      callback(serverResponse.message);
    } else if (request.status > 400) { //bad request
      var serverResponse = JSON.parse(this.response); //fail 422: "msg": "Invalid header string: 'utf-8' codec can't decode byte 0xc3 in position 0: invalid continuation byte"
      callback();
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send();
}

function API_postGuestComment(parentID, text, callback) {
  var url = "https://youcmt.com/api/comment/post/guest";
  var data = new FormData(); //body
  if (parentID == videoID) {
    data.append("vid", parentID);
  } else {
    data.append("parent_comment_id", parentID);
  }
  data.append("text", text);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      //fetch fresh data from server
      if (parentID == videoID) {
        API_getRootComments(callback);
      } else {
        API_getReplyComments(parentID, callback);
      }
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_postUserComment(parentID, text, callback) {
  var url = "https://youcmt.com/api/comment/post/user";
  var data = new FormData(); //body
  if (parentID == videoID) {
    data.append("vid", parentID);
  } else {
    data.append("parent_comment_id", parentID);
  }
  data.append("text", text);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      var serverResponse = JSON.parse(this.response);
      if (parentID == videoID) {
        API_getRootComments(callback);
      } else {
        API_getReplyComments(parentID, callback);
      }
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_postGuestReply(parentID, rootID, text, callback) { //exclusively for replying to a non-root comment
  var url = "https://youcmt.com/api/comment/post/guest";
  var data = new FormData(); //body
  data.append("parent_comment_id", parentID);
  data.append("text", text);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      //fetch fresh data from server
      if (rootID == null) rootID = parentID;
      API_getReplyComments(rootID, callback);
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_postUserReply(parentID, rootID, text, callback) { //exclusively for replying to a non-root comment
  var url = "https://youcmt.com/api/comment/post/user";
  var data = new FormData(); //body
  data.append("parent_comment_id", parentID);
  data.append("text", text);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      var serverResponse = JSON.parse(this.response);
      if (rootID == null) rootID = parentID;
      API_getReplyComments(rootID, callback);
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_postCommentEdit(cid, newText, callback) {
  var url = "https://youcmt.com/api/comment/edit";
  var data = new FormData(); //body
  data.append("comment_id", cid);
  data.append("text", newText);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      var serverResponse = JSON.parse(this.response);
      API_getRootComments(callback);
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_postReplyEdit(cid, rootID, newText, callback) { //exclusively for replying to a non-root comment
  var url = "https://youcmt.com/api/comment/edit";
  var data = new FormData(); //body
  data.append("comment_id", cid);
  data.append("text", newText);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      var serverResponse = JSON.parse(this.response);
      API_getReplyComments(rootID, callback);
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_postCommentDelete(cid, callback) {
  var url = "https://youcmt.com/api/comment/delete";
  var data = new FormData(); //body
  data.append("comment_id", cid);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      var serverResponse = JSON.parse(this.response);
      API_getRootComments(callback);
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_postReplyDelete(cid, rootID, callback) { //exclusively for replying to a non-root comment
  var url = "https://youcmt.com/api/comment/delete";
  var data = new FormData(); //body
  data.append("comment_id", cid);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      var serverResponse = JSON.parse(this.response);
      API_getReplyComments(rootID, callback);
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_sendResetLink(email, callback) { //send link to the email, and callback with status code
  var url = "https://youcmt.com/api/user/confirm_email";
  var data = new FormData(); //body
  data.append("email", email);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //link sent
      callback(200);
    } else { //bad email / not found
      callback(404);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_resetPassword(email, code, newPass, callback) { //from "forgot password"
  var url = "https://youcmt.com/api/user/reset_password";
  var data = new FormData(); //body
  data.append("email", email);
  data.append("reset_code", code);
  data.append("new_password", newPass);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //link sent
      callback(200);
    } else if (request.status == 401) { //incorrect reset code
      callback(401);
    } else { //other error
      callback(500); //possible email changed
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_updateUserAvatar(selection, callback) {
  var url = "https://youcmt.com/api/user/update_profile";
  var data = new FormData(); //body
  data.append("new_profile_img", selection);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      var serverResponse = JSON.parse(this.response);
      console.log(serverResponse);
      callback(selection);
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_updateUserPassword(oldPassword, newPassword, callback) {
  var url = "https://youcmt.com/api/user/update_profile";
  var data = new FormData(); //body
  data.append("old_password", oldPassword);
  data.append("new_password", newPassword);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse.message);
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse.message); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_voteUp(cid, callback, tid) {
  var url = "https://youcmt.com/api/rate_comment";
  var data = new FormData(); //body
  data.append("comment_id", cid);
  data.append("rating", 1);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      var serverResponse = JSON.parse(this.response);
      if (tid == null) {
        API_getRootComments(callback);
      } else {
        API_getReplyComments(tid, callback);
      }
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_voteDown(cid, callback, tid) {
  var url = "https://youcmt.com/api/rate_comment";
  var data = new FormData(); //body
  data.append("comment_id", cid);
  data.append("rating", -1);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + userCookie.authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      var serverResponse = JSON.parse(this.response);
      if (tid == null) {
        API_getRootComments(callback);
      } else {
        API_getReplyComments(tid, callback);
      }
    } else { //error, unable to submit comment
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse); //should display some error message
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send(data);
}

function API_getHotVideos(callback) {
  var request = new XMLHttpRequest();
  request.open('GET', 'https://youcmt.com/api/video/whatshot', true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      console.log("Fetched Hot videos");
      var resonse = JSON.parse(this.response);
      console.log(resonse);
      callback(resonse);
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.send();
}
