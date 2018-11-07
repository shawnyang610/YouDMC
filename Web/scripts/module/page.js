var videoID;
var videoInfo;
var authToken;
var currentServerTime;
var rootCommentsArray = [];

function setup() {
  videoID = getMeta("videoID");
  sessionStorage.setItem("token", ""); //testing
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

function fillHeaderLoggedOut() {
  var headerDiv = document.getElementById("header-right");
  headerDiv.innerHTML = "";

  var form = document.createElement("form");
  var table = document.createElement("table");

  var labelRow = document.createElement("tr");

  var regCell = document.createElement("td");
  var regLink = document.createElement("button");
  regLink.appendChild(document.createTextNode("Register"));
  regLink.setAttribute("onclick", "showRegister()");
  regLink.className = "btn btn-info";
  regCell.appendChild(regLink);
  regCell.setAttribute('rowspan', '2');

  var unLabelCell = document.createElement("td");
  unLabelCell.appendChild(document.createTextNode("Username:"));
  var pwLabelCell = document.createElement("td");
  pwLabelCell.appendChild(document.createTextNode("Password:"));

  var logCell = document.createElement("td");
  var logLink = document.createElement("button");
  logLink.appendChild(document.createTextNode("Login"));
  logLink.setAttribute("onclick", "login()");
  //login() should probably pass username and password to the function??
  logLink.className = "btn btn-info";
  logCell.appendChild(logLink);
  logCell.setAttribute('rowspan', '2');

  labelRow.appendChild(regCell);
  labelRow.appendChild(unLabelCell);
  labelRow.appendChild(pwLabelCell);
  labelRow.appendChild(logCell);

  var inputRow = document.createElement("tr");
  var unInputCell = document.createElement("td");
  var unInput = document.createElement("input");
  unInput.id = "headerUNinput";
  unInput.setAttribute('type', 'text');
  unInput.setAttribute('name', 'username');
  unInputCell.appendChild(unInput);
  var pwInputCell = document.createElement("td");
  var pwInput = document.createElement("input");
  pwInput.id = "headerPWinput";
  pwInput.setAttribute('type', 'password');
  pwInput.setAttribute('name', 'password');
  pwInputCell.appendChild(pwInput);

  inputRow.appendChild(unInputCell);
  inputRow.appendChild(pwInputCell);

  table.appendChild(labelRow);
  table.appendChild(inputRow);
  form.appendChild(table);
  headerDiv.appendChild(form);
}

function fillHeaderLoggedIn() {
  var headerDiv = document.getElementById("header-right");
  headerDiv.innerHTML = "";
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
  fillCommentBox();
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
  commentsDiv.className = "bg-warning"; //get rid of spinning circle
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
  var writingDiv = document.getElementById("write");
  writingDiv.className = "input-group";

  var inputBox = document.createElement("input");
  inputBox.className = "form-control";
  inputBox.setAttribute("placeholder","Add your comment here");
    var buttonGroup = document.createElement("div");
    buttonGroup.className = "input-group-append";
      var cancelButton = document.createElement("button");
      cancelButton.className = "btn btn-secondary";
      cancelButton.appendChild(document.createTextNode("Cancel"));
      cancelButton.setAttribute("onclick", "cancelRootComment()");
      var submitButton = document.createElement("button");
      submitButton.className = "btn btn-secondary";
      submitButton.appendChild(document.createTextNode("Submit"));
      submitButton.setAttribute("onclick", "submitRootComment()");
    buttonGroup.appendChild(cancelButton);
    buttonGroup.appendChild(submitButton);
  writingDiv.appendChild(inputBox);
  writingDiv.appendChild(buttonGroup);
}
