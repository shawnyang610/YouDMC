function RootCommentObject(commentAPIObject) {
  //data fields
  this.id = commentAPIObject.id;
  this.profilePictureUrl = getMeta("staticResourcePath") + "images/profile1.png";
  this.username = commentAPIObject.username;
  this.timestamp = translateDateTime(commentAPIObject.date);
  this.bodyText = commentAPIObject.text;
  this.up = document.createTextNode(commentAPIObject.like);
  this.dn = document.createTextNode(commentAPIObject.dislike);
  this.replyCount = commentAPIObject.count;
  this.replies = []; //an array of reply objects
  this.showingReplies = false;

  //important variables that requires dynamic change
  this.listItem = document.createElement("li"); //for return
  this.replyList = document.createElement("ul"); //for replies to this comment
  this.showRepliesText = document.createTextNode("");

  //transforming to listItem form
  this.render();
}

RootCommentObject.prototype.render = function() {
  this.listItem.className = "media my-4";
  var img = document.createElement("img");
  img.className = "mr-3";
  img.width = 75;
  img.src = this.profilePictureUrl;
  img.alt = "profilePic";
  var divBody = document.createElement("div");
  divBody.className = "media-body";
  var header = this.getHeader();
  var text = document.createTextNode(this.bodyText);
  var info = this.getInfo();
  this.replyBox = this.getReply();
  var more = this.getMore();
  divBody.appendChild(header); //commenter name and time
  divBody.appendChild(text); //comment text
  divBody.appendChild(info); //thumbs up/down and reply link
  divBody.appendChild(this.replyBox); //the "reply" input box
  divBody.appendChild(more); //the "show x comments"
  divBody.appendChild(this.replyList); //reserved for replyComments to this

  this.listItem.appendChild(img);
  this.listItem.appendChild(divBody);
}

//commenter name and time
RootCommentObject.prototype.getHeader = function() {
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
RootCommentObject.prototype.getInfo = function() { //not yet interactive
  var info = document.createElement("h6");
  var thumbsUpChar = document.createTextNode('\u{1F44D}');
  var thumbsDownChar = document.createTextNode('\u{1F44E}');
  var space = document.createTextNode(' ');

  info.appendChild(thumbsUpChar);
  info.appendChild(space);
  info.appendChild(this.up);
  info.appendChild(space);
  info.appendChild(thumbsDownChar);
  info.appendChild(space);
  info.appendChild(this.dn);
  info.appendChild(space);

  var replyLink = document.createElement("a");
  replyLink.appendChild(document.createTextNode("REPLY"));
  replyLink.setAttribute("onclick" ,"showReplyBox(" + this.id + ")");

  info.appendChild(replyLink);

  return info;
}

//the "reply" input box
RootCommentObject.prototype.getReply = function() { //ui not updated
  var replyBox = document.createElement("div");
  var inputField = document.createElement("textarea");
  //inputField.setAttribute("type", "text");
  var cancelButton = document.createElement("button");
  cancelButton.appendChild(document.createTextNode("Cancel"));
  cancelButton.setAttribute('onclick',"hideReplyBox(" + this.id + ")");
  cancelButton.style.float = "right";
  var submitButton = document.createElement("button");
  submitButton.appendChild(document.createTextNode("Submit"));
  submitButton.setAttribute('onclick','submitComment()');
  submitButton.style.float = "right";
  replyBox.appendChild(inputField);
  replyBox.appendChild(document.createElement("br"));
  replyBox.appendChild(submitButton);
  replyBox.appendChild(cancelButton);

  return replyBox;
}

//the "show x replies"
RootCommentObject.prototype.getMore = function() {
  var toggleLink = document.createElement("a");
  this.showRepliesText = "";
  if (this.replyCount == 1) {
    this.showRepliesText = "Show reply";
  } else if (this.replyCount > 1) {
    this.showRepliesText = "Show " + this.replyCount + " replies";
  }
  toggleLink.appendChild(document.createTextNode(this.showRepliesText));
  toggleLink.setAttribute("onclick", "toggleReplies(" + this.id + ")");
  return toggleLink;
}

RootCommentObject.prototype.getListItem = function() {
  return this.listItem;
}

RootCommentObject.prototype.appendToPage = function() {
  var page = document.getElementById("rootCommentsList");
  this.hideReplyBox();
  page.appendChild(this.getListItem());
};

RootCommentObject.prototype.hideReplyBox = function() {
  this.replyBox.style.display = "none";
}

RootCommentObject.prototype.showReplyBox = function() {
  this.replyBox.style.display = "inline";
}

RootCommentObject.prototype.toggleReplies = function() {
  if (this.showingReplies == true) {

  } else {

  }
}
