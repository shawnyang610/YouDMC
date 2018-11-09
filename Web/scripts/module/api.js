function getServerTime(callback, innerCallBack) {
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

function getVideoInfo(callback) {
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

function getRootComments(callback) {
  var url = "https://youcmt.com/api/comment/get/comments?vid=" + videoID;
  var request = new XMLHttpRequest();
  request.open('GET', url, true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var serverResponse = JSON.parse(this.response).comments;
      console.log("Loaded " + serverResponse.length + " root comment(s)");
      for (i = 0; i < serverResponse.length; i++) {
        rootCommentsArray[i] = new RootCommentObject(serverResponse[i]);
      }
      callback();
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

function getReplyComments(rootCommentID, callback) {
  var url = "https://youcmt.com/api/comment/get/comments?top_comment_id=" + rootCommentID;
  var request = new XMLHttpRequest();
  request.open('GET', url, true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var serverResponse = JSON.parse(this.response).comments;
      console.log("Loaded " + serverResponse.length + " reply comment(s) for rootComment " + rootCommentID);
      var replyArray = findReplyArray(rootCommentID);
      for (i = 0; i < serverResponse.length; i++) {
        replyArray[i] = new SubCommentObject(serverResponse[i]);
      }
      callback(rootCommentID);
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

function registerUser(callback) {
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

function logInUser(callback) {
  var url = "https://youcmt.com/api/user/login";
  var userNameInput = document.getElementById("headerUNinput").value;
  var passwordInput = document.getElementById("headerPWinput").value;
  var data = new FormData(); //body
  data.append('username', userNameInput);
  data.append('password', passwordInput);
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //login success
      var serverResponse = JSON.parse(this.response);
      callback(serverResponse);
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

function logOutUser(callback) {
  var url = "https://youcmt.com/api/user/logout_access";
  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.setRequestHeader("Authorization", "Bearer " + authToken); //append this into header
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

function postGuestComment(parentID, text, callback) {
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
      var serverResponse = JSON.parse(this.response);
      callback(parentID, text); //render the text onto the page
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

function postUserComment(parentID, text, callback) {
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
  request.setRequestHeader("Authorization", "Bearer " + authToken); //append this into header
  request.onload = function() {
    console.log(JSON.parse(this.response));
    if (request.status == 200) { //comment saved
      var serverResponse = JSON.parse(this.response);
      callback(parentID, text); //render the text onto the page
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

function sendResetLink(email, callback) { //send link to the email, and callback with status code
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

function resetPassword(email, code, newPass, callback) {
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
