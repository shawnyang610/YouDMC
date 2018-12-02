//subcomment constructor
var SubComment = function(commentAPIObject) {
  this.cid = commentAPIObject.id;
  this.pic = getMeta("staticResourcePath") +
    "images/avatars/" + commentAPIObject.profile_img + ".png"; //guest use 0
  this.data = commentAPIObject;
};

SubComment.prototype.update = function(newAPIObject) {
  this.data = newAPIObject;
}

SubComment.prototype.getListItem = function() {
  var list = document.createElement("li");
  list.className = "media";
  var img = document.createElement("img");
    img.width = 50;
    img.src = this.pic;
    img.alt = "profilePic";
  var divBody = document.createElement("div");
    divBody.className = "media-body";
    divBody.appendChild(this.getHeader()); //commenter name and time
    divBody.appendChild(this.getText()); //comment body text
    divBody.appendChild(this.getInfo()); //thumbs up/down and reply link
    divBody.appendChild(this.getWriteBox()); //the "reply" input box
  list.appendChild(img);
  list.appendChild(divBody);
  return list;
}

SubComment.prototype.getHeader = function() {
  var header = document.createElement("h6");
  var posterLink = createLink(this.data.username, "", "", "#posterLink");
  var timestampText = " ";
  if (this.data.date == this.data.edit_date) {
    //original comments
    timestampText += translateDateTime(this.data.date);
  } else {
    timestampText += "edited " + translateDateTime(this.data.edit_date);
  }
  header.appendChild(posterLink);
  header.appendChild(createText(timestampText));
  if (this.data.user_id == userCookie.uid  && this.data.is_deleted == 0) { //this is "my comment"
    header.appendChild(this.getDropdown());
  }
  return header;
}

SubComment.prototype.getText = function() {
  if (this.data.is_deleted == 0) { //valid comment
    var textDiv = document.createElement("div");
    textDiv.id = this.cid + "_textDiv";
    textDiv.appendChild(createText(this.data.text));
    return textDiv;
  } else {
    return deletedTemplate();
  }
}

SubComment.prototype.getInfo = function() {
  var info = document.createElement("h6");
  if (this.data.voted != null && this.data.voted == 1) { //voted up
    info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-success",
      "voteUp(" + this.cid + "," + this.data.top_comment_id + ")", "#up"));
    info.appendChild(createText(this.data.like));
    info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-light",
      "voteDown(" + this.cid + "," + this.data.top_comment_id + ")", "#down"));
    info.appendChild(createText(this.data.dislike));
    info.appendChild(createText(" "));
    info.appendChild(createLink("REPLY","badge badge-secondary", "writeReply(" + this.cid + ")", "#reply"));
  } else if (this.data.voted != null && this.data.voted == -1) { //voted down
    info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-light",
      "voteUp(" + this.cid + "," + this.data.top_comment_id + ")", "#up"));
    info.appendChild(createText(this.data.like));
    info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-danger",
      "voteDown(" + this.cid + "," + this.data.top_comment_id + ")", "#down"));
    info.appendChild(createText(this.data.dislike));
    info.appendChild(createText(" "));
    info.appendChild(createLink("REPLY","badge badge-secondary", "writeReply(" + this.cid + ")", "#reply"));
  } else {
    info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-light",
      "voteUp(" + this.cid + "," + this.data.top_comment_id + ")", "#up"));
    info.appendChild(createText(this.data.like));
    info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-light",
      "voteDown(" + this.cid + "," + this.data.top_comment_id + ")", "#down"));
    info.appendChild(createText(this.data.dislike));
    info.appendChild(createText(" "));
    info.appendChild(createLink("REPLY","badge badge-secondary", "writeReply(" + this.cid + ")", "#reply"));
  }
  return info;
}

SubComment.prototype.getWriteBox = function() {
  var commentRowDiv = document.createElement("div");
  commentRowDiv.style.display = "none";
  commentRowDiv.class = "input-group input-group-sm";
  commentRowDiv.id = "writeBox_" + this.cid;
  var inputBox = createInput("text", "Add your comment here", "form-control");
  inputBox.id = "focusBox_" + this.cid;
  var buttonGroup = document.createElement("div");
  buttonGroup.className = "input-group-append";
    var cancelButton = createButton("Cancel",
      "btn btn-sm btn-secondary", "cancelReply(" + this.cid + ")");
    var submitButton = createButton("Submit",
      "btn btn-sm btn-secondary", "submitReply(" + this.cid + "," + this.data.top_comment_id + ")");
    buttonGroup.appendChild(cancelButton);
    buttonGroup.appendChild(submitButton);
  commentRowDiv.appendChild(inputBox);
  commentRowDiv.appendChild(buttonGroup);
  //enter key trigger
  inputBox.addEventListener("keyup", function(event) {
    event.preventDefault();
	  if (event.keyCode === 13) { // Number 13 is the "Enter" key on the keyboard
		    submitButton.click();
	  }
  });
  return commentRowDiv;
}

SubComment.prototype.getDropdown = function() {
  var btnGrp = createDiv("dropdown");
  btnGrp.style.display = "inline";
  btnGrp.id = this.cid + "_dropDown";
  var dropButton = document.createElement("a");
  dropButton.setAttribute("class", "float-right badge badge-pill badge-light dropdown-toggle");
  dropButton.setAttribute("data-toggle", "dropdown");
  dropButton.innerHTML = " ";
  var options = createDiv("dropdown-menu");
    var link0 = createLink("Edit", "dropdown-item", "editComment(" + this.cid
                            + "," + this.data.top_comment_id + ")", "");
    var link1 = createLink("Delete", "dropdown-item", "deleteComment("+ this.cid
                            + "," + this.data.top_comment_id + ")", "");
    options.appendChild(link0);
    options.appendChild(link1);
  btnGrp.appendChild(dropButton);
  btnGrp.appendChild(options);
  return btnGrp;
}
