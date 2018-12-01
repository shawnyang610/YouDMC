function fillNavBarLoggedOut() {
  var navBar = getDOM("navBar");
  navBar.innerHTML = "";
  navBar.appendChild(createLogo());
  var rightDiv = document.createElement("div")
    rightDiv.appendChild(createNBList());
    rightDiv.appendChild(createLoginForm());
  navBar.appendChild(rightDiv);
}

function fillNavBarLoggedIn() {
  var navBar = getDOM("navBar");
  navBar.innerHTML = "";
  navBar.appendChild(createLogo());
  var rightDiv = document.createElement("div")
    rightDiv.appendChild(createNBList());
    rightDiv.appendChild(createAccountForm());
  navBar.appendChild(rightDiv);
}

function createLogo() {
  var logo = document.createElement("a");
  logo.className = "navbar-brand mr-auto";
  logo.href = "https://www.YouCMT.com";
  var logo_img = document.createElement("img");
  logo_img.src = getMeta("staticResourcePath") + "images/logo.png";
  logo_img.height = 30;
  logo.appendChild(logo_img);
  return logo;
}

function createNBList() { //empty list for navbar allignment to work properly
  var list = document.createElement("ul");
  list.className = "navbar-nav mr-auto";
  return list;
}

function createLoginForm() {
  var form = document.createElement("div"); //to prevent navigations
  form.className = "form-inline my-2 my-md-0";
  var inputGroup = createDiv("input-group input-group-sm");
    var usernameInput = createInput("text", "username", "form-control");
    usernameInput.id = "navBarUsernameInput";
    inputGroup.appendChild(usernameInput);
    var passwordInput = createInput("password", "password", "form-control");
    passwordInput.id = "navBarPasswordInput";
    inputGroup.appendChild(passwordInput);
    inputGroup.appendChild(createButton("Login", "btn btn-sm btn-outline-success", "navBarLoginClicked()"));
  form.appendChild(inputGroup);
  form.appendChild(createButton("Register", "btn btn-sm btn-primary my-2 my-sm-0", "navBarRegisterClicked()"));
  return form;
}

function createAccountForm() {
  var form = document.createElement("div"); //to prevent navigations
  form.className = "form-inline my-2 my-md-0";
  form.appendChild(createGreetingText());
  form.appendChild(createAccountButton());
  form.appendChild(createLogoutButton());
  return form;
}

function createGreetingText() {
  var greetingTextNode = document.createElement("span");
  greetingTextNode.className = "navbar-text mr-2";
  greetingTextNode.innerHTML = "Greetings, " + userCookie.uname;
  return greetingTextNode;
}

function createAccountButton() {
  var btnGrp = createDiv("dropdown");
  var dropButton = document.createElement("button");
  dropButton.setAttribute("class", "btn btn-sm btn-secondary dropdown-toggle");
  dropButton.setAttribute("type", "button");
  dropButton.setAttribute("data-toggle", "dropdown");
  dropButton.innerHTML = "Account";
  var options = createDiv("dropdown-menu");
    var link0 = createLink("Change Avatar", "dropdown-item", "sidePanelChangeAvatar()", "");
    var link1 = createLink("Change Password", "dropdown-item", "sidePanelChangePassword()", "");
    var link2 = createLink("Other Settings", "dropdown-item", "console.log(\"Not yet supported\")", "");
    options.appendChild(link0);
    options.appendChild(link1);
    options.appendChild(link2);
  btnGrp.appendChild(dropButton);
  btnGrp.appendChild(options);
  return btnGrp;
}

function createLogoutButton() {
  var logButton = createButton("Log Out", "btn btn-sm btn-danger my-2 my-sm-0", "navBarLogoutClicked()");
  return logButton;
}

//==============================================================================
//actions=======================================================================
//==============================================================================

function navBarLoginClicked() {
  var username = getDOM("navBarUsernameInput").value;
  var password = getDOM("navBarPasswordInput").value;
  if (username == "") { //guard against empty username input
    getDOM("navBarUsernameInput").focus();
    return;
  }
  if (password.length < 6) { //guard against obvious bad password input
    getDOM("navBarPasswordInput").value = "";
    getDOM("navBarPasswordInput").focus();
    return;
  }
  API_logInUser(username, password, navBarLoginUpdate);
}

function navBarRegisterClicked() {
  showRegister(); //in sidePanel.js
}

function navBarLogoutClicked() {
  resetSession();
  fillNavBarLoggedOut();
}

//==============================================================================
//call backs====================================================================
//==============================================================================

function navBarLoginUpdate(response) { //callback function for login API call
  if (response == "Invalid Username / Password") {
    showForgotPanel("Forgot your password?"); //in sidePanel.js
  } else if (response.message == "Succesfully logged in") {
    setSessionStorage(response);
    fillNavBarLoggedIn();
  } else {
    console.log("Unexpected response when logging in");
  }
}
