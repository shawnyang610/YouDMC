var inputArray = [];

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

function showRegister() {
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

  inputArray[0] = userNameInput;
  inputArray[1] = passwordInput;
  inputArray[2] = repeatPasswordInput;
  inputArray[3] = emailInput;

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
  var un = inputArray[0].value;
  var pw1 = inputArray[1].value;
  var pw2 = inputArray[2].value;
  var email = inputArray[3].value;

  if (!validUsername(un)) {
    statusText.innerHTML = "Not a valid username. Must contain only alpha-numeric.";
    return;
  }

  if (pw1.length < 6) {
    statusText.innerHTML = "Password must be at least 6 characters";
    return;
  }

  if (pw1 != pw2) {
    statusText.innerHTML = "Two passwords must match";
    return;
  }

  if (!validEmail(email)) {
    statusText.innerHTML = "Not a valid email";
    return;
  }

  statusText.innerHTML = "Good to go";
}

function login() {

}

function logout() {

}

function validUsername(un) { //guards for username here
  if (un == null || un.length == 0) {
    return false;
  }
  return true;
}

function validEmail(em) { //guards for email here
  if (em == null || em.length == 0) {
    return false;
  }
  return true;
}
