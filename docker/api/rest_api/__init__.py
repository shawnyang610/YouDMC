from flask import Flask, render_template, request
from flask_restful import Api
from flask_jwt_extended import JWTManager
from flask_sqlalchemy import SQLAlchemy
# from flask_jwt_extended import JWTManager
# from werkzeug.security import generate_password_hash
# from flask_mail import Mail, Message
from os.path import abspath, dirname

###########################
#### config flask app ####
###########################
app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False
app.config['PROPAGATE_EXCEPTIONS'] = True
app.config['SECRET_KEY'] = 'myappsecretkey'

#### web home ####
@app.route("/api/")
def api_index():
    return render_template("api_index.html")

@app.route("/")
def home():
    args = request.args
    if "v" in args.keys():
        v = args['v']
    else:
        v = ''
    return render_template("index.html", v=v)

################################
#### for password reset ########
################################
email_confirm_table = {"head":"FSAFSA"}


###########################
#### config database ######
##########################
basedir = abspath(dirname(__file__))

app.config['POSTGRES_USER'] = "shawn"
app.config["POSTGRES_DEFAULT_USER"] = "postgres"
app.config["POSTGRES_PASSWORD"] = "my_password"
app.config["POSTGRES_DB"]="youcmt-db"
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://'+ app.config['POSTGRES_USER']+":"+app.config["POSTGRES_PASSWORD"]+"@postgres:5432/"+app.config["POSTGRES_DB"]
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

########################
#### config jwt ########
#######################
app.config["JWT_SECRET_KEY"] = "myjwtsecretkey"
app.config["JWT_BLACKLIST_ENABLED"] =True
app.config["JWT_BLACKLIST_TOKEN_CHECKS"] = ["access", "refresh"]

jwt = JWTManager(app)
from rest_api.models.jwt import RevokedTokenModel # noqa

@jwt.token_in_blacklist_loader
def is_blacklisted(decrypted_token):
    jti = decrypted_token["jti"]
    return RevokedTokenModel.is_jti_blacklisted(jti)

# TODO
@jwt.user_claims_loader
def add_claims_to_access_token(identity):
    pass


###########################
#### config flask-mail  ######
###########################

app.config["MAIL_SERVER"]="smtp.gmail.com"
app.config["MAIL_PORT"]=465
app.config["MAIL_USE_TLS"] = False
app.config["MAIL_USE_SSL"] = True
app.config["MAIL_USERNAME"] = "info.youcmt@gmail.com"
app.config["MAIL_PASSWORD"] = "Youcmtcmt"

# testing
# @app.route("/email")
# def email():
#     mail = Mail(app)
#     msg = Message("test", sender="info.youcmt@gmail.com", recipients=["info.youcmt@gmail.com", "shawnyang610@gmail.com"])
#     msg.body = "text body"
#     msg.html = "<b>HTML</b>"
#     mail.send(msg)
#     return "sent"

###########################
#### config api  ######
###########################
api = Api(app)



from rest_api.resources.env import DateTime # noqa
api.add_resource(DateTime, "/api/datetime")

from rest_api.resources.user import (
    UserRegister,
    UserLogin,
    UserLogoutAccess,
    UserLogoutRefresh,
    ConfirmEmail,
    ResetPassword) # noqa

api.add_resource(UserRegister, "/api/user/register")
api.add_resource(UserLogin, "/api/user/login")
api.add_resource(UserLogoutAccess, "/api/user/logout_access")
api.add_resource(UserLogoutRefresh, "/api/user/logout_refresh")
api.add_resource(ConfirmEmail, "/api/user/confirm_email")
api.add_resource(ResetPassword, "/api/user/reset_password")

from rest_api.resources.jwt import TokenRefresh, AccessTokenCheck # noqa
api.add_resource(TokenRefresh, "/api/refresh_token")
api.add_resource(AccessTokenCheck, "/api/check_token")


from rest_api.resources.video import VideoInfo, WhatsHot # noqa
api.add_resource(VideoInfo, "/api/video/info")
api.add_resource(WhatsHot, "/api/video/whatshot")

from rest_api.resources.comment import(
    Comment,
    Comment_Loggedin,
    GetComments,UserComment,
    EditComment,
    DeleteComment) # noqa

api.add_resource(Comment, "/api/comment/post/guest")
api.add_resource(Comment_Loggedin, "/api/comment/post/user")
api.add_resource(GetComments, "/api/comment/get/comments")
api.add_resource(UserComment, "/api/comment/get/user_comments")
api.add_resource(EditComment, "/api/comment/edit")
api.add_resource(DeleteComment, "/api/comment/delete")

from rest_api.resources.rating import RateComment # noqa
api.add_resource(RateComment, "/api/rate_comment")

####################################
#### allow rest api request header
######################################
@app.after_request
def after_request(response):
  response.headers.add('Access-Control-Allow-Origin', '*')
  response.headers.add('Access-Control-Allow-Headers', 'Content-Type,Authorization')
  response.headers.add('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE')
  return response
  

