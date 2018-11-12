//subcomment constructor
var SubComment = function(commentAPIObject) {
  this.cid = commentAPIObject.id;
  this.pic = getMeta("staticResourcePath") +
    "images/profile" + commentAPIObject.profile_img + ".png"; //guest use 0
  this.data = commentAPIObject;
};

SubComment.prototype.update = function(newAPIObject) {
  this.data = newAPIObject;
}

SubComment.prototype.getListItem = function() {
  var list = document.createElement("li");
  list.className = "media mb-3";
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
  return header;
}

SubComment.prototype.getText = function() {
  if (this.data.is_deleted == 0) { //valid comment
    return createText(this.data.text);
  } else {
    return deletedTemplate();
  }
}

SubComment.prototype.getInfo = function() {
  var info = document.createElement("h6");
  info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-light", "voteUp(" + this.cid + ")", "#up"));
  info.appendChild(createText(this.data.like));
  info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-light", "voteDown(" + this.cid + ")", "#down"));
  info.appendChild(createText(this.data.dislike));
  info.appendChild(createText(" "));
  info.appendChild(createLink("REPLY","badge badge-secondary", "writeReply(" + this.cid + ")", "#reply"));
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
