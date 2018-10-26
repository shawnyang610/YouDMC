function setup() {
  loadLogo(); //make sure logo path is correct
  var VID = getMeta("videoID");
  if (VID == "") {
    loadHomePage();
  } else {
    loadCommentPage();
  }
}

function loadHomePage() {
  fill_header_loggedOut(); //default not logged in
  document.getElementById("testSectionVID").appendChild(document.createTextNode("Home page")); //for test section only
  var content = document.getElementById("content");
  content.innerHTML = "Welcome to YouCMT.com!";
}

function loadCommentPage() {
  fill_header_loggedOut(); //default not logged in
  document.getElementById("testSectionVID").appendChild(document.createTextNode(getMeta("videoID"))); //for test section only
  showVideoInfo();
  hideCommentBox(); //initialize "add your comment here"
  //fetchComments(); //show existing comments
  refresh_messages(); //in case no-show
}

function loadLogo() {
  var logo = document.getElementById("logoIcon");
  logo.src = getMeta("staticResourcePath") + "images/logo.png";
}

function fill_header_loggedOut() {
  clear_header();
  var form = document.createElement("form");
  var table = document.createElement("table");

  var labelRow = document.createElement("tr");

  var regCell = document.createElement("td");
  var regLink = document.createElement("a");
  regLink.appendChild(document.createTextNode("Register"));
  regLink.href = "#register";
  regCell.appendChild(regLink);
  regCell.setAttribute('rowspan', '2');

  var unLabelCell = document.createElement("td");
  unLabelCell.appendChild(document.createTextNode("Username:"));
  var pwLabelCell = document.createElement("td");
  pwLabelCell.appendChild(document.createTextNode("Password:"));

  var logCell = document.createElement("td");
  var logLink = document.createElement("a");
  logLink.appendChild(document.createTextNode("Login"));
  logLink.href = "#login";
  logCell.appendChild(logLink);
  logCell.setAttribute('rowspan', '2');

  labelRow.appendChild(regCell);
  labelRow.appendChild(unLabelCell);
  labelRow.appendChild(pwLabelCell);
  labelRow.appendChild(logCell);

  var inputRow = document.createElement("tr");
  var unInputCell = document.createElement("td");
  var unInput = document.createElement("input");
  unInput.setAttribute('type', 'text');
  unInput.setAttribute('name', 'username');
  unInputCell.appendChild(unInput);
  var pwInputCell = document.createElement("td");
  var pwInput = document.createElement("input");
  pwInput.setAttribute('type', 'password');
  pwInput.setAttribute('name', 'password');
  pwInputCell.appendChild(pwInput);

  inputRow.appendChild(unInputCell);
  inputRow.appendChild(pwInputCell);

  table.appendChild(labelRow);
  table.appendChild(inputRow);
  form.appendChild(table);
  document.getElementById("header-right").appendChild(form);
}

function fill_header_loggedIn() {
  clear_header();
  var form = document.createElement("form");
  var table = document.createElement("table");

  var labelRow = document.createElement("tr");

  var acctCell = document.createElement("td");
  var acctLink = document.createElement("a");
  acctLink.appendChild(document.createTextNode("Account"));
  acctLink.href = "#account";
  acctCell.appendChild(acctLink);
  acctCell.setAttribute('rowspan', '2');

  var logoCell = document.createElement("td");
  var logoLink = document.createElement("a");
  logoLink.appendChild(document.createTextNode("Logout"));
  logoLink.href = "#logout";
  logoCell.appendChild(logoLink);
  logoCell.setAttribute('rowspan', '2');

  labelRow.appendChild(acctCell);
  labelRow.appendChild(logoCell);

  table.appendChild(labelRow);

  form.appendChild(table);
  document.getElementById("header-right").appendChild(form);
}

function clear_header() {
  document.getElementById("header-right").innerHTML = "";
}

function show_sidepanel() {
  document.getElementById("content").style.width = "75%";
  document.getElementById("aside").style.width = "25%";
  document.getElementById("aside").innerHTML = "Side panel content to be filled";
}

function hide_sidepanel() {
  document.getElementById("content").style.width = "100%";
  document.getElementById("aside").style.width = "0%";
  document.getElementById("aside").innerHTML = "";
}

function appendComment(commentObject, appendLocation) {
  
}

