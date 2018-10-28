from flask import Flask, render_template, request
from flask_restful import Api
from flask_jwt_extended import JWTManager
from flask_sqlalchemy import SQLAlchemy
from flask_jwt_extended import JWTManager
from werkzeug.security import generate_password_hash
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
    return render_template("meta_test_v2.html", v=v)


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
#### config api  ######
###########################
api = Api(app)


from rest_api.resources.env import DateTime # noqa
api.add_resource(DateTime, "/api/datetime")

from rest_api.resources.user import UserRegister, UserLogin, UserLogoutAccess, UserLogoutRefresh # noqa
api.add_resource(UserRegister, "/api/user/register")
api.add_resource(UserLogin, "/api/user/login")
api.add_resource(UserLogoutAccess, "/api/user/logout_access")
api.add_resource(UserLogoutRefresh, "/api/user/logout_refresh")

from rest_api.resources.jwt import TokenRefresh # noqa
api.add_resource(TokenRefresh, "/api/refresh_token")


from rest_api.resources.video import VideoInfo # noqa
api.add_resource(VideoInfo, "/api/video/info")

from rest_api.resources.comment import Comment # noqa
api.add_resource(Comment, "/api/comment")

####################################
#### allow rest api request header
######################################
@app.after_request
def after_request(response):
  response.headers.add('Access-Control-Allow-Origin', '*')
  response.headers.add('Access-Control-Allow-Headers', 'Content-Type,Authorization')
  response.headers.add('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE')
  return response