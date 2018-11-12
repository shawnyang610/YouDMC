var videoID;
var videoInfo;
var authToken;
var myUID;
var myUsername;
var myPIC;
var votedHistory;
var currentServerTime;
var rootCommentsArray = [];
var scrollDiv; //to ensure screen scroll to a certain position

function setup() {
  videoID = getMeta("videoID");
  authToken = sessionStorage.getItem("token");
  if (authToken != null && authToken != "") {
    myUID = sessionStorage.getItem("uid");
    myUsername = sessionStorage.getItem("myname");
    myPIC = sessionStorage.getItem("avatar");
  }
  votedHistory = sessionStorage.getItem("voted");
  if (votedHistory == null) {
    votedHistory = {};
  }
  fillDebugButtons();
  //choosed logged in header or logged out header
  //assumes user will not tamper with auth token so it's always valid
  if (authToken == null || authToken == "") {
    //logged out / not logged in
    fillHeaderLoggedOut();
  } else {
    //logged in
    fillHeaderLoggedIn();
  }
  //choose home page or comment page
  if (videoID == "" || videoID == "home") {
    loadHomePage();
  } else {
    loadCommentPage();
  }
}

function loadHomePage() {
  var content = document.getElementById("content");
  var homeText = document.createElement("h1");
  homeText.appendChild(document.createTextNode("Welcome to YouCMT.com!"))
  content.innerHTML = "";
  content.appendChild(homeText);
}

function loadCommentPage() {
  //no comments will be fetched until the servertime is known
  //this will ensure that the comments timestamp will be displayed properly
  API_getServerTime(API_getRootComments, displayRootComments);
  minimizeRootCommentBox();
  API_getVideoInfo(displayVideoInfo);
}

function displayRootComments(response) { //this is a callback function from API_getRootComments
  var commentsDiv = getDOM("comments");
  commentsDiv.className = ""; //get rid of spinning circle
  if (response == null) {
    commentsDiv.innerHTML = "Unable to fetch comments";
  } else if (response.length == 0) {
    commentsDiv.innerHTML = "This video does not yet have any comments!";
  } else { //get to work
    mergeRootComments(response);
    commentsDiv.innerHTML = "";
    for (i = 0; i < rootCommentsArray.length; i++) {
      commentsDiv.appendChild(rootCommentsArray[i].getListItem());
    }
    autoScroll();
  }
}