function insert_fetchedComment(commentObject) {
  /**
    At this point this only works for root comments
    For "leaf" comments, I plan on using the term "replies"
    Structure:Comments -> p object -> messageTable -> Table body -> rows
  */
  var newP = document.createElement("p");
  newP.id = commentObject.id; //set proper ID so other guys can find him
  newP.style.border = "solid #00ffff"; //not needed for production

  var messageTable = document.createElement("TABLE");
  var tblBody = document.createElement("tbody");
  var topRow = document.createElement("tr"); //for header info
  var bodyRow = document.createElement("tr"); //for main text
  var infoRow = document.createElement("tr"); //for up/down and reply link
  var moreRow = document.createElement("tr"); //only if there are replies
  var replyRow = document.createElement("tr"); //reserved space for reply box

  var cell, cellText;

  //---top row: profile picture (spans 2 rows), username, and time
  cell = document.createElement("td"); //Profile Picture
  var profilePic = document.createElement("img");
  profilePic.setAttribute("src", getMeta("staticResourcePath") + "images/profile1.png"); //user profile picture
  profilePic.setAttribute("width", "75");
  cell.rowSpan = "2";
  cell.appendChild(profilePic);
  topRow.appendChild(cell);

  cell = document.createElement("td"); //Message User
  var userLink = document.createElement("a");
  userLink.appendChild(document.createTextNode(commentObject.username));
  userLink.href = "#user_" + commentObject.username;
  cell.appendChild(userLink);
  topRow.appendChild(cell);

  cell = document.createElement("td"); //Message Time
  cellText = document.createTextNode(Date());
  cell.appendChild(cellText);
  topRow.appendChild(cell);
  //---end of top row

  //--- body row: for the actual text
  cell = document.createElement("td"); //Message Content
  cellText = document.createTextNode(commentObject.text);
  cell.colSpan = "2";
  cell.appendChild(cellText);
  bodyRow.appendChild(cell);
  //---end of body row

  //--- info row: for thumbs up, down, and reply link
  cell = document.createElement("td"); //empty space
  infoRow.appendChild(cell);
  var thumbsUpChar = '\u{1F44D}';
  var thumbsDownChar = '\u{1F44E}';
  cell = document.createElement("td"); //vote up button/char/icon and count
  var voteUpButton = document.createElement("a");
  voteUpButton.appendChild(document.createTextNode(thumbsUpChar));
  voteUpButton.href = "#votedUP";
  cell.appendChild(voteUpButton);
  cellText = document.createTextNode(commentObject.like);
  cell.appendChild(cellText);
  infoRow.appendChild(cell);

  cell = document.createElement("td"); //vote down button/char/icon and count
  var voteDownButton = document.createElement("a");
  voteDownButton.appendChild(document.createTextNode(thumbsDownChar));
  voteDownButton.href = "#votedDown";
  cell.appendChild(voteDownButton);
  cellText = document.createTextNode(commentObject.dislike);
  cell.appendChild(cellText);
  infoRow.appendChild(cell);

  cell = document.createElement("td"); //reply link
  var replyLink = document.createElement("a");
  replyLink.appendChild(document.createTextNode("Reply"));
  replyLink.href = "#reply"; //this should trigger reply box <<<---------------------------------
  replyLink.setAttribute("onclick", "showReplyBox("+ commentObject.id +")");
  cell.appendChild(replyLink);
  infoRow.appendChild(cell);
  //---end of info row

  //--- more row: only show this if there is replies to this thread
  cell = document.createElement("td"); //empty space
  moreRow.appendChild(cell);
  cell = document.createElement("td"); //show replies link
  var showRepliesLink = document.createElement("a");
  if (commentObject.count == 1) {
    cellText = document.createTextNode("Show 1 reply");
  } else {
    cellText = document.createTextNode("Show " + commentObject.count + " replies");
  }
  showRepliesLink.appendChild(cellText);
  showRepliesLink.setAttribute("onclick", "fetchReplies("+ commentObject.id +")");
  cell.appendChild(showRepliesLink);
  cell.rowSpan = "2";
  cell.id = commentObject.id + "fetchRepliesCell";
  moreRow.appendChild(cell);
  //---end of more row

  //--- reply row: reserved space for reply reply box
  cell = document.createElement("td"); //empty space
  replyRow.appendChild(cell);
  cell = document.createElement("td"); //empty space
  cell.id = commentObject.id + "replyCell";
  cell.rowSpan = "2";
  replyRow.appendChild(cell);
  //---end of reply row

  //finishing touches
  tblBody.appendChild(topRow);
  tblBody.appendChild(bodyRow);
  tblBody.appendChild(infoRow);
  if (commentObject.count > 0 || false) { //toggle t/f for testing
    tblBody.appendChild(moreRow);
  }
  tblBody.appendChild(replyRow);
  messageTable.appendChild(tblBody);
  newP.appendChild(messageTable);
  document.getElementById("comments").appendChild(newP);
}

