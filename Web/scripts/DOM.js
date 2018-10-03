function setup() {
  fill_header_loggedOut();
  hideCommentBox();
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

function insert_randomMessage() {
  var random = Math.floor(Math.random() * 5);
  var verse = Math.floor(Math.random() * 5);
  var messages = [
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin imperdiet consequat semper. Ut enim nulla, placerat quis aliquam vitae, aliquam nec arcu. Quisque sit amet urna quis est elementum malesuada eget a urna. Donec imperdiet sapien nibh. Morbi dictum, diam ut porta porttitor, lectus ex pulvinar purus, a viverra nisi magna ut diam.",
    "Sed in posuere lacus. Donec tristique id augue a aliquet. Ut nec elit bibendum, tristique dolor vitae, egestas neque. Sed efficitur purus at porttitor cursus. Duis posuere ullamcorper erat vel pharetra. Cras eu mi a diam tincidunt tincidunt nec quis odio. Nam turpis nibh, vehicula non vehicula sollicitudin, fringilla sit amet leo. In a iaculis nunc. Donec sollicitudin sagittis magna, id luctus ante efficitur quis. Curabitur cursus aliquam arcu. Nulla risus diam, convallis ac tempor vitae, rutrum ac libero.",
    "Donec dapibus arcu quis leo condimentum porttitor. Donec eleifend ut sem quis iaculis. Nullam sed dui justo. Sed eget mauris sapien. Nunc iaculis dui dolor, sed feugiat felis bibendum id. Nullam maximus risus mauris, et vestibulum nulla condimentum a. Quisque pulvinar auctor venenatis. Aenean leo sem, euismod et nunc sit amet, venenatis dapibus massa.",
    "Nullam sollicitudin libero non nulla eleifend, ac iaculis eros porta. Donec sit amet aliquet sem. Integer at luctus sapien. Ut feugiat eu metus in blandit. Phasellus vitae turpis nibh. Integer vitae ligula ut purus blandit posuere et vel felis. Praesent et viverra arcu. Phasellus elit dui, aliquet id elementum ut, bibendum eu nunc. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur vitae turpis non nisi auctor pretium sit amet sit amet orci. Vivamus egestas ligula sed lacinia rhoncus.",
    "Donec et felis nec arcu egestas dictum. Fusce pretium mauris ipsum, et elementum leo ornare at. Curabitur vehicula mi orci, eu finibus magna luctus non. Maecenas eu magna vitae turpis tempor viverra. Donec eget mollis ex. Sed lectus nisl, maximus ac ante in, maximus ultricies tellus. Donec eu nisi non quam gravida volutpat. Vivamus volutpat feugiat porttitor. Duis at neque at neque viverra ullamcorper."
  ];


  var newP = document.createElement("p");
  newP.style.border = "solid #00ffff";

  var messageTable = document.createElement("TABLE");
  var tblBody = document.createElement("tbody");
  var topRow = document.createElement("tr");
  var botRow = document.createElement("tr");

  var cell, cellText;
  cell = document.createElement("td"); //Profile Picture
  var profilePic = document.createElement("img");
  profilePic.setAttribute("src", "images/profile" + random + ".png");
  profilePic.setAttribute("width", "75");
  cell.rowSpan = "2";
  cell.appendChild(profilePic);
  topRow.appendChild(cell);

  cell = document.createElement("td"); //Message User
  cellText = document.createTextNode("User" + random);
  cell.appendChild(cellText);
  topRow.appendChild(cell);

  cell = document.createElement("td"); //Message Time
  cellText = document.createTextNode(Date());
  cell.appendChild(cellText);
  topRow.appendChild(cell);

  cell = document.createElement("td"); //Message Content
  cellText = document.createTextNode(messages[verse]);
  cell.colSpan = "2";
  cell.appendChild(cellText);
  botRow.appendChild(cell);

  tblBody.appendChild(topRow);
  tblBody.appendChild(botRow);

  messageTable.appendChild(tblBody);
  newP.appendChild(messageTable);
  document.getElementById("comments").appendChild(newP);
}

function clear_messages() {
  document.getElementById("comments").innerHTML = "";
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

function showCommentBox() {
  document.getElementById("write").innerHTML = "";
  document.getElementById("write").innerHTML = "[Comment Box is here]";
}

function hideCommentBox() {
  document.getElementById("write").innerHTML = "";
  var commentTrigger = document.createElement("a");
  commentTrigger.appendChild(document.createTextNode("Add your comment here..."));
  commentTrigger.href = "#comment";
  commentTrigger.setAttribute("onclick", "showCommentBox()");
  document.getElementById("write").appendChild(commentTrigger);
}

function submitComment() {

}
