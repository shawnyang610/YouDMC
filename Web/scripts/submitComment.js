//all comment submits will be here guest/user, root/reply, and their callbacks

//==============================================================================
//input boxes for ready to write================================================
//==============================================================================

function minimizeRootCommentBox() {
  var writingDiv = getDOM("write");
  writingDiv.className = "h5 bg-white";
  writingDiv.innerHTML = "";
  var commentLink = createLink("Add your comment here", "badge badge-white", "maximizeRootCommentBox()", "");
  writingDiv.appendChild(commentLink);
}

function maximizeRootCommentBox() {
  var writingDiv = getDOM("write");
  writingDiv.innerHTML = "";
  writingDiv.className = "input-group mt-3 mb-3";

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

function writeReply(id) { //when clicked on "REPLY"
  getDOM("writeBox_" + id).style.display = "inline";
  getDOM("focusBox_" + id).focus();
}

function cancelReply(id) { //clicked "cancel" on a "REPLY"
  getDOM("writeBox_" + id).style.display = "none";
}

//==============================================================================
//submissions ==================================================================
//==============================================================================

function submitRootComment() {
  var commentText = getDOM("rootInputBox").value;
  if (commentText == null || commentText == "") { //guard against empty comment
    getDOM("rootInputBox").focus();
    return;
  }
  if (userCookie.authToken == null || userCookie.authToken == "") { //not logged in, post as guest
    postGuestRootComment(videoID, commentText);
  } else { //post as a logged in user
    postUserRootComment(videoID, commentText);
  }
  minimizeRootCommentBox();
}

function submitReply(parent_id, top_id) { //submitting a reply under a thread
  //if parent_id = top_id, this is a direct response to a root comment
  if (userCookie.authToken == null || userCookie.authToken == "") { //not logged in, post as guest
    postGuestReply(parent_id, top_id, getDOM("focusBox_"+parent_id).value);
  } else { //post as a logged in user
    postUserReply(parent_id, top_id, getDOM("focusBox_"+parent_id).value);
  }
  getDOM("focusBox_" + parent_id).value = "";
  getDOM("writeBox_" + parent_id).style.display = "none";
  //scrollDiv = id;
}

function postGuestRootComment(videoID, commentText) {
  API_postGuestComment(videoID, commentText, displayRootComments);
}

function postUserRootComment(videoID, commentText) {
  API_postUserComment(videoID, commentText, displayRootComments);
}

function postGuestReply(parent_id, top_id, commentText) {
  API_postGuestReply(parent_id, top_id, commentText, displaySubComments);
}

function postUserReply(parent_id, top_id, commentText) {
  API_postUserReply(parent_id, top_id, commentText, displaySubComments);
}

//==============================================================================
//merging and memory management=================================================
//==============================================================================
function mergeRootComments(response) { //given new root comments array, merge
  var temp = [];
  var newComments = response.length - rootCommentsArray.length;
  for (i = 0; i < newComments; i++) {
    temp[i] = new RootComment(response[i]);
  }
  for (i = 0; i < rootCommentsArray.length; i++) {
    rootCommentsArray[i].update(response[newComments + i]);
    temp[newComments + i] = rootCommentsArray[i];
  }
  rootCommentsArray = temp;
}

function mergeSubComments(oldArray, newArray) { //given new sub-comment array, merge
  var temp = [];
  var newComments = newArray.length - oldArray.length;
  for (i = 0; i < newComments; i++) {
    temp[i] = new SubComment(newArray[i]);
  }
  for (i = 0; i < oldArray.length; i++) {
    oldArray[i].update(newArray[newComments + i]);
    temp[newComments + i] = oldArray[i];
  }
  return temp;
}

function isRootComment(cid) { //test if a comment is root or not
  for (i = 0; i < rootCommentsArray; i++) {
    if (rootCommentsArray[i].cid == cid) {
      return true;
    }
  }
  return false;
}
