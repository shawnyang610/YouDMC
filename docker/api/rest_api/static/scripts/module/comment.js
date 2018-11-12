//for shared comment functions
function writeReply(id) {
  //display the reply writing row for this comment
  console.log("Wrting reply for " + id);
}

function displaySubComments(cid, response) {
  if (response == null || response.length == 0) {
    console.log("This shouldn't happen, trying to display replies that doesn't exist");
  } else { //get to work
    //first find the rootObject
    var rootObject = findComment(cid);
    rootObject.showingReplies = true;
    rootObject.replies = mergeSubComments(rootObject.replies, response);

    //update rootObject's node code: show/hide and all that
    var rootObjectLi = getDOM(cid);
    rootObjectLi.innerHTML = rootObject.getListItem().innerHTML;

    // //this is the DOM to insert stuff into
    // var repliesList = getDOM("replyList_" + cid);
    // repliesList.className = ""; //get rid of spinning circle
    // repliesList.innerHTML = "";
    //
    // //console.log(rootObject.replies);
    // for (i = 0; i < rootObject.replies.length; i++) {
    //   repliesList.appendChild(rootObject.replies[i].getListItem());
    // }
    autoScroll();
  }
}

function writeReply(id) {
  getDOM("writeBox_" + id).style.display = "inline";
  getDOM("focusBox_" + id).focus();
}

function cancelReply(id) {
  getDOM("writeBox_" + id).style.display = "none";
}

function submitReply(id) {
  if (authToken == null || authToken == "") { //not logged in, post as guest
    postGuestComment(id, getDOM("focusBox_"+id).value);
  } else { //post as a logged in user
    postUserComment(id, getDOM("focusBox_"+id).value);
  }
  getDOM("focusBox_" + id).value = "";
  getDOM("writeBox_" + id).style.display = "none";
  scrollDiv = id;
}

function submitReply(id, top_id) {
  if (id == top_id) {
    submitReply(id);
    return;
  }
  if (authToken == null || authToken == "") { //not logged in, post as guest
    postGuestReply(id, top_id, getDOM("focusBox_"+id).value);
  } else { //post as a logged in user
    postUserReply(id, top_id, getDOM("focusBox_"+id).value);
  }
  getDOM("focusBox_" + id).value = "";
  getDOM("writeBox_" + id).style.display = "none";
  scrollDiv = id;
}

function postGuestComment(cid, text) {
  if (cid == videoID) { //a root comment
    API_postGuestComment(cid, text, displayRootComments);
  } else { //a reply
    API_postGuestComment(cid, text, displaySubComments);
  }
}

function postUserComment(cid, text) {
  if (cid == videoID) { //a root comment
    API_postUserComment(cid, text, displayRootComments);
  } else { //a reply
    API_postUserComment(cid, text, displaySubComments);
  }
}

function postGuestReply(cid, tid, text) {
  API_postGuestReply(cid, tid, text, displaySubComments);
}

function postUserReply(cid, tid, text) {
  API_postUserReply(cid, tid, text, displaySubComments);
}

function toggleReplies(cid) {
  var rootObject = findComment(cid);
  if (rootObject == null) {
    console.log("error - clicked on toggle reply of a ghost comment");
  }

  rootObject.showingReplies = !rootObject.showingReplies;
  if (rootObject.showingReplies) {
    rootObject.fetchReplies();
  }
  //update rootObject's node code: show/hide and all that
  var rootObjectLi = getDOM(cid);
  rootObjectLi.innerHTML = rootObject.getListItem().innerHTML;
}
