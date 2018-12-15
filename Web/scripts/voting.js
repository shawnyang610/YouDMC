function voteUp(cid, tid) { //only works sub comments
  if (userCookie.authToken == null || userCookie.authToken == "") {
    showRegister();
    getDOM("statusText").innerHTML = "You must be logged in first before voting, register now?"
  } else {
    if (tid == null) {
      API_voteUp(cid, displayRootComments);
    } else {
      API_voteUp(cid, displaySubComments, tid);
    }
  }
}

function voteDown(cid, tid) {
  if (userCookie.authToken == null || userCookie.authToken == "") {
    showRegister();
    getDOM("statusText").innerHTML = "You must be logged in first before voting, register now?"
  } else {
    if (tid == null) {
      API_voteDown(cid, displayRootComments);
    } else {
      API_voteDown(cid, displaySubComments, tid);
    }
  }
}
