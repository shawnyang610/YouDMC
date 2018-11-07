var debugDivID = "debug";

function fillDebugButtons() {
  var refreshButton = document.createElement("button");
  refreshButton.appendChild(document.createTextNode("Refresh Messages"));
  refreshButton.setAttribute("onclick","refreshMessages()");

  var logTokenButton = document.createElement("button");
  logTokenButton.appendChild(document.createTextNode("Log Auth Token"));
  logTokenButton.setAttribute("onclick","logAuthToken()");

  var pageInfo = document.createElement("p");
  pageInfo.innerHTML = "Current Page = " + videoID;
  var authInfo = document.createElement("p");
  authInfo.innerHTML = "AuthToken = " + authToken;

  var debugDiv = document.getElementById(debugDivID);
  debugDiv.appendChild(refreshButton);
  debugDiv.appendChild(logTokenButton);

  debugDiv.appendChild(pageInfo);
  debugDiv.appendChild(authInfo);
}

function logAuthToken() {
  if (authToken == null || authToken == "") {
    console.log("No auth token present");
  } else {
    console.log(authToken);
  }  
}
