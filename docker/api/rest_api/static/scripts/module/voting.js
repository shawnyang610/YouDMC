function voteUp(cid, tid) { //only works sub comments
  if (authToken == null || authToken == "") {
    alert("You must be logged in first before voting");
  } else {
    if (tid == null) {
      API_voteUp(cid, displayRootComments);
    } else {
      API_voteUp(cid, displaySubComments, tid);
    }
  }
}

function voteDown(cid) {
  if (authToken == null || authToken == "") {
    alert("You must be logged in first before voting");
  } else {
    if (tid == null) {
      API_voteDown(cid, displayRootComments);
    } else {
      API_voteDown(cid, displaySubComments, tid);
    }
  }
}
