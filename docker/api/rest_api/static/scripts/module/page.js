var videoID;
var videoInfo;
var authToken;
var currentServerTime;
var rootCommentsArray = [];

function setup() {
  videoID = getMeta("videoID");
  authToken = sessionStorage.getItem("token");
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

function fillHeaderLoggedOut(statusText) {
  var headerDiv = document.getElementById("header-right");
  headerDiv.innerHTML = "";

  var inputGroupDiv = document.createElement("div");
  inputGroupDiv.className = "input-group";
  var userNameInput = createInput("text", "username", "form-control");
  userNameInput.id = "headerUNinput";
  var passwordInput = createInput("password", "password", "form-control");
  passwordInput.id = "headerPWinput";
  var loginButton = createButton("Log In", "input-group-append btn", "logIn()");

  inputGroupDiv.appendChild(userNameInput);
  inputGroupDiv.appendChild(passwordInput);
  inputGroupDiv.appendChild(loginButton);

  var registerButton = createButton("Register", "btn btn-warning ml-2", "showRegister()");
  inputGroupDiv.appendChild(registerButton);

  headerDiv.appendChild(inputGroupDiv);

  var statusP = document.createElement("p");
  statusP.className = "text-danger";
  statusP.id = "headerStatusText";
  statusP.style.display = "none"; //hide by default
  headerDiv.appendChild(statusP);
}

function changeHeaderStatusText(newText) {
  var statusP = getDOM("headerStatusText");
  if (newText != null && newText != "") {
    statusP.style.display = "inline";
    statusP.innerHTML = newText;
  } else {
    statusP.style.display = "none";
    statusP.innerHTML = "";
  }
}

function fillHeaderLoggedIn() {
  var headerDiv = document.getElementById("header-right");
  headerDiv.innerHTML = "";
  var form = document.createElement("form");
  var table = document.createElement("table");

  var labelRow = document.createElement("tr");

  var acctCell = document.createElement("td");
  var acctButton = createButton("Account", "btn btn-lg btn-outline-primary", "showAccountPanel()");
  acctCell.appendChild(acctButton);
  acctCell.setAttribute('rowspan', '2');

  var logoCell = document.createElement("td");
  var logoButton = createButton("Log out", "btn btn-lg btn-outline-danger", "logOut()");
  logoCell.appendChild(logoButton);
  logoCell.setAttribute('rowspan', '2');

  labelRow.appendChild(acctCell);
  labelRow.appendChild(logoCell);

  table.appendChild(labelRow);

  form.appendChild(table);
  headerDiv.appendChild(form);
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
  getServerTime(getRootComments, displayRootComments);
  minimizeRootCommentBox();
  getVideoInfo(displayVideoInfo);
}

function displayVideoInfo() {
  var videoInfoDiv = document.getElementById("videoInfo");
  videoInfoDiv.className = "bg-info"; //get rid of spinning circle
  if (videoInfo == null || videoInfo.message != null) {
    videoInfoDiv.innerHTML = "Unable to fetch video info";
  } else { //successfully got info: render to page
    var table = document.createElement("table");
    var cell = document.createElement("td");

    var titleRow = document.createElement("tr");
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode("Title"));
    titleRow.appendChild(cell);
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode(videoInfo.title));
    titleRow.appendChild(cell);

    var dateRow = document.createElement("tr");
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode("Date"));
    dateRow.appendChild(cell);
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode(videoInfo.date));
    dateRow.appendChild(cell);

    var authorRow = document.createElement("tr");
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode("Author"));
    authorRow.appendChild(cell);
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode(videoInfo.author));
    authorRow.appendChild(cell);

    var descriptionRow = document.createElement("tr");
    descriptionRow.id = "descriptionRow";
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode("Description"));
    descriptionRow.appendChild(cell);
    cell = document.createElement("td");
    cell.id = "descriptionCell";
    //this will either contain the description or the "show description" trigger
    descriptionRow.appendChild(cell);

    var hideRow = document.createElement("tr");
    //this will always have the "hide description" trigger, but could be hidden
    hideRow.id = "hideRow";
    cell = document.createElement("td");
      hideRow.appendChild(cell); //empty cell for indent
    cell = document.createElement("td");
      var hideTrigger = document.createElement("button");
      hideTrigger.className = "btn btn-sm btn-outline-secondary";
      hideTrigger.appendChild(document.createTextNode("Hide Description"));
      hideTrigger.setAttribute("onclick", "hideDescription()");
      cell.appendChild(hideTrigger);
      hideRow.appendChild(cell);

    table.appendChild(titleRow);
    table.appendChild(dateRow);
    table.appendChild(authorRow);
    table.appendChild(descriptionRow);
    table.appendChild(hideRow);

    videoInfoDiv.appendChild(table);
    hideDescription();
  }
}

