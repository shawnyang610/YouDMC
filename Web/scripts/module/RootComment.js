//comment object constructor :: for all comments
var RootComment = function(commentAPIObject) {
  this.cid = commentAPIObject.id;
  this.pic = getMeta("staticResourcePath") +
    "images/profile" + commentAPIObject.profile_img + ".png"; //guest use 0
  this.data = commentAPIObject;
  this.replies = []; //for storing replies
  this.showingReplies = false;
};

RootComment.prototype.update = function(newAPIObject) {
  this.data = newAPIObject;
  if (this.showingReplies) {
    //fetch replies via api, then callback display subcomments
    API_getReplyComments(this.cid, displaySubComments);
  } //else don't care
}

RootComment.prototype.getListItem = function() {
  var list = document.createElement("li");
  list.id = this.cid;
  list.className = "media";
  var img = document.createElement("img");
    img.className = "mr-2"
    img.width = 50;
    img.src = this.pic;
    img.alt = "profilePic";
  var divBody = document.createElement("div");
    divBody.className = "media-body";
    divBody.appendChild(this.getHeader()); //commenter name and time
    divBody.appendChild(this.getText()); //comment body text
    divBody.appendChild(this.getInfo()); //thumbs up/down and reply link
    divBody.appendChild(this.getWriteBox()); //the "reply" input box
    divBody.appendChild(this.getReplyList()); //all the replies
    if (this.data.count > 0) {
      divBody.appendChild(this.getToggleButton()); //the show/hide replies button
    }
  list.appendChild(img);
  list.appendChild(divBody);
  return list;
}

RootComment.prototype.getHeader = function() {
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

RootComment.prototype.getText = function() {
  if (this.data.is_deleted == 0) { //valid comment
    return createText(this.data.text);
  } else {
    return deletedTemplate();
  }
}

RootComment.prototype.getInfo = function() {
  var info = document.createElement("h6");
  var cid = this.cid;
  var voteStatus = this.cid;
  if (voteStatus == 1) {
    info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-success", "", ""));
    info.appendChild(createText(this.data.like));
    info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-light", "voteDown(" + this.cid + ")", "#down"));
    info.appendChild(createText(this.data.dislike));
  } else if (voteStatus == -1) {
    info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-light", "voteUp(" + this.cid + ")", "#up"));
    info.appendChild(createText(this.data.like));
    info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-danger", "", ""));
    info.appendChild(createText(this.data.dislike));
  } else {
    info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-light", "voteUp(" + this.cid + ")", "#up"));
    info.appendChild(createText(this.data.like));
    info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-light", "voteDown(" + this.cid + ")", "#down"));
    info.appendChild(createText(this.data.dislike));
  }
  info.appendChild(createText(" "));
  info.appendChild(createLink("REPLY","badge badge-secondary", "writeReply(" + this.cid + ")", "#reply"));
  return info;
}

RootComment.prototype.getWriteBox = function() {
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
      "btn btn-sm btn-secondary", "submitReply(" + this.cid + ")");
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

//functions exclusive to rootcomments starts here
RootComment.prototype.getReplyList = function() {
  var list = document.createElement("ul");
  list.id = "replyList_" + this.cid;
  if (this.showingReplies) {
    //loop and render all the replies
    for (i = 0; i < this.replies.length; i++) {
      list.appendChild(this.replies[i].getListItem());
    }
    list.style.display = "inline";
  } else {
    list.style.display = "none";
  }
  return list;
}

RootComment.prototype.getToggleButton = function() {
  var text;
  var cid = this.cid;
  var showing = this.showingReplies;
  var count = this.data.count; //number of replies
  if (!showing) {
    if (count == 1) {
      text = "Show reply";
    } else {
      text = "Show " + count + " replies";
    }
  } else {
    if (count == 1) {
      text = "Hide reply";
    } else {
      text = "Hide replies";
    }
  }
  var toggleButton = createButton(text, "btn btn-outline-secondary btn-sm",
    "toggleReplies(" + cid + ")");
  return toggleButton;
}

RootComment.prototype.fetchReplies = function() {
  API_getReplyComments(this.cid, displaySubComments);
}
