function setup() {
  document.getElementById("demo").innerHTML="JS running"; //indicate js is working
}

function insertTimeStamp() {  
  var newP = document.createElement("p");
  newP.appendChild(document.createTextNode(Date()));
  document.getElementById("contents").appendChild(newP);
}
