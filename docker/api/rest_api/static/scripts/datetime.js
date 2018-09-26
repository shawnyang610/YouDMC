function run() { //run automatically when page is loaded
  appendToPage('script running');
  var request = new XMLHttpRequest();
  request.open('GET', 'https://youcmt.com/api/datetime', true);
  appendToPage('open');
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      appendToPage('request success, status = ' + request.status);
      appendToPage('onload');
      appendToPage(JSON.parse(this.response).datetime);
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
