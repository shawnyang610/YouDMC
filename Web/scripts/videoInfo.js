// All the comment page contents except for the actual comments
function showEmbedVideo() {
  var embedDiv = getDOM("videoEmbed");
  var video = document.createElement("iframe");
  video.setAttribute("type", "text/html");
  video.setAttribute("width", "640");
  video.setAttribute("height", "360");
  video.setAttribute("allow", "encrypted-media");
  video.setAttribute("src", "http://www.youtube.com/embed/" + getMeta("videoID"));
  video.innerHTML = "video";
  embedDiv.appendChild(video);
}

function displayVideoInfo() {
  var videoInfoDiv = document.getElementById("videoInfo");
  videoInfoDiv.className = "mt-3"; //get rid of spinning circle
  if (videoInfo == null || videoInfo.message != null) {
    //videoInfoDiv.className = "bg-info"
    videoInfoDiv.innerHTML = "Unable to fetch video info";
  } else { //successfully got info: render to page
    var table = document.createElement("table");
    var cell = document.createElement("td");

    var titleRow = document.createElement("tr");
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode("Title"));
    titleRow.appendChild(cell);
    cell = document.createElement("td");
    cell.appendChild(createLink(videoInfo.title, "text-bold", "", "https://www.youtube.com/watch?v=" + getMeta("videoID")));
    titleRow.appendChild(cell);

    var dateRow = document.createElement("tr");
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode("Date"));
    dateRow.appendChild(cell);
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode(videoInfo.date));
    dateRow.appendChild(cell);

    var authorRow = document.createElement("tr");
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode("Author"));
    authorRow.appendChild(cell);
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode(videoInfo.author));
    authorRow.appendChild(cell);

    var descriptionRow = document.createElement("tr");
    descriptionRow.id = "descriptionRow";
    cell = document.createElement("td");
    cell.appendChild(document.createTextNode("Description"));
    descriptionRow.appendChild(cell);
    cell = document.createElement("td");
    cell.id = "descriptionCell";
    //this will either contain the description or the "show description" trigger
    descriptionRow.appendChild(cell);

    var hideRow = document.createElement("tr");
    //this will always have the "hide description" trigger, but could be hidden
    hideRow.id = "hideRow";
    cell = document.createElement("td");
      hideRow.appendChild(cell); //empty cell for indent
    cell = document.createElement("td");
      var hideTrigger = document.createElement("button");
      hideTrigger.className = "btn btn-sm btn-outline-secondary";
      hideTrigger.appendChild(document.createTextNode("Hide Description"));
      hideTrigger.setAttribute("onclick", "hideDescription()");
      cell.appendChild(hideTrigger);
      hideRow.appendChild(cell);

    table.appendChild(titleRow);
    table.appendChild(dateRow);
    table.appendChild(authorRow);
    table.appendChild(descriptionRow);
    table.appendChild(hideRow);

    videoInfoDiv.appendChild(table);
    hideDescription();
  }
}

function showDescription() {
  //clicked on show description trigger ->
  //change cell text to description and show "hide row"
  var discriptionCell = document.getElementById("descriptionCell");
  discriptionCell.innerHTML = videoInfo.description;
  var hideRow = document.getElementById("hideRow");
  //show the hide row
  hideRow.style.display = "";
}

function hideDescription() {
  //clicked on hide description trigger ->
  //change cell text to trigger and hide "hide row"
  var discriptionCell = document.getElementById("descriptionCell");
  discriptionCell.innerHTML = "";
  var showTrigger = document.createElement("button");
  showTrigger.className = "btn btn-sm btn-outline-secondary";
  showTrigger.appendChild(document.createTextNode("Show Description"));
  showTrigger.setAttribute("onclick", "showDescription()");
  discriptionCell.appendChild(showTrigger);
  var hideRow = document.getElementById("hideRow");
  //hide the hide row
  hideRow.style.display = "none";
}
