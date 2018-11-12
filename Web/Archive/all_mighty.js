var currentServerTime;
var rootCommentsArray = [];

//main routine
async function setup() {
  loadTime();
}

//pre-work
function loadTime() {
  var request = new XMLHttpRequest();
  request.open('GET', 'https://youcmt.com/api/datetime', true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var responseTime = JSON.parse(this.response).datetime;
      console.log("set current time = " + responseTime);
      currentTime = responseTime;
      loadPage(); //guarantees time is loaded
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.send();
}

//loading page modules
function loadPage() {
  console.log("loadpage started");
  loadHeader();
  loadDebug();
  var VID = getMeta("videoID");
  if (VID == "") {
    loadHomePage();
  } else {
    loadCommentPage();
  }
}

//header
loadHeader() {
  loadLogo(); //make sure logo path is correct
  if (true) {
    loadLoggedOutHeader();
  } else {
    loadLoggedInHeader();
  }
}

function loadLogo() {
  var logo = document.getElementById("logoIcon");
  logo.src = getMeta("staticResourcePath") + "images/logo.png";
}

function loadLoggedOutHeader() {
  document.getElementById("header-right").innerHTML = ""; //clear_header
  var form = document.createElement("form");
  var table = document.createElement("table");

  var labelRow = document.createElement("tr");

  var regCell = document.createElement("td");
  var regLink = document.createElement("a");
  regLink.appendChild(document.createTextNode("Register"));
  regLink.href = "#register";
  regCell.appendChild(regLink);
  regCell.setAttribute('rowspan', '2');

  var unLabelCell = document.createElement("td");
  unLabelCell.appendChild(document.createTextNode("Username:"));
  var pwLabelCell = document.createElement("td");
  pwLabelCell.appendChild(document.createTextNode("Password:"));

  var logCell = document.createElement("td");
  var logLink = document.createElement("a");
  logLink.appendChild(document.createTextNode("Login"));
  logLink.href = "#login";
  logCell.appendChild(logLink);
  logCell.setAttribute('rowspan', '2');

  labelRow.appendChild(regCell);
  labelRow.appendChild(unLabelCell);
  labelRow.appendChild(pwLabelCell);
  labelRow.appendChild(logCell);

  var inputRow = document.createElement("tr");
  var unInputCell = document.createElement("td");
  var unInput = document.createElement("input");
  unInput.setAttribute('type', 'text');
  unInput.setAttribute('name', 'username');
  unInputCell.appendChild(unInput);
  var pwInputCell = document.createElement("td");
  var pwInput = document.createElement("input");
  pwInput.setAttribute('type', 'password');
  pwInput.setAttribute('name', 'password');
  pwInputCell.appendChild(pwInput);

  inputRow.appendChild(unInputCell);
  inputRow.appendChild(pwInputCell);

  table.appendChild(labelRow);
  table.appendChild(inputRow);
  form.appendChild(table);
  document.getElementById("header-right").appendChild(form);
}

function loadLoggedInHeader() {
  document.getElementById("header-right").innerHTML = ""; //clear_header
  var form = document.createElement("form");
  var table = document.createElement("table");

  var labelRow = document.createElement("tr");

  var acctCell = document.createElement("td");
  var acctLink = document.createElement("a");
  acctLink.appendChild(document.createTextNode("Account"));
  acctLink.href = "#account";
  acctCell.appendChild(acctLink);
  acctCell.setAttribute('rowspan', '2');

  var logoCell = document.createElement("td");
  var logoLink = document.createElement("a");
  logoLink.appendChild(document.createTextNode("Logout"));
  logoLink.href = "#logout";
  logoCell.appendChild(logoLink);
  logoCell.setAttribute('rowspan', '2');

  labelRow.appendChild(acctCell);
  labelRow.appendChild(logoCell);

  table.appendChild(labelRow);

  form.appendChild(table);
  document.getElementById("header-right").appendChild(form);
}

//debug options
loadDebug() {
  var vid = getMeta("videoID");
  if (vid == "") {
    document.getElementById("testSectionVID").appendChild(document.createTextNode("Home page"));
  } else {
    document.getElementById("testSectionVID").appendChild(document.createTextNode(vid));
  }
}

//page ****************************************************************************************
function loadHomePage() {
  var content = document.getElementById("content");
  var homeText = document.createElement("h1");
  homeText.appendChild(document.createTextNode("Welcome to YouCMT.com!"))
  content.innerHTML = "";
  content.appendChild(homeText);
}


function loadCommentPage() {
  //for test section only
  //showVideoInfo();
  //hideCommentBox(); //initialize "add your comment here"
  refresh_messages();
}



//Constructor for a RootCommentObject
function RootCommentObject(commentAPIObject) {
  //data fields
  this.id = commentAPIObject.id;
  this.profilePictureUrl = getMeta("staticResourcePath") + "images/profile1.png";
  this.username = commentAPIObject.username;
  this.timestamp = translateDateTime(commentAPIObject.date);
  this.bodyText = commentAPIObject.text;
  this.up = document.createTextNode(commentAPIObject.like);
  this.dn = document.createTextNode(commentAPIObject.dislike);
  this.replyCount = commentAPIObject.count;
  this.replies = []; //an array of reply objects
  this.showingReplies = false;

  //important variables that requires dynamic change
  this.listItem = document.createElement("li"); //for return
  this.replyList = document.createElement("ul"); //for replies to this comment
  this.showRepliesText = document.createTextNode("");

  //transforming to listItem form
  this.render();
}

RootCommentObject.prototype.render = function() {
  this.listItem.className = "media my-4";
  var img = document.createElement("img");
  img.className = "mr-3";
  img.width = 75;
  img.src = this.profilePictureUrl;
  img.alt = "profilePic";
  var divBody = document.createElement("div");
  divBody.className = "media-body";
  var header = this.getHeader();
  var text = document.createTextNode(this.bodyText);
  var info = this.getInfo();
  this.replyBox = this.getReply();
  var more = this.getMore();
  divBody.appendChild(header); //commenter name and time
  divBody.appendChild(text); //comment text
  divBody.appendChild(info); //thumbs up/down and reply link
  divBody.appendChild(this.replyBox); //the "reply" input box
  divBody.appendChild(more); //the "show x comments"
  divBody.appendChild(this.replyList); //reserved for replyComments to this

  this.listItem.appendChild(img);
  this.listItem.appendChild(divBody);
}

//commenter name and time
RootCommentObject.prototype.getHeader = function() {
  var header = document.createElement("h6");
  var posterLink = document.createElement("a");
  posterLink.appendChild(document.createTextNode(this.username));
  posterLink.href = "#posterLink";
  header.appendChild(posterLink);
  header.appendChild(document.createTextNode(" "));
  header.appendChild(document.createTextNode(this.timestamp));
  return header;
}

//thumbs up/down and reply link
RootCommentObject.prototype.getInfo = function() {
  var info = document.createElement("h6");
  var thumbsUpChar = document.createTextNode('\u{1F44D}');
  var thumbsDownChar = document.createTextNode('\u{1F44E}');
  var space = document.createTextNode(' ');

  info.appendChild(thumbsUpChar);
  info.appendChild(space);
  info.appendChild(this.up);
  info.appendChild(space);
  info.appendChild(thumbsDownChar);
  info.appendChild(space);
  info.appendChild(this.dn);
  info.appendChild(space);

  var replyLink = document.createElement("a");
  replyLink.appendChild(document.createTextNode("REPLY"));
  replyLink.setAttribute("onclick" ,"showReplyBox(" + this.id + ")");

  info.appendChild(replyLink);

  return info;
}

//the "reply" input box
RootCommentObject.prototype.getReply = function() {
  var replyBox = document.createElement("div");
  var inputField = document.createElement("textarea");
  //inputField.setAttribute("type", "text");
  var cancelButton = document.createElement("button");
  cancelButton.appendChild(document.createTextNode("Cancel"));
  cancelButton.setAttribute('onclick',"hideReplyBox(" + this.id + ")");
  cancelButton.style.float = "right";
  var submitButton = document.createElement("button");
  submitButton.appendChild(document.createTextNode("Submit"));
  submitButton.setAttribute('onclick','submitComment()');
  submitButton.style.float = "right";
  replyBox.appendChild(inputField);
  replyBox.appendChild(document.createElement("br"));
  replyBox.appendChild(submitButton);
  replyBox.appendChild(cancelButton);

  return replyBox;
}

//the "show x replies"
RootCommentObject.prototype.getMore = function() {
  var toggleLink = document.createElement("a");
  this.showRepliesText = "";
  if (this.replyCount == 1) {
    this.showRepliesText = "Show reply";
  } else if (this.replyCount > 1) {
    this.showRepliesText = "Show " + this.replyCount + " replies";
  }
  toggleLink.appendChild(document.createTextNode(this.showRepliesText));
  toggleLink.setAttribute("onclick", "toggleReplies(" + this.id + ")");
  return toggleLink;
}

RootCommentObject.prototype.getListItem = function() {
  return this.listItem;
}

RootCommentObject.prototype.appendToPage = function() {
  var page = document.getElementById("rootCommentsList");
  this.hideReplyBox();
  page.appendChild(this.getListItem());
};

RootCommentObject.prototype.hideReplyBox = function() {
  this.replyBox.style.display = "none";
}

RootCommentObject.prototype.showReplyBox = function() {
  this.replyBox.style.display = "inline";
}

RootCommentObject.prototype.toggleReplies = function() {
  if (this.showingReplies == true) {

  } else {

  }
}

function renderRootComments() { //renders all root comments based on local memory
  for (i = 0; i < rootCommentsArray.length; i++) {
    rootCommentsArray[i].appendToPage();
  }
}

function fetchRootComments() { //fethes all root comments for the current page
  var videoID = getMeta("videoID");
  var url = "https://youcmt.com/api/comment?vid=" + videoID;
  var request = new XMLHttpRequest();
  request.open('GET', url, true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var serverResponse = JSON.parse(this.response).comments;
      console.log("serverResponse.length = " + serverResponse.length);
      for (i = 0; i < serverResponse.length; i++) {
        rootCommentsArray[i] = new RootCommentObject(serverResponse[i]);
      }
      //console.log("root comments array saved locally");
      //inializeReplyComments();
      renderRootComments();
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

function clear_messages() {
  document.getElementById("rootCommentsList").innerHTML = "";
}

function refresh_messages() {
  clear_messages();
  fetchRootComments();
}

function getCommentObject(commentID) {
  for (i = 0; i < rootCommentsArray.length; i++) {
    if (rootCommentsArray[i].id == commentID) {
      return rootCommentsArray[i];
    }
  }
  return 0;
}

function hideReplyBox(commentID) {
  var commentObject = getCommentObject(commentID);
  if (commentObject != 0) {
    commentObject.hideReplyBox();
  } else {
    console.log("Error when looking for " + commendID);
  }
}

function showReplyBox(commentID) {
  var commentObject = getCommentObject(commentID);
  if (commentObject != 0) {
    commentObject.showReplyBox();
  } else {
    console.log("Error when looking for " + commendID);
  }
}

function toggleReplies(commentID) {
  var commentObject = getCommentObject(commentID);
  if (commentObject != 0) {
    commentObject.toggleReplies();
  } else {
    console.log("Error when looking for " + commendID);
  }
}

function translateDateTime(commentTime) {
  var ct = new Date(commentTime);
  var st = new Date(currentTime);
  console.log("commentTime = " + ct);
  console.log("serverTime = " + st);
  var diff = (st-ct)/60000; //1 min = 60 000 ms

  if (diff < 1) {
    return "Just now";
  } else if (diff < 2) {
    return "1 minute ago";
  } else if (diff < 60) {
    return Math.floor(diff) + " minutes ago";
  } else {
    diff /= 60; //diff is now in hours
    if (diff < 2) {
      return "1 hour ago";
    } else if (diff < 24) {
      return Math.floor(diff) + " hours ago";
    } else {
      diff /= 24; //diff is now in days
      if (diff < 2) {
        return " 1 day ago";
      } else if (diff < 30) {
        return Math.floor(diff) + " days ago";
      } else {
        diff /= 30.42; //diff is now in months
        if (diff < 2) {
          return "1 month ago";
        } else if (diff < 12) {
          return Math.floor(diff) + " months ago";
        } else {
          diff /= 12; //diff is now in years
          if (diff < 2) {
            return "1 year ago";
          } else {
            return Math.floor(diff) + " years ago";
          }
        }
      }
    }
  }
  return ct;
}

function getMeta(name) { //helper function to get any meta info based on tag
  var metas = document.getElementsByTagName('meta');
  for (var i=0; i < metas.length; i++) {
      if (metas[i].getAttribute("name") == name) {
         return metas[i].getAttribute("content");
      }
   }
   return "";
}
