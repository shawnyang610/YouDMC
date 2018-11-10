function SubCommentObject(commentAPIObject) {
  //data fields
  this.id = commentAPIObject.id;
  this.profilePictureUrl = getMeta("staticResourcePath") + "images/profile2.png";
  this.username = commentAPIObject.username;
  this.timestamp = translateDateTime(commentAPIObject.date);
  this.bodyText = commentAPIObject.text;
  this.up = commentAPIObject.like;
  this.dn = commentAPIObject.dislike;

  //important variables that requires dynamic change
  this.listItem = document.createElement("li"); //for return

  //transforming to listItem form
  this.render();
}

//this function will create all HTML DOM objects based on this object
SubCommentObject.prototype.render = function() {
  this.listItem.className = "media mt-3";
    var img = document.createElement("img");
    //img.className = "mr-3";
    img.width = 75;
    img.src = this.profilePictureUrl;
    img.alt = "profilePic";
  var divBody = document.createElement("div");
  divBody.className = "media-body";

  divBody.appendChild(this.getHeader()); //commenter name and time
  divBody.appendChild(createText(this.bodyText)); //comment text
  divBody.appendChild(this.getInfo()); //thumbs up/down and reply link
  divBody.appendChild(this.getWriteBox()); //the "reply" input box
  divBody.appendChild(this.initializeReplyList()); //reserved for replyComments to this
  this.listItem.appendChild(img);
  this.listItem.appendChild(divBody);
}

//commenter name and time
SubCommentObject.prototype.getHeader = function() {
  var header = document.createElement("h6");
  var posterLink = document.createElement("a");
  posterLink.appendChild(document.createTextNode(this.username));
  posterLink.href = "#posterLink";
  header.appendChild(posterLink);
  header.appendChild(document.createTextNode(" "));
  header.appendChild(document.createTextNode(this.timestamp));
  return header;
}

//thumbs up/down and reply link
SubCommentObject.prototype.getInfo = function() { //not yet implemented voting
  var info = document.createElement("h6");
  info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-light", "voteUp()", "#up"));
  info.appendChild(createText(this.up));
  info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-light", "voteDown()", "#down"));
  info.appendChild(createText(this.dn));
  info.appendChild(createText(" "));
  info.appendChild(createLink("REPLY","badge badge-secondary", "writeReply(" + this.id + ")", ""));
  return info;
}

//the "reply" input box
SubCommentObject.prototype.getWriteBox = function() { //buttons not linked to functions
  var commentRowDiv = document.createElement("div");
  commentRowDiv.style.display = "none";
  commentRowDiv.class = "input-group input-group-sm";
  commentRowDiv.id = "writeBox_" + this.id;
  var inputBox = createInput("text", "Add your comment here", "form-control");
  inputBox.id = "focusBox_" + this.id;
  var buttonGroup = document.createElement("div");
  buttonGroup.className = "input-group-append";
    var cancelButton = createButton("Cancel",
      "btn btn-sm btn-secondary", "cancelReply(" + this.id + ")");
    var submitButton = createButton("Submit",
      "btn btn-sm btn-secondary", "submitReply(" + this.id + ")");
    buttonGroup.appendChild(cancelButton);
    buttonGroup.appendChild(submitButton);
  commentRowDiv.appendChild(inputBox);
  commentRowDiv.appendChild(buttonGroup);
  return commentRowDiv;
}

//the unordered list - reserved for replies to this root comment
SubCommentObject.prototype.initializeReplyList = function() {
  var list = document.createElement("ul");
  list.id = "replyList_" + this.id;
  return list;
}

SubCommentObject.prototype.getListItem = function() {
  return this.listItem;
}
