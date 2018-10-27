function getUserInfo(url) { //run automatically when page is loaded
  appendToPage('script running');
  var request = new XMLHttpRequest();
  request.open('GET', url, true);
  appendToPage('open');
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      appendToPage('request success, status = ' + request.status);
      appendToPage('onload');
      appendToPage(JSON.stringify(this.responseText));
      appendToPage(JSON.parse(this.response).username);
      appendToPage(JSON.parse(this.response).email);
      //appendToPage(JSON.parse(this.response).'registration date');
      appendToPage(JSON.parse(this.response).profile_img);
    } else {
      appendToPage('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };

  request.send();
  appendToPage('request sent, status = ' + request.status);
}

function appendToPage(someString) {
  var newP = document.createElement("p");
  newP.innerHTML = someString;
  document.getElementById("contents").appendChild(newP);
}
