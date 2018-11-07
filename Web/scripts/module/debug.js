var debugDivID = "debug";

function fillDebugButtons() {
  var refreshButton = document.createElement("button");
  refreshButton.appendChild(document.createTextNode("Refresh Messages"));
  refreshButton.setAttribute("onclick","refreshMessages()");

  var showSideButton = document.createElement("button");
  showSideButton.appendChild(document.createTextNode("Show Side Panel"));
  showSideButton.setAttribute("onclick","showSidePanel()");

  var hideSideButton = document.createElement("button");
  hideSideButton.appendChild(document.createTextNode("Hide Side Panel"));
  hideSideButton.setAttribute("onclick","hideSidePanel()");

  var pageInfo = document.createElement("p");
  pageInfo.innerHTML = "Current Page = " + videoID;
  var authInfo = document.createElement("p");
  authInfo.innerHTML = "AuthToken = " + authToken;

  var debugDiv = document.getElementById(debugDivID);
  debugDiv.appendChild(refreshButton);
  debugDiv.appendChild(showSideButton);
  debugDiv.appendChild(hideSideButton);
  debugDiv.appendChild(pageInfo);
  debugDiv.appendChild(authInfo);
}
