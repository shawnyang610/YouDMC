function showSidePanel() {
  var left = getDOM("content");
  var right = getDOM("aside");
  left.className = "col-8";
  right.className = "col-4 bg-light p-2";
  right.innerHTML = "";
}

function hideSidePanel() {
  var left = document.getElementById("content");
  var right = document.getElementById("aside");
  left.className = "col-10";
  right.className = "";
  right.innerHTML = "";
}

//==============================================================================
//registration =================================================================
//==============================================================================

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

  API_registerUser(updateRegistrationStatus);
}

function updateRegistrationStatus(status) {
  //console.log(status);
  if (status.message == "user registered!") { //in business
    setSessionStorage(status);
    //update UI to logged in UI
    fillNavBarLoggedIn();
    hideSidePanel();
  } else {
    //the status IS the message
    statusText.innerHTML = status;
  }
}

//==============================================================================
//forgot password ==============================================================
//==============================================================================

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
  API_sendResetLink(filledEmail, updateForgotPanel); //call API with email, and provide the callback function
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
  var oldPassInput = createInput("text", "enter the reset code", "form-control");
  var passwordInput = createInput("password", "new password", "form-control");
  var repasswordInput = createInput("password", "re-enter new password", "form-control");
  //set DOM ID so other functions can get its value
  oldPassInput.id = "oldPassInput";
  passwordInput.id = "passwordInput";
  repasswordInput.id = "repasswordInput";

  //put things into input group to look better
  var inputGroup1 = document.createElement("div");
  inputGroup1.className = "input-group mb-3";
  inputGroup1.appendChild(oldPassInput);
  var inputGroup2 = document.createElement("div");
  inputGroup2.className = "input-group mb-3";
  inputGroup2.appendChild(passwordInput);
  var inputGroup3 = document.createElement("div");
  inputGroup3.className = "input-group mb-3";
  inputGroup3.appendChild(repasswordInput);

  var cancelButton = createButton("Cancel", "btn btn-sm btn-outline-danger", "hideSidePanel()");
  var sendButton = createButton("Reset Password", "btn btn-sm btn-outline-success", "resetPasswordClicked()");

  panel.appendChild(inputGroup1);
  panel.appendChild(inputGroup2);
  panel.appendChild(inputGroup3);
  panel.appendChild(cancelButton);
  panel.appendChild(sendButton);

  oldPassInput.focus();
}

function resetPasswordClicked() {
  var email = getDOM("emailInput").value;
  var code = getDOM("resetCodeInput").value;
  var p1 = getDOM("passwordInput").value;
  var p2 = getDOM("repasswordInput").value;
  if (code == "") {
    getDOM("statusText").innerHTML = "Please enter the code you received from your email";
    getDOM("resetCodeInput").focus();
  } else if (!validPassword(p1)) {
    getDOM("statusText").innerHTML = "Password must be at least 6 characters";
    getDOM("passwordInput").focus();
  } else if (getDOM("passwordInput").value != getDOM("repasswordInput").value) {
    getDOM("statusText").innerHTML = "Re-entered password does not match with first one";
    getDOM("repasswordInput").focus();
  } else {
    API_resetPassword(email, code, p1, updateResetPanel);
  }
}

function updateResetPanel(status) { //callback function for reset password API (with reset code)
  if (status == 200) { //success
    hideSidePanel();
    showSidePanel();
    getDOM("aside").appendChild(createP("Reset successful! Please log in again", "text-success"));
    getDOM("aside").appendChild(createButton("OK", "btn btn-success", "hideSidePanel()"));
  } else if (status == 401) { //bad reset code
    getDOM("statusText").innerHTML = "Bad reset code, please try again";
  } else { //other error
    getDOM("statusText").innerHTML = "Could not reset your password, please check all information and try again";
  }
}

//==============================================================================
//avatar change=================================================================
//==============================================================================

function sidePanelChangeAvatar(choice) {
  if (choice == null) {
    choice = userCookie.upic; //first call
  }
  showSidePanel();
  var sidePanel = getDOM("aside");
  //display your current Avatar
  var labelP = document.createElement("p");
  labelP.innerHTML = "Your current Avatar";
  labelP.className = "text-primary";
  var currentAvatar = document.createElement("img");
  currentAvatar.src = getMeta("staticResourcePath") + "images/avatars/" + choice + ".png";
  currentAvatar.setAttribute("width", "200");
  var statusP = document.createElement("p");
  statusP.id = "statusText";
  statusP.className = "text-danger";
  statusP.innerHTML = "Press confirm to submit";

  sidePanel.appendChild(labelP);
  sidePanel.appendChild(currentAvatar);
  sidePanel.appendChild(statusP);
  sidePanel.appendChild(getAvatarChoicesDiv());
  sidePanel.appendChild(createButton("Cancel", "btn btn-sm btn-outline-danger mt-3", "hideSidePanel()"));
  sidePanel.appendChild(createButton("OK", "btn btn-sm btn-outline-success mt-3", "changeAvatar(" + choice + ")"));

  var credits = document.createElement("div");
  credits.innerHTML = "Icons made by <a href=\"https://creativemarket.com/eucalyp\" title=\"Eucalyp\">Eucalyp</a> from <a href=\"https://www.flaticon.com/\" 		    title=\"Flaticon\">www.flaticon.com</a> is licensed by <a href=\"http://creativecommons.org/licenses/by/3.0/\" 		    title=\"Creative Commons BY 3.0\" target=\"_blank\">CC 3.0 BY</a>";
  sidePanel.appendChild(credits);
}

