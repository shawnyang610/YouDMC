function fillHeaderLoggedIn() {
  var headerDiv = document.getElementById("header-right");
  headerDiv.innerHTML = "";
  var form = document.createElement("form");
  var table = document.createElement("table");

  var labelRow = document.createElement("tr");

  var usernameCell = document.createElement("td");
  usernameCell.appendChild(createP("Hi, " + myUsername, "mt-2"));
  usernameCell.setAttribute('rowspan', '2');

  var usericonCell = document.createElement("td");
  var img = document.createElement("img");
    img.className = "m-2"
    img.width = 30;
    img.src = getMeta("staticResourcePath") +
      "images/profile" + myPIC + ".png";
    img.alt = "myProfilePic";
  usericonCell.appendChild(img);
  usericonCell.setAttribute('rowspan', '2');

  var acctCell = document.createElement("td");
  var acctButton = createButton("Account", "btn btn-lg btn-outline-primary", "showAccountPanel()");
  acctCell.appendChild(acctButton);
  acctCell.setAttribute('rowspan', '2');

  var logoCell = document.createElement("td");
  var logoButton = createButton("Log out", "btn btn-lg btn-outline-danger", "logOut()");
  logoCell.appendChild(logoButton);
  logoCell.setAttribute('rowspan', '2');

  labelRow.appendChild(usernameCell);
  labelRow.appendChild(usericonCell);
  labelRow.appendChild(acctCell);
  labelRow.appendChild(logoCell);

  table.appendChild(labelRow);

  form.appendChild(table);
  headerDiv.appendChild(form);
}

function fillHeaderLoggedOut(statusText) {
  var headerDiv = document.getElementById("header-right");
  headerDiv.innerHTML = "";

  var inputGroupDiv = document.createElement("div");
  inputGroupDiv.className = "input-group";
  var userNameInput = createInput("text", "username", "form-control");
  userNameInput.id = "headerUNinput";
  var passwordInput = createInput("password", "password", "form-control");
  passwordInput.id = "headerPWinput";
  var loginButton = createButton("Log In", "input-group-append btn", "logIn()");

  inputGroupDiv.appendChild(userNameInput);
  inputGroupDiv.appendChild(passwordInput);
  inputGroupDiv.appendChild(loginButton);

  var registerButton = createButton("Register", "btn btn-warning ml-2", "showRegister()");
  inputGroupDiv.appendChild(registerButton);

  headerDiv.appendChild(inputGroupDiv);

  var statusP = document.createElement("p");
  statusP.className = "text-danger";
  statusP.id = "headerStatusText";
  statusP.style.display = "none"; //hide by default
  headerDiv.appendChild(statusP);
}

function fillNavBarLoggedOut() {

}

function changeHeaderStatusText(newText) {
  var statusP = getDOM("headerStatusText");
  if (newText != null && newText != "") {
    statusP.style.display = "inline";
    statusP.innerHTML = newText;
  } else {
    statusP.style.display = "none";
    statusP.innerHTML = "";
  }
}