function showDescription() {
  //clicked on show description trigger ->
  //change cell text to description and show "hide row"
  var discriptionCell = document.getElementById("descriptionCell");
  discriptionCell.innerHTML = videoInfo.description;
  var hideRow = document.getElementById("hideRow");
  //show the hide row
  hideRow.style.display = "";
}

function hideDescription() {
  //clicked on hide description trigger ->
  //change cell text to trigger and hide "hide row"
  var discriptionCell = document.getElementById("descriptionCell");
  discriptionCell.innerHTML = "";
  var showTrigger = document.createElement("button");
  showTrigger.className = "btn btn-sm btn-outline-secondary";
  showTrigger.appendChild(document.createTextNode("Show Description"));
  showTrigger.setAttribute("onclick", "showDescription()");
  discriptionCell.appendChild(showTrigger);
  var hideRow = document.getElementById("hideRow");
  //hide the hide row
  hideRow.style.display = "none";
}

function displayRootComments() {
  var commentsDiv = document.getElementById("comments");
  commentsDiv.className = ""; //get rid of spinning circle
  if (rootCommentsArray == null) {
    commentsDiv.innerHTML = "Unable to fetch comments";
  } else if (rootCommentsArray.length == 0) {
    commentsDiv.innerHTML = "This video does not yet have any comments!";
  } else {
    for (i = 0; i < rootCommentsArray.length; i++) {
      commentsDiv.appendChild(rootCommentsArray[i].getListItem());
    }
  }
}

function fillCommentBox() {
  var writingDiv = getDOM("write");
  writingDiv.innerHTML = "";
  writingDiv.className = "input-group";

  var inputBox = document.createElement("input");
  inputBox.id = "rootInputBox";
  inputBox.className = "form-control";
  inputBox.setAttribute("placeholder","Add your comment here");
    var buttonGroup = document.createElement("div");
    buttonGroup.className = "input-group-append";
      var cancelButton = createButton("Cancel",
      "btn btn-secondary", "minimizeRootCommentBox()");
      var submitButton = createButton("Submit",
      "btn btn-secondary", "submitRootComment()");
    buttonGroup.appendChild(cancelButton);
    buttonGroup.appendChild(submitButton);
  writingDiv.appendChild(inputBox);
  writingDiv.appendChild(buttonGroup);
  inputBox.focus();
}

function minimizeRootCommentBox() {
  var writingDiv = getDOM("write");
  writingDiv.className = "h3";
  writingDiv.innerHTML = "";
  var commentLink = createLink("Add your comment here", "badge badge-light", "fillCommentBox()", "");
  writingDiv.appendChild(commentLink);
}

function submitRootComment() {
  if (authToken == null || authToken == "") { //not logged in, post as guest
    postGuestComment(videoID, getDOM("rootInputBox").value, fakeAddMyComment);
  } else { //post as a logged in user
    postUserComment(videoID, getDOM("rootInputBox").value, fakeAddMyComment);
  }
  minimizeRootCommentBox();
}

function fakeAddMyComment(parentID, text) {
  if (text == null) {
    console.log("Unable to submit comment");
  } else {
    console.log("Successfully added comment");
  }
  refreshMessages(); //this is not good. should add just the recent comment manually
}
