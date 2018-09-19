function getVideoInfo(videoID) { //run automatically when page is loaded
  appendToPage('script running');
  var request = new XMLHttpRequest();
  var url = 'https://youcmt.com/api/video/info?vid=' + videoID;
  request.open('GET', url, true);
  appendToPage('open');
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      appendToPage('request success, status = ' + request.status);
      appendToPage('onload');
      appendToPage(JSON.parse(this.response).responseText);
      appendToPage(JSON.parse(this.response).vid);
      appendToPage(JSON.parse(this.response).title);
      appendToPage(JSON.parse(this.response).date);
      appendToPage(JSON.parse(this.response).author);
      appendToPage(JSON.parse(this.response).description);
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