function insert_fetchedReply(commentObject, parentObject) {
  /**
    This only works for "replies" which are non-root comments
    The parent object should be the cell reserved for posting replies
    Structure:cell -> p object -> messageTable -> Table body -> rows
                   -> next p object
                   -> next p object
  */
  var newP = document.createElement("p");
  newP.id = commentObject.id; //set proper ID so other guys can find him
  newP.style.border = "solid #ff00ff"; //not needed for production

  var messageTable = document.createElement("TABLE");
  var tblBody = document.createElement("tbody");
  var topRow = document.createElement("tr"); //for header info
  var bodyRow = document.createElement("tr"); //for main text
  var infoRow = document.createElement("tr"); //for up/down and reply link
  var moreRow = document.createElement("tr"); //only if there are replies
  var replyRow = document.createElement("tr"); //reserved space for reply box

  var cell, cellText;

  //---top row: profile picture (spans 2 rows), username, and time
  cell = document.createElement("td"); //Profile Picture
  var profilePic = document.createElement("img");
  profilePic.setAttribute("src", getMeta("staticResourcePath") + "images/profile1.png"); //user profile picture
  profilePic.setAttribute("width", "75");
  cell.rowSpan = "2";
  cell.appendChild(profilePic);
  topRow.appendChild(cell);

  cell = document.createElement("td"); //Message User
  var userLink = document.createElement("a");
  userLink.appendChild(document.createTextNode(commentObject.username));
  userLink.href = "#user_" + commentObject.username;
  cell.appendChild(userLink);
  topRow.appendChild(cell);

  cell = document.createElement("td"); //Message Time
  cellText = document.createTextNode(Date());
  cell.appendChild(cellText);
  topRow.appendChild(cell);
  //---end of top row

  //--- body row: for the actual text
  cell = document.createElement("td"); //Message Content
  cellText = document.createTextNode(commentObject.text);
  cell.colSpan = "2";
  cell.appendChild(cellText);
  bodyRow.appendChild(cell);
  //---end of body row

  //--- info row: for thumbs up, down, and reply link
  cell = document.createElement("td"); //empty space
  infoRow.appendChild(cell);
  var thumbsUpChar = '\u{1F44D}';
  var thumbsDownChar = '\u{1F44E}';
  cell = document.createElement("td"); //vote up button/char/icon and count
  var voteUpButton = document.createElement("a");
  voteUpButton.appendChild(document.createTextNode(thumbsUpChar));
  voteUpButton.href = "#votedUP";
  cell.appendChild(voteUpButton);
  cellText = document.createTextNode(commentObject.like);
  cell.appendChild(cellText);
  infoRow.appendChild(cell);

  cell = document.createElement("td"); //vote down button/char/icon and count
  var voteDownButton = document.createElement("a");
  voteDownButton.appendChild(document.createTextNode(thumbsDownChar));
  voteDownButton.href = "#votedDown";
  cell.appendChild(voteDownButton);
  cellText = document.createTextNode(commentObject.dislike);
  cell.appendChild(cellText);
  infoRow.appendChild(cell);

  cell = document.createElement("td"); //reply link
  var replyLink = document.createElement("a");
  replyLink.appendChild(document.createTextNode("Reply"));
  replyLink.setAttribute("onclick", "showReplyBox("+ commentObject.id +")");
  cell.appendChild(replyLink);
  infoRow.appendChild(cell);
  //---end of info row

  //--- reply row: reserved space for reply reply box
  cell = document.createElement("td"); //empty space
  replyRow.appendChild(cell);
  cell = document.createElement("td"); //empty space
  cell.id = commentObject.id + "replyCell";
  cell.rowSpan = "2";
  replyRow.appendChild(cell);
  //---end of reply row

  //finishing touches
  tblBody.appendChild(topRow);
  tblBody.appendChild(bodyRow);
  tblBody.appendChild(infoRow);
  tblBody.appendChild(replyRow);

  messageTable.appendChild(tblBody);
  newP.appendChild(messageTable);
  parentObject.appendChild(newP);
}

