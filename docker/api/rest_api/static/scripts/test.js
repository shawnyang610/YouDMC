function setup() {
  document.getElementById("jsStatus").innerHTML="JS running"; //indicate js is working
}

function insertTimeStamp() {
  var newP = document.createElement("p");
  newP.appendChild(document.createTextNode(Date()));
  document.getElementById("contents").appendChild(newP);
}

function insertMessage(newMessage) {
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
  document.getElementById("contents").appendChild(newP);
}
/**
function generateRandomMessage() {
  var m = new Object();
  m.parentMessage = null;
  m.user = "Sample User";
  m.userProfilePicture = ;
  m.body = "Generic message body";
  return m;
}
*/
