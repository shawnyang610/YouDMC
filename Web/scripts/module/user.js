function showSidePanel() {
  var left = getDOM("content");
  var right = getDOM("aside");
  left.className = "col-8";
  right.className = "col-4 bg-dark p-2";
  right.innerHTML = "";
}

function hideSidePanel() {
  var left = document.getElementById("content");
  var right = document.getElementById("aside");
  left.className = "col-12";
  right.className = "";
  right.innerHTML = "";
}

function showRegister() { //triggered by pressing the register button on header
  showSidePanel();
  var panel = document.getElementById("aside");
  var userNameInput = createInput("text", "choose a username", "form-control");
  var passwordInput = createInput("password", "choose a password (6 or more characters)", "form-control");
  var repeatPasswordInput = createInput("password", "enter your password again", "form-control");
  var emailInput = createInput("text", "enter your email address", "form-control");
  //set DOM ID so other functions can get its value
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
  if (userNameInput == null || userNameInput.length == 0) { //local guard for bad username
    fillHeaderLoggedOut(); //this will reset the username and password field
    changeHeaderStatusText("Enter Username");
    return;
  }
  if (passwordInput == null || passwordInput.length < 6) { //local guard for bad password
    getDOM("headerPWinput").value = ""; //this will reset the "bad password"
    changeHeaderStatusText("Enter a valid Password"); //this only change the status text
    return;
  }
  logInUser(updateLoginStatus);
}

function updateLoginStatus(status) {
  if (status.access_token != null) { //a token
    authToken = status.access_token;
    sessionStorage.setItem("token", authToken); //update sessionStorage
    fillHeaderLoggedIn();
    hideSidePanel();
  } else { //some error
    authToken = "";
    sessionStorage.setItem("token", authToken); //update sessionStorage
    fillHeaderLoggedOut(); //clear username & password
    showForgotPanel("Forgot your password?");
  }
}

function logOut() {
  logOutUser(fillHeaderLoggedOut);
  authToken = "";
  sessionStorage.setItem("token", authToken); //update sessionStorage
}

function showForgotPanel(statusText) {
  showSidePanel();
  var panel = document.getElementById("aside");

  var statusP = document.createElement("p");
  statusP.id = "statusText";
  statusP.className = "text-danger";
  statusP.innerHTML = statusText;

  var emailInput = createInput("text", "enter your email address", "form-control");
  //set DOM ID so other functions can get its value
  emailInput.id = "emailInput";

  //put things into input group to look better
  var inputGroup = document.createElement("div");
  inputGroup.className = "input-group mb-3";
  inputGroup.appendChild(emailInput);

  var sendButton = createButton("Send Reset Code to my Email", "btn btn-sm btn-outline-success", "sendResetLinkClicked()");

  panel.appendChild(statusP);
  panel.appendChild(inputGroup);
  panel.appendChild(sendButton);
}

function sendResetLinkClicked() { //as soon as the "send reset code" is pressed, trigger this
  var filledEmail = getDOM("emailInput").value;
  sendResetLink(filledEmail, updateForgotPanel); //call API with email, and provide the callback function
}

function updateForgotPanel(status) {
  var filledEmail = getDOM("emailInput").value;
  var panel = document.getElementById("aside");
  if (status == 404) {
    //bad email, try again (redraw everything)
    getDOM("statusText").innerHTML = "Invalid Email";
    getDOM("emailInput").value = "";
    getDOM("emailInput").focus();
  } else {
    showResetPanel(); //link sent
  }
}

function showResetPanel() {
  var filledEmail = getDOM("emailInput").value; //get the filled value before removing it
  showSidePanel();
  var panel = document.getElementById("aside");

  var statusP = document.createElement("p");
  statusP.id = "statusText";
  statusP.className = "text-danger";
  statusP.innerHTML = "Code sent. Please allow some time for the code to arrive and check your Spam Folder";

  var emailInput = createInput("text", "enter your email address", "form-control");
  //set DOM ID so other functions can get its value
  emailInput.value = filledEmail; //no need to fill this again
  emailInput.id = "emailInput";

  //put things into input group to look better
  var inputGroup = document.createElement("div");
  inputGroup.className = "input-group mb-3";
  inputGroup.appendChild(emailInput);

  var sendButton = createButton("Not Received? Send again", "btn btn-sm btn-outline-warning mb-5", "sendResetLinkClicked()");

  panel.appendChild(statusP);
  panel.appendChild(inputGroup);
  panel.appendChild(sendButton);

  //adding extra input fields for finishing the reset process
  var resetCodeInput = createInput("text", "enter the reset code", "form-control");
  var passwordInput = createInput("password", "new password", "form-control");
  var repasswordInput = createInput("password", "re-enter new password", "form-control");
  //set DOM ID so other functions can get its value
  resetCodeInput.id = "resetCodeInput";
  passwordInput.id = "passwordInput";
  repasswordInput.id = "repasswordInput";

  //put things into input group to look better
  var inputGroup1 = document.createElement("div");
  inputGroup1.className = "input-group mb-3";
  inputGroup1.appendChild(resetCodeInput);
  var inputGroup2 = document.createElement("div");
  inputGroup2.className = "input-group mb-3";
  inputGroup2.appendChild(passwordInput);
  var inputGroup3 = document.createElement("div");
  inputGroup3.className = "input-group mb-3";
  inputGroup3.appendChild(repasswordInput);

  var sendButton = createButton("Reset Password", "btn btn-sm btn-outline-success", "resetPasswordClicked()");

  panel.appendChild(inputGroup1);
  panel.appendChild(inputGroup2);
  panel.appendChild(inputGroup3);
  panel.appendChild(sendButton);

  resetCodeInput.focus();
}

function resetPasswordClicked() {
  var email = getDOM("emailInput").value;
  var code = getDOM("resetCodeInput").value;
  var p1 = getDOM("passwordInput").value;
  var p2 = getDOM("repasswordInput").value;
  if (!validPassword(p1)) {
    getDOM("statusText").innerHTML = "Password must be at least 6 characters";
    getDOM(passwordInput).focus();
  } else if (getDOM("passwordInput").value != getDOM("repasswordInput").value) {
    getDOM("statusText").innerHTML = "Re-entered password does not match with first one";
    getDOM(repasswordInput).focus();
  } else {
    resetPassword(email, code, p1, updateResetPanel);
  }
}

function updateResetPanel(status) { //callback function for reset password API (with reset code)
  if (status == 200) { //success
    hideSidePanel();
    changeHeaderStatusText("Reset Successful");
    getDOM("headerUNinput").focus();
  } else if (status == 401) { //bad reset code
    getDOM("statusText").innerHTML = "Bad reset code, please try again";
  } else { //other error
    getDOM("statusText").innerHTML = "Could not reset your password, please check all information and try again";
  }
}
