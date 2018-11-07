function showSidePanel() {
  var left = document.getElementById("content");
  var right = document.getElementById("aside");
  left.className = "col-8 bg-success";
  right.className = "col-4 bg-dark p-2";
  right.innerHTML = "";
}

function hideSidePanel() {
  var left = document.getElementById("content");
  var right = document.getElementById("aside");
  left.className = "col-12 bg-success";
  right.className = "";
  right.innerHTML = "";
}

function showRegister() { //triggered by pressing the register button on header
  showSidePanel();
  var panel = document.getElementById("aside");
  var userNameInput = document.createElement("input");
  userNameInput.className = "form-control";
  userNameInput.setAttribute('type', 'text');
  userNameInput.setAttribute('name', 'username');
  userNameInput.setAttribute("placeholder","choose a username");

  var passwordInput = document.createElement("input");
  passwordInput.className = "form-control";
  passwordInput.setAttribute('type', 'password');
  passwordInput.setAttribute('name', 'password');
  passwordInput.setAttribute("placeholder","choose a password (6 or more characters)");

  var repeatPasswordInput = document.createElement("input");
  repeatPasswordInput.className = "form-control";
  repeatPasswordInput.setAttribute('type', 'password');
  repeatPasswordInput.setAttribute('name', 'repeatpassword');
  repeatPasswordInput.setAttribute("placeholder","enter your password again");

  var emailInput = document.createElement("input");
  emailInput.className = "form-control";
  emailInput.setAttribute('type', 'text');
  emailInput.setAttribute('name', 'email');
  emailInput.setAttribute("placeholder","enter your email address");

  userNameInput.id = "userNameInput";
  passwordInput.id = "passwordInput";
  repeatPasswordInput.id = "repeatPasswordInput";
  emailInput.id = "emailInput";

  //put things into input group to look better
  var inputGroup0 = document.createElement("div");
  inputGroup0.className = "input-group mb-3";
  inputGroup0.appendChild(userNameInput);
  var inputGroup1 = document.createElement("div");
  inputGroup1.className = "input-group mb-3";
  inputGroup1.appendChild(passwordInput);
  var inputGroup2 = document.createElement("div");
  inputGroup2.className = "input-group mb-3";
  inputGroup2.appendChild(repeatPasswordInput);
  var inputGroup3 = document.createElement("div");
  inputGroup3.className = "input-group mb-3";
  inputGroup3.appendChild(emailInput);

  panel.appendChild(inputGroup0);
  panel.appendChild(inputGroup1);
  panel.appendChild(inputGroup2);
  panel.appendChild(inputGroup3);

  var statusText = document.createElement("p");
  statusText.id = "statusText";
  statusText.className = "text-danger";

  var cancelButton = document.createElement("button");
  cancelButton.className = "btn btn-sm btn-outline-danger";
  cancelButton.appendChild(document.createTextNode("Cancel"));
  cancelButton.setAttribute("onclick", "hideSidePanel()");
  var submitButton = document.createElement("button");
  submitButton.className = "btn btn-sm btn-outline-success";
  submitButton.appendChild(document.createTextNode("Submit"));
  submitButton.setAttribute("onclick", "submitRegistration()");

  panel.appendChild(statusText);
  panel.appendChild(cancelButton);
  panel.appendChild(submitButton);
}

function submitRegistration() {
  var statusText = document.getElementById("statusText");
  var userNameInput = document.getElementById("userNameInput").value;
  var passwordInput = document.getElementById("passwordInput").value;
  var repeatPasswordInput = document.getElementById("repeatPasswordInput").value;
  var emailInput = document.getElementById("emailInput").value;

  if (!validUsername(userNameInput)) {
    statusText.innerHTML = "Not a valid username. Must contain only alpha-numeric.";
    return;
  }

  if (passwordInput.length < 6) {
    statusText.innerHTML = "Password must be at least 6 characters";
    return;
  }

  if (passwordInput != repeatPasswordInput) {
    statusText.innerHTML = "Two passwords must match";
    return;
  }

  if (!validEmail(emailInput)) {
    statusText.innerHTML = "Not a valid email";
    return;
  }

  statusText.innerHTML = "";

  registerUser(updateRegistrationStatus);
}

function updateRegistrationStatus(status) {
  console.log(status);
  if (status.message == "user registered!") { //in business
    authToken = status.access_token;
    sessionStorage.setItem("token", authToken); //update sessionStorage
    //update UI to logged in UI
    fillHeaderLoggedIn();
    hideSidePanel();
  } else {
    //the status IS the message
    statusText.innerHTML = status;
  }
}

function logIn() { //this is triggered from header button
  var userNameInput = document.getElementById("headerUNinput").value;
  var passwordInput = document.getElementById("headerPWinput").value;
  if (userNameInput == null || userNameInput.length == 0) {
    fillHeaderLoggedOut("Enter Username");
    return;
  }
  if (passwordInput == null || passwordInput.length == 0) {
    fillHeaderLoggedOut("Enter Password");
    return;
  }
  logInUser(updateLoginStatus);
  console.log("Finished user.js login()");
}

function updateLoginStatus(status) {
  console.log("user137");
  console.log(status);
  if (status.access_token != null) { //a token
    authToken = status.access_token;
    sessionStorage.setItem("token", authToken); //update sessionStorage
    fillHeaderLoggedIn();
    hideSidePanel();
  } else { //some error
    authToken = "";
    sessionStorage.setItem("token", authToken); //update sessionStorage
    fillHeaderLoggedOut(status);
  }
}

function logOut() {
  //console.log("Trying to log out");
  logOutUser(fillHeaderLoggedOut);
  authToken = "";
  sessionStorage.setItem("token", authToken); //update sessionStorage
}