function getAvatarChoicesDiv() {
  var containerDiv = createDiv("container");
  var defaultAvatar = document.createElement("img");
  defaultAvatar.src = getMeta("staticResourcePath") + "images/avatars/0.png";
  defaultAvatar.setAttribute("width", "30");
  defaultAvatar.setAttribute("style", "m-2");
  defaultAvatar.setAttribute("onclick", "sidePanelChangeAvatar(0)");
  containerDiv.appendChild(defaultAvatar);
  for (i = 150; i > 100; i--) {
    var Avatar = document.createElement("img");
    Avatar.src = getMeta("staticResourcePath") + "images/avatars/" + i + ".png";
    Avatar.setAttribute("width", "30");
    Avatar.setAttribute("style", "m-2");
    Avatar.setAttribute("onclick", "sidePanelChangeAvatar(" + i + ")");
    containerDiv.appendChild(Avatar);
  }
  return containerDiv;
}

function changeAvatar(selection) {
  API_updateUserAvatar(selection, ChangeAvatarCallback);
}

function ChangeAvatarCallback(selection) {
  //assumes success
  if (selection > 0) {
    setup();
    showSidePanel();
    getDOM("aside").appendChild(createP("Change successful!", "text-success"));
    getDOM("aside").appendChild(createButton("OK", "btn btn-success", "hideSidePanel()"));
    sessionStorage.setItem("upic", selection);
    userCookie.upic = selection;
  } else { //maybe -1 for failed for whatever reason
    showSidePanel();
    console.log(selection);
    getDOM("aside").appendChild(createP("Unable to change your Avatar, please try again", "text-danger"));
    getDOM("aside").appendChild(createButton("OK", "btn btn-success", "hideSidePanel()"));
  }
}

//==============================================================================
//password reset================================================================
//==============================================================================

function sidePanelChangePassword() {
  showSidePanel();
  var panel = document.getElementById("aside");
  var statusP = document.createElement("p");
  statusP.id = "statusText";
  statusP.className = "text-danger";
  statusP.innerHTML = "";
  panel.appendChild(statusP);

  //adding extra input fields for finishing the reset process
  var resetCodeInput = createInput("password", "current password", "form-control");
  var passwordInput = createInput("password", "new password", "form-control");
  var repasswordInput = createInput("password", "re-enter new password", "form-control");
  //set DOM ID so other functions can get its value
  resetCodeInput.id = "oldPassInput";
  passwordInput.id = "newPassInput1";
  repasswordInput.id = "newPassInput2";

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

  var cancelButton = createButton("Cancel", "btn btn-sm btn-outline-danger", "hideSidePanel()");
  var sendButton = createButton("Change Password", "btn btn-sm btn-outline-success", "changePasswordClicked()");

  panel.appendChild(inputGroup1);
  panel.appendChild(inputGroup2);
  panel.appendChild(inputGroup3);
  panel.appendChild(cancelButton);
  panel.appendChild(sendButton);

  resetCodeInput.focus();
}

function changePasswordClicked() {
  var oldPass = getDOM("oldPassInput").value;
  var newPass1 = getDOM("newPassInput1").value;
  var newPass2 = getDOM("newPassInput2").value;
  if (oldPass.length < 6) {
    getDOM("oldPassInput").value = "";
    getDOM("oldPassInput").focus();
    getDOM("statusText").innerHTML = "You must provide valid current password";
  } else if (newPass1.length < 6) {
    getDOM("newPassInput1").value = "";
    getDOM("newPassInput2").value = "";
    getDOM("newPassInput1").focus();
    getDOM("statusText").innerHTML = "The password must be at least 6 characters";
  } else if (newPass1 != newPass2) {
    getDOM("newPassInput1").value = "";
    getDOM("newPassInput2").value = "";
    getDOM("newPassInput1").focus();
    getDOM("statusText").innerHTML = "The two password entries must match";
  } else {
    API_updateUserPassword(oldPass, newPass1, changePasswordCallback);
  }
}

function changePasswordCallback(response) {
  showSidePanel();
  getDOM("aside").appendChild(createP(response, "text-info"));
  getDOM("aside").appendChild(createButton("OK", "btn btn-success", "hideSidePanel()"));
}
