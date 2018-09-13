function setup() {
  document.getElementById("jsStatus").innerHTML="JS running"; //indicate js is working
}

function test() {
  appendToPage('test');
  var request = new XMLHttpRequest();
  request.open('GET', 'http://youcmt.com/api/datetime', true);
  appendToPage('open');
  request.onload = function() {
    appendToPage('onload');
    appendToPage(JSON.parse(this.response).datetime);
  }
  request.send();
}

function appendToPage(someString) {
  var newP = document.createElement("p");
  newP.innerHTML = someString;
  document.getElementById("contents").appendChild(newP);
}
