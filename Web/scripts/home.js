function loadHomePage() {
  var content = getDOM("content");
  content.className = "allign-center";
  var homeTitle = document.createElement("h3");
    homeTitle.appendChild(createText("Welcome to YouCMT.com!"))
  content.innerHTML = "";
  content.appendChild(homeTitle);
  content.appendChild(getMissionText());
  API_getServerTime(API_getHotVideos, fillHotVideos);
}

function fillHotVideos(response) {
  if (response == null || response.length == 0) {
    console.log("Empty response");
    return;
  }
  var array = response.message;
  if (array == null || array.length == 0) { //in case unable to get
    return;
  }
  var content = getDOM("content");
  content.appendChild(createP("Currently Discussed", "h4 font-italic"));
  for (i = 0; i < array.length; i++) {
    content.appendChild(hotVideoLinkDiv(array[i]));
  }
}

function hotVideoLinkDiv(msgObject) {
  var div = document.createElement("div");
  div.className = "mb-2";
  var dateText = createText(translateDateTime(msgObject.date));
  var titleText = msgObject.title;
  var vidLink = createLink(titleText, "", "", "https://www.youcmt.com/watch?v=" + msgObject.vid);

  div.appendChild(dateText);
  div.appendChild(document.createElement("br"));
  div.appendChild(vidLink);

  return div;
}

function getMissionText() {
  var div = document.createElement("div");
  div.className = "mt-3 mb-3";
  div.innerHTML = `
    Here we support freedom of speech.
    We believe that comments should not be disabled nor deleted.
    This is why we created this site, we hope you enjoy it!`;
  return div;
}
