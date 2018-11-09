function translateDateTime(commentTime) {
  var ct = new Date(commentTime);
  var st = new Date(currentTime);
  var diff = (st-ct)/60000; //1 min = 60 000 ms

  if (diff < 1) {
    return "Just now";
  } else if (diff < 2) {
    return "1 minute ago";
  } else if (diff < 60) {
    return Math.floor(diff) + " minutes ago";
  } else {
    diff /= 60; //diff is now in hours
    if (diff < 2) {
      return "1 hour ago";
    } else if (diff < 24) {
      return Math.floor(diff) + " hours ago";
    } else {
      diff /= 24; //diff is now in days
      if (diff < 2) {
        return " 1 day ago";
      } else if (diff < 30) {
        return Math.floor(diff) + " days ago";
      } else {
        diff /= 30.42; //diff is now in months
        if (diff < 2) {
          return "1 month ago";
        } else if (diff < 12) {
          return Math.floor(diff) + " months ago";
        } else {
          diff /= 12; //diff is now in years
          if (diff < 2) {
            return "1 year ago";
          } else {
            return Math.floor(diff) + " years ago";
          }
        }
      }
    }
  }
  return ct;
}

function getMeta(name) { //helper function to get any meta info based on tag
  var metas = document.getElementsByTagName('meta');
  for (var i=0; i < metas.length; i++) {
      if (metas[i].getAttribute("name") == name) {
         return metas[i].getAttribute("content");
      }
   }
   return "";
}

function validUsername(un) { //guards for username here
  if (un == null || un.length == 0) {
    return false;
  }
  return true;
}

function validEmail(email) { //guards for email here
  var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(String(email).toLowerCase());
}

function validPassword(pw) { //guards for password here
  return pw.length >= 6;
}

function getDOM(id) {
  return document.getElementById(id);
}

function createButton(buttonText, styleClass, functionName) {
  var button = document.createElement("button");
  button.className = styleClass;
  button.appendChild(document.createTextNode(buttonText));
  button.setAttribute("onclick", functionName);
  return button;
}

function createInput(inputType, placeholderText, styleClass) {
  var input = document.createElement("input");
  input.setAttribute("type", inputType);
  if (placeholderText != "") {
    input.setAttribute("placeholder", placeholderText);
  }
  input.className = styleClass;
  return input;
}

function createLink(linkText, linkClass, functionName, href) { //a tag
  var link = document.createElement("a");
  link.innerHTML = linkText;
  link.className = linkClass;
  link.setAttribute("onclick", functionName);
  if (href != "") {
    link.setAttribute("href", href);
  }
  return link;
}

function createText(text) {
  return document.createTextNode(text);
}

function submitComment(parentID) {
  if (authToken == null || authToken == "") {
    submitGuestComment();
  } else {
    submitUserComment();
  }
}

function findReplyArray(id) {
  for (i = 0; i < rootCommentsArray.length; i++) {
    if (rootCommentsArray[i].id == id) {
      return rootCommentsArray[i].replies;
    }
  }
  console.log("Trying to find a reply array that does not exist! " + id);
  return null;
}
