function editComment(cid, tid) {
  var dropDownBtn = getDOM(cid + "_dropDown")
  dropDownBtn.style.display = "none";
  var workZone = getDOM(cid + "_textDiv");
  workZone.setAttribute("save", workZone.innerHTML);
  var originalText = workZone.innerHTML;
  workZone.innerHTML = "";
  var writingDiv = createDiv("inputGroup");
    writingDiv.className = "input-group mt-1 mb-1";
    var inputBox = document.createElement("input");
    inputBox.id = cid + "_editBox";
    inputBox.className = "form-control";
    inputBox.value = originalText;
      var buttonGroup = document.createElement("div");
      buttonGroup.className = "input-group-append";
        var cancelButton = createButton("Cancel",
        "btn btn-sm btn-secondary", "cancelEdit(" + cid + "," + tid + ")");
        var submitButton = createButton("Submit",
        "btn btn-sm btn-secondary", "submitEdit(" + cid + "," + tid + ")");
      buttonGroup.appendChild(cancelButton);
      buttonGroup.appendChild(submitButton);
    writingDiv.appendChild(inputBox);
    writingDiv.appendChild(buttonGroup);
  workZone.appendChild(writingDiv);
  inputBox.focus();
}

function cancelEdit(cid, originalText) {
  var workZone = getDOM(cid + "_textDiv");
  workZone.innerHTML = workZone.getAttribute("save");
  var dropDownBtn = getDOM(cid + "_dropDown")
  dropDownBtn.style.display = "inline";
}

function submitEdit(cid, tid) {
  var dropDownBtn = getDOM(cid + "_dropDown")
  dropDownBtn.style.display = "inline";
  var workZone = getDOM(cid + "_editBox");
  var newText = workZone.value;
  if (newText == "") { //you cannot have an empty comment
    workZone.focus();
    return;
  }
  if (tid == null) { //edited a root comment
    API_postCommentEdit(cid, newText, displayRootComments);
  } else { //edited a reply
    API_postReplyEdit(cid, tid, newText, displaySubComments);
  }
}

function deleteComment(cid, tid) {
  var dropDownBtn = getDOM(cid + "_dropDown")
  dropDownBtn.style.display = "none";
  var workZone = getDOM(cid + "_textDiv");
  workZone.setAttribute("save", workZone.innerHTML);
  workZone.innerHTML = "";
  var inputGroup = createDiv("inputGroup");
    inputGroup.className = "input-group mt-1 mb-1";
    var label = createDiv("input-group-prepend");
      var span = document.createElement("span");
      span.className = "input-group-text";
      span.innerHTML = "Are you sure you want to delete this comment?";
    label.appendChild(span);
      var buttonGroup = document.createElement("div");
      buttonGroup.className = "input-group-append";
        var cancelButton = createButton("Cancel",
        "btn btn-sm btn-secondary", "cancelEdit(" + cid + "," + tid + ")");
        var submitButton = createButton("Delete",
        "btn btn-sm btn-secondary", "confirmDelete(" + cid + "," + tid + ")");
      buttonGroup.appendChild(cancelButton);
      buttonGroup.appendChild(submitButton);
    inputGroup.appendChild(label);
    inputGroup.appendChild(buttonGroup);
  workZone.appendChild(inputGroup);
}

function confirmDelete(cid, tid) {
  if (tid == null) { //deleted a root comment
    API_postCommentDelete(cid, displayRootComments);
  } else { //deleted a reply
    API_postReplyDelete(cid, tid, displaySubComments);
  }
}
