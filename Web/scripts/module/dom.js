function createDiv(styleClass) {
  var div = document.createElement("div");
  div.className = styleClass;
  return div;
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

function createP(text, style) {
  var p = document.createElement("p");
  p.innerHTML = text;
  p.className = style;
  return p;
}

function createText(text) {
  return document.createTextNode(text);
}

function getDOM(id) {
  return document.getElementById(id);
}
