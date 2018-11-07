function getServerTime(callback, innerCallBack) {
  var request = new XMLHttpRequest();
  request.open('GET', 'https://youcmt.com/api/datetime', true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var responseTime = JSON.parse(this.response).datetime;
      console.log("set current time = " + responseTime);
      currentTime = responseTime;
      callback(innerCallBack); //guarantees time is loaded
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.send();
}

function getVideoInfo(callback) {
  var request = new XMLHttpRequest();
  request.open('GET', 'https://youcmt.com/api/video/info?vid=' + videoID, true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      console.log("Fetched Video Info");
      videoInfo = JSON.parse(this.response);
      callback(); //ready to display video info
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.send();
}

function getRootComments(callback) {
  var url = "https://youcmt.com/api/comment/get/comments?vid=" + videoID;
  var request = new XMLHttpRequest();
  request.open('GET', url, true);
  request.onload = function() {
    if (request.status >= 200 && request.status < 400) {
      var serverResponse = JSON.parse(this.response).comments;
      console.log("serverResponse.length = " + serverResponse.length);
      for (i = 0; i < serverResponse.length; i++) {
        rootCommentsArray[i] = new RootCommentObject(serverResponse[i]);
      }
      callback();
    } else {
      console.log('request failed, status = ' + request.status);
    }
  };
  request.error = function(e) {
      console.log("request.error called. Error: " + e);
  };
  request.onreadystatechange = function(){
      //console.log("request.onreadystatechange called. readyState: " + this.readyState);
  };
  request.send();
}