function clear_messages() {
  document.getElementById("comments").innerHTML = "";
}

function delete_messages() {
  //to be filled for API to delete all messages on this page
}

function refresh_messages() {
  clear_messages();
  fetchComments();
}

function show_embedVideo(videoID) {
  hide_embedVideo();
  var frame = document.createElement('iframe');
  frame.setAttribute('type', 'text/html');
  frame.setAttribute('width', '800');
  frame.setAttribute('height', '450');
  frame.setAttribute('src', videoID || 'https://www.youtube.com/embed/jNQXAC9IVRw');
  document.getElementById("video").appendChild(frame);
}

function hide_embedVideo() {
  document.getElementById("video").innerHTML = "";
}

function showVideoInfo() {
  var parent = document.getElementById("videoInfo");
  var request = new XMLHttpRequest();
  var url = 'https://youcmt.com/api/video/info?vid=' + getMeta("videoID");
  request.open('GET', url, true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var table = document.createElement("table");
      var cell = document.createElement("td");

      var titleRow = document.createElement("tr");
      cell = document.createElement("td");
      cell.appendChild(document.createTextNode("Title"));
      titleRow.appendChild(cell);
      cell = document.createElement("td");
      cell.appendChild(document.createTextNode(JSON.parse(this.response).title));
      titleRow.appendChild(cell);

      var dateRow = document.createElement("tr");
      cell = document.createElement("td");
      cell.appendChild(document.createTextNode("Date"));
      dateRow.appendChild(cell);
      cell = document.createElement("td");
      cell.appendChild(document.createTextNode(JSON.parse(this.response).date));
      dateRow.appendChild(cell);

      var authorRow = document.createElement("tr");
      cell = document.createElement("td");
      cell.appendChild(document.createTextNode("Author"));
      authorRow.appendChild(cell);
      cell = document.createElement("td");
      cell.appendChild(document.createTextNode(JSON.parse(this.response).author));
      authorRow.appendChild(cell);

      var descriptionRow = document.createElement("tr");
      descriptionRow.id = "descriptionRow";
      cell = document.createElement("td");
      cell.appendChild(document.createTextNode("Description"));
      descriptionRow.appendChild(cell);
      cell = document.createElement("td");
      cell.appendChild(document.createTextNode(JSON.parse(this.response).description));
      cell.id = "descriptionCell";
      cell.style.display = "none";
      descriptionRow.appendChild(cell);

      var showhideRow = document.createElement("tr");
      showhideRow.setAttribute("onclick", "showhideDescription()");
      cell = document.createElement("td");
      showhideRow.appendChild(cell);
      cell = document.createElement("td");
      var td = document.createTextNode("Show Description");
      cell.appendChild(td);
      cell.id = "SHDCell";
      showhideRow.appendChild(cell);

      table.appendChild(titleRow);
      table.appendChild(dateRow);
      table.appendChild(authorRow);
      table.appendChild(descriptionRow);
      table.appendChild(showhideRow);

      parent.appendChild(table);
    } else {
      parent.appendChild(document.createTextNode("Unable to fetch video info"));
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };

  request.send();
}

function showhideDescription() {
  if (document.getElementById("SHDCell").innerHTML == "Show Description") {
    //currently hiden, show
    document.getElementById("descriptionCell").style.display = "inline";
    document.getElementById("SHDCell").innerHTML = "Hide Description";
  } else {
    //currently visible, hide
    document.getElementById("descriptionCell").style.display = "none";
    document.getElementById("SHDCell").innerHTML = "Show Description";
  }
}

function showCommentBox() {
  //todo: put all elements into table/grid for better allignment
  //include profile picture
  document.getElementById("write").innerHTML = ""; //clear
  var activeComment = document.createElement("textarea");
  activeComment.id = "commentTextArea";
  activeComment.setAttribute('rows','5');
  activeComment.style.resize = 'none';
  activeComment.style.width = document.getElementById("content").style.width;
  document.getElementById("write").appendChild(activeComment);

  var cancelButton = document.createElement("button");
  cancelButton.appendChild(document.createTextNode("Cancel"));
  cancelButton.setAttribute('onclick','hideCommentBox()');
  cancelButton.style.float = "right";
  var submitButton = document.createElement("button");
  submitButton.appendChild(document.createTextNode("Submit"));
  submitButton.setAttribute('onclick','submitComment()');
  submitButton.style.float = "right";

  document.getElementById("write").appendChild(document.createElement("br"));
  document.getElementById("write").appendChild(submitButton);
  document.getElementById("write").appendChild(cancelButton);
}

