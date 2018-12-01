var videoID;    //the video id of current page
//var videoInfo;  //???

var currentServerTime;        //loaded when page is refreshed
var rootCommentsArray = [];   //saves all root comments into local memory
//var scrollDiv; //to ensure screen scroll to a certain position

function setup() {
  initializeGlobalVars();
  //choosed logged in header or logged out header
  //assumes user will not tamper with auth token so it's always valid
  if (userCookie.authToken == "") {
    fillNavBarLoggedOut();
  } else {
    fillNavBarLoggedIn();
  }
  //choose home page or comment page
  if (videoID == null || videoID == "" || videoID == "home") {
    loadHomePage();
  } else {
    loadCommentPage();
  }
}

function initializeGlobalVars() {
  videoID = getMeta("videoID");
  getSessionStorage();
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
    //autoScroll();
  }
}
