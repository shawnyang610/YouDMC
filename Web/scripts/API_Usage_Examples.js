"user/register":
  POST body = {
    "username":"shawn",
    "password":"mypass",
    "email":"shawn@shawn.com"
  }
  SUCCESS_RESPONSE = {
    "message":"user registered!",
    "id":"14",
    "access_token": "rthnosnoabea1389hnr0n00b00b..."
  }
  FAIL_RESPONSE = {
    "message":"user already exists"
  }

"user/login":
  POST body = {
    ("username":"shawn" || "email":"shawn@shawn.com"),
    "password":"mypass"
  }
  SUCCESS_RESPONSE = {
    "message":"user logged in!",
    "id":"14",
    "access_token": "rthnosnoabea1389hnr0n00b00b..."
  }
  FAIL_RESPONSE = {
    "message":"user does not exist"
  }

"/checkToken": GET HEADER={"Authorization" = "Bearer " + token};

"user/logout_access": POST HEADER={"Authorization" = "Bearer " + token};

"user/confirm_email": POST BODY={ //forgot password part 1
    "email" = "shawn@shawn.com"
  };
  RESPONSE = sendEmail {
    "Hi Shawn1, your code is " + someCode;
  }
  "user/reset_password": POST BODY={ //forgot password part 2
    "email" = "shawn@shawn.com"
    "reset_code" = "dgeewa",
    "new_password" = "mynewpass"
  };

"comment/post/guest" POST BODY = {
  "text":"hello world",
	"vid":"TEST"
}
