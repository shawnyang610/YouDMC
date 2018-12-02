/** commentAPIObject includes
    "id": 90,
    "date": "2018-12-01 18:08:28.070898",
    "edit_date": "2018-12-01 18:08:28.070898",
    "is_deleted": 0,
    "text": "User root comment 1",
    "user_id": 18,
    "username": "qwe",
    "profile_img": "128",
    "top_comment_id": 0,
    "parent_comment_id": 0,
    "like": 1,
    "dislike": 0,
    "count": 0,
    "voted": -1
*/
//comment object constructor :: for all comments
var RootComment = function(commentAPIObject) {
  this.cid = commentAPIObject.id;
  this.pic = getMeta("staticResourcePath") +
    "images/avatars/" + commentAPIObject.profile_img + ".png"; //guest use 0
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
  if (this.data.user_id == userCookie.uid && this.data.is_deleted == 0) { //this is "my comment"
    header.appendChild(this.getDropdown());
  }
  return header;
}

RootComment.prototype.getText = function() {
  if (this.data.is_deleted == 0) { //valid comment
    var textDiv = document.createElement("div");
    textDiv.id = this.cid + "_textDiv";
    textDiv.appendChild(createText(this.data.text));
    return textDiv;
  } else {
    return deletedTemplate();
  }
}

RootComment.prototype.getInfo = function() {
  var info = document.createElement("h6");
  if (this.data.voted != null && this.data.voted == 1) { //voted up
    info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-success", "voteUp(" + this.cid + ")", "#up"));
    info.appendChild(createText(this.data.like));
    info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-light", "voteDown(" + this.cid + ")", "#down"));
    info.appendChild(createText(this.data.dislike));
    info.appendChild(createText(" "));
    info.appendChild(createLink("REPLY","badge badge-secondary", "writeReply(" + this.cid + ")", "#reply"));
  } else if (this.data.voted != null && this.data.voted == -1) { //voted down
    info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-light", "voteUp(" + this.cid + ")", "#up"));
    info.appendChild(createText(this.data.like));
    info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-danger", "voteDown(" + this.cid + ")", "#down"));
    info.appendChild(createText(this.data.dislike));
    info.appendChild(createText(" "));
    info.appendChild(createLink("REPLY","badge badge-secondary", "writeReply(" + this.cid + ")", "#reply"));
  } else { //no vote
    info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-light", "voteUp(" + this.cid + ")", "#up"));
    info.appendChild(createText(this.data.like));
    info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-light", "voteDown(" + this.cid + ")", "#down"));
    info.appendChild(createText(this.data.dislike));
    info.appendChild(createText(" "));
    info.appendChild(createLink("REPLY","badge badge-secondary", "writeReply(" + this.cid + ")", "#reply"));
  }
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
  list.className = "list-group";
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

RootComment.prototype.getDropdown = function() {
  var btnGrp = createDiv("dropdown");
  btnGrp.style.display = "inline";
  btnGrp.id = this.cid + "_dropDown";
  var dropButton = document.createElement("a");
  dropButton.setAttribute("class", "float-right badge badge-pill badge-light dropdown-toggle");
  dropButton.setAttribute("data-toggle", "dropdown");
  dropButton.innerHTML = " ";
  var options = createDiv("dropdown-menu");
    var link0 = createLink("Edit", "dropdown-item", "editComment("+this.cid+")", "");
    var link1 = createLink("Delete", "dropdown-item", "deleteComment("+this.cid+")", "");
    options.appendChild(link0);
    options.appendChild(link1);
  btnGrp.appendChild(dropButton);
  btnGrp.appendChild(options);
  return btnGrp;
}

function toggleReplies(cid) { //when show/hide reply is clicked
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

function displaySubComments(cid, response) { //forced to show reply, after getting fresh data
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
  }
}
