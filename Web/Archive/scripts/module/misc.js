//for video info and
function displayVideoInfo() {
  var videoInfoDiv = document.getElementById("videoInfo");
  videoInfoDiv.className = "mt-3"; //get rid of spinning circle
  if (videoInfo == null || videoInfo.message != null) {
    //videoInfoDiv.className = "bg-info"
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

function maximizeRootCommentBox() {
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
  var commentLink = createLink("Add your comment here", "badge badge-light", "maximizeRootCommentBox()", "");
  writingDiv.appendChild(commentLink);
}

function submitRootComment() {
  if (authToken == null || authToken == "") { //not logged in, post as guest
    postGuestComment(videoID, getDOM("rootInputBox").value);
  } else { //post as a logged in user
    postUserComment(videoID, getDOM("rootInputBox").value);
  }
  minimizeRootCommentBox();
}
