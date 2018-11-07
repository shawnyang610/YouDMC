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
  //function createLink(linkText, linkClass, functionName, href)
  var info = document.createElement("h6");
  var thumbsUpChar = document.createTextNode('\u{1F44D}');
  var thumbsDownChar = document.createTextNode('\u{1F44E}');
  var space = document.createTextNode(' ');

  info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-light", "voteUp()", ""));
  info.appendChild(this.up);
  //info.appendChild(space);
  info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-light", "voteDown()", ""));
  //info.appendChild(space);
  info.appendChild(this.dn);
  info.appendChild(space);

  info.appendChild(createLink("REPLY","badge badge-secondary", "reply()", ""));

  return info;
}

//the "reply" input box
RootCommentObject.prototype.getReply = function() { //buttons not linked to functions
  var writingDiv = document.getElementById("write");
  writingDiv.className = "input-group";

  var commentRowDiv = document.createElement("div");
  var inputBox = document.createElement("input");
  inputBox.className = "form-control";
  inputBox.setAttribute("placeholder","Add your comment here");
    var buttonGroup = document.createElement("div");
    buttonGroup.className = "input-group-append";
      var cancelButton = document.createElement("button");
      cancelButton.className = "btn btn-sm btn-secondary";
      cancelButton.appendChild(document.createTextNode("Cancel"));
      cancelButton.setAttribute("onclick", "cancelRootComment()");
      var submitButton = document.createElement("button");
      submitButton.className = "btn btn-sm btn-secondary";
      submitButton.appendChild(document.createTextNode("Submit"));
      submitButton.setAttribute("onclick", "submitRootComment()");
    buttonGroup.appendChild(cancelButton);
    buttonGroup.appendChild(submitButton);
  commentRowDiv.appendChild(inputBox);
  commentRowDiv.appendChild(buttonGroup);
  return commentRowDiv;
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
