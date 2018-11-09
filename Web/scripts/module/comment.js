function RootCommentObject(commentAPIObject) {
  //data fields
  this.id = commentAPIObject.id;
  this.profilePictureUrl = getMeta("staticResourcePath") + "images/profile2.png";
  this.username = commentAPIObject.username;
  this.timestamp = translateDateTime(commentAPIObject.date);
  this.bodyText = commentAPIObject.text;
  this.up = commentAPIObject.like;
  this.dn = commentAPIObject.dislike;
  this.replyCount = commentAPIObject.count;
  this.replies = []; //an array of reply objects
  this.showingReplies = false;

  //important variables that requires dynamic change
  this.listItem = document.createElement("li"); //for return

  //transforming to listItem form
  this.render();
}

//this function will create all HTML DOM objects based on this object
RootCommentObject.prototype.render = function() {
  this.listItem.className = "media my-4";
    var img = document.createElement("img");
    img.className = "mr-3";
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
  if (this.replyCount > 0) {
    var buttonDiv = document.createElement("div");
    buttonDiv.id = "toggleDiv_" + this.id;
    buttonDiv.appendChild(getToggleButton(this.id, this.showingReplies, this.replyCount));
    divBody.appendChild(buttonDiv); //the "show x comments"
  }
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
RootCommentObject.prototype.getInfo = function() { //not yet implemented voting
  var info = document.createElement("h6");
  info.appendChild(createLink('\u{1F44D}',"badge badge-pill badge-light", "voteUp()", "#up"));
  info.appendChild(createText(this.up));
  info.appendChild(createLink('\u{1F44E}',"badge badge-pill badge-light", "voteDown()", "#down"));
  info.appendChild(createText(this.dn));
  info.appendChild(createText(" "));
  info.appendChild(createLink("REPLY","badge badge-secondary", "writeReply(" + this.id + ")", "#reply"));
  return info;
}

//the "reply" input box
RootCommentObject.prototype.getWriteBox = function() { //buttons not linked to functions
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
RootCommentObject.prototype.initializeReplyList = function() {
  var list = document.createElement("ul");
  list.id = "replyList_" + this.id;
  return list;
}

//the "show x replies"
function getToggleButton(id, showing, count) {
  var text;
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
  "toggleReplies(" + id + "," + showing + "," + count + ")");
  return toggleButton;
}

RootCommentObject.prototype.getListItem = function() {
  return this.listItem;
}

function writeReply(id) {
  console.log("Write Reply " + id);
  getDOM("writeBox_" + id).style.display = "inline";
  getDOM("focusBox_" + id).focus();
}

function cancelReply(id) {
  getDOM("writeBox_" + id).style.display = "none";
}

function submitReply(id) {
  if (authToken == null || authToken == "") { //not logged in, post as guest
    postGuestComment(id, getDOM("focusBox_"+id).value, fakeAddMyComment);
  } else { //post as a logged in user
    postUserComment(id, getDOM("focusBox_"+id).value, fakeAddMyComment);
  }
  getDOM("focusBox_" + id).value = "";
  getDOM("writeBox_" + id).style.display = "none";
}

function toggleReplies(id, showing, count) {
  this.showingReplies = !this.showingReplies; //flip
  var toggleDiv = getDOM("toggleDiv_" + id);
  toggleDiv.innerHTML = "";
  toggleDiv.appendChild(getToggleButton(id, !showing, count));

  if (!this.showingReplies) { //was showing, now hide
    getDOM("replyList_" + id).style.display = "none"; //hide the list
  } else { //was hiding, now show
    var list = getDOM("replyList_" + id);
    list.innerHTML = ""; //clear all current content
    list.className = "loader_comments"; //setup spinning circle
    list.style.display = ""; //show the list
    getReplyComments(id, updateReplies); //call API, and callback when ready
  }
}

function updateReplies(id) { //api called back, ready to render all replies
  var replyArray = findReplyArray(id);
  if (replyArray == null || replyArray.length == 0) {
    //this shouldn't happen, it means API failed to get any replies
    console.log("API failed to fetch replies???");
    return;
  } else {
    var list = getDOM("replyList_" + id);
    list.className = ""; //get rid of spinning circle
    for (i = replyArray.length - 1; i >= 0; i--) {
      list.appendChild(replyArray[i].getListItem());
    }
  }
}