function showReplyBox(parentCommentID) {
  var parent = document.getElementById(parentCommentID + "replyCell");
  if (parent == null) {
    console.log("Unexpected Error! " + parentCommentID + " does not exist in this page");
    return;
  }
  var activeComment = document.createElement("textarea");
  activeComment.id = parentCommentID + "replyInputBox";
  activeComment.setAttribute('rows','5');
  activeComment.style.resize = 'none';
  activeComment.style.width = document.getElementById("content").style.width;
  parent.appendChild(activeComment);

  var cancelButton = document.createElement("button");
  cancelButton.appendChild(document.createTextNode("Cancel"));
  cancelButton.setAttribute('onclick','hideReplyBox(' + parentCommentID + ')');
  cancelButton.style.float = "right";
  var submitButton = document.createElement("button");
  submitButton.appendChild(document.createTextNode("Submit"));
  submitButton.setAttribute('onclick','submitReply(' + parentCommentID + ')');
  submitButton.style.float = "right";

  parent.appendChild(document.createElement("br"));
  parent.appendChild(submitButton);
  parent.appendChild(cancelButton);
}

function hideCommentBox() {
  document.getElementById("write").innerHTML = ""; //clear
  var commentTrigger = document.createElement("a");
  commentTrigger.appendChild(document.createTextNode("Add your comment here..."));
  //commentTrigger.href = "#comment";
  commentTrigger.setAttribute("onclick", "showCommentBox()");
  document.getElementById("write").appendChild(commentTrigger);
}

function hideReplyBox(parentCommentID) {
  var parent = document.getElementById(parentCommentID + "replyCell");
  parent.innerHTML = "";
}

function submitComment() {
  var commentText = document.getElementById("commentTextArea").value;
  if (!commentText) {
    alert("Comment cannot be empty");
    return;
  }
  var url = "https://youcmt.com/api/comment";
  var userID = 2; //or current logged in user
  var vidID = getMeta("videoID");

  var data = new FormData();
  data.append('user_id', userID);
  data.append('text', commentText);
  data.append('vid', vidID);

  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      console.log("Request success");
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.send(data);

  hideCommentBox();
  refresh_messages();
}

function submitReply(parentCommentID) {
  console.log("Posting reply to " + parentCommentID);
  var commentText = document.getElementById(parentCommentID + "replyInputBox").value;

  if (!commentText) {
    alert("Comment cannot be empty");
    return;
  }
  var url = "https://youcmt.com/api/comment";
  var userID = 2; //or current logged in user

  var data = new FormData();
  data.append('user_id', userID);
  data.append('text', commentText);
  data.append('parent_comment_id', parentCommentID);

  var request = new XMLHttpRequest();
  request.open('POST', url, true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      console.log("Request success");
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.send(data);

  hideReplyBox(parentCommentID);
  refresh_messages();
}

function fetchComments() {
  var videoID = getMeta("videoID");
  var url = "https://youcmt.com/api/comment?vid=" + videoID;
  var request = new XMLHttpRequest();
  request.open('GET', url, true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var commentsArray = JSON.parse(this.response).comments;
      console.log(JSON.stringify(commentsArray));
      for (i = commentsArray.length - 1; i >= 0 ; i--) { //in reverse-chrono order
        console.log(commentsArray[i]);
        insert_fetchedComment(commentsArray[i]);
      }
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send();
}

function fetchReplies(topCommentID) {
  var repliesCell = document.getElementById(topCommentID + "fetchRepliesCell");
  repliesCell.innerHTML = "";
  var url = "https://youcmt.com/api/comment?top_comment_id=" + topCommentID;
  var request = new XMLHttpRequest();
  request.open('GET', url, true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var commentsArray = JSON.parse(this.response).comments;
      console.log(JSON.stringify(commentsArray));
      for (i = 0; i < commentsArray.length ; i++) { //in chrono order
        console.log(commentsArray[i]);
        insert_fetchedReply(commentsArray[i], repliesCell); //comment object, parent object (the cell)
      }
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send();
}

function getMeta(name) {
  var metas = document.getElementsByTagName('meta');
  for (var i=0; i < metas.length; i++) {
      if (metas[i].getAttribute("name") == name) {
         return metas[i].getAttribute("content");
      }
   }
   return "";
}
