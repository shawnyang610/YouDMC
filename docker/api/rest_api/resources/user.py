from flask_restful import Resource, reqparse
# from flask import request
from werkzeug.security import generate_password_hash, check_password_hash
from rest_api.models.user import UserModel
from flask_jwt_extended import (
    create_access_token,
    create_refresh_token,
    jwt_required,
    fresh_jwt_required,
    jwt_refresh_token_required,
    get_raw_jwt,
    get_jwt_identity
)
from rest_api.models.jwt import RevokedTokenModel
import datetime
from rest_api.helper.email import registration_confirmation, confirm_email_owner
from rest_api import email_confirm_table

class UserRegister(Resource):
    parser = reqparse.RequestParser()
    parser.add_argument(
        "username", type=str, required=True, help="username cannot be blank."
    )
    parser.add_argument(
        "password", type=str, required=True, help="password cannot be blank."
    )
    parser.add_argument(
        "email", type=str, required=True, help="email cannot be blank."
    )

    expires = datetime.timedelta(days=365)

    def post(self):
        data = self.parser.parse_args()
        role = "USER"
        profile_img = "0" # str type, 0~99 preset images
        password_hash = generate_password_hash(data["password"])
        
        user = UserModel.find_by_username(data["username"])
        if user:
            return{
                "message":"username already exists."
            },400
        
        user = UserModel.find_by_email(data["email"])
        if user:
            return{
                "message":"email already exists."
            },400
        
        user = UserModel(
            role=role,
            username=data["username"],
            password_hash=password_hash,
            email=data["email"],
            profile_img=profile_img
        )
        try:
            user.save_to_db()
            identity = {
                "role":user.role,
                "id":user.id
            }
            
            registration_confirmation(username=user.username, recipient=user.email)

            access_token = create_access_token(identity=identity, fresh=True, expires_delta=self.expires)
            refresh_token = create_refresh_token(identity=identity)
        except:
            return {
                "message":"something went wrong during user registration."
            },500
            


        
        
        return {
            "message":"user registered!",
            "role":user.role,
            "id":user.id,
            "username":user.username,
            "email":user.email,
            "profile_img":user.profile_img,
            "reg_date": str(user.date),
            "access_token": access_token,
            "refresh_token": refresh_token
        },201


class UserLogin(Resource):
    user_parser = reqparse.RequestParser()
    user_parser.add_argument(
        'username', type=str, required=True, help='username cannot be blank.'
    )
    user_parser.add_argument(
        'password', type=str, required=True, help='password cannot be blank.'
    )

    expires = datetime.timedelta(minutes=15)

    def post(self):
        data = self.user_parser.parse_args()
        user = UserModel.find_by_username(data['username'])

        if not user:
            return {
                "message" : "username does not exist."
            },404
        
        if check_password_hash(user.password_hash, data['password']):
            identity = {
                "role":user.role,
                "id":user.id
            }
            access_token = create_access_token(identity=identity, fresh=True, expires_delta=self.expires)
            refresh_token = create_refresh_token(identity=identity)
            return {
                "message":"Succesfully logged in",
                "role":user.role,
                "id":user.id,
                "username":user.username,
                "email":user.email,
                "profile_img":user.profile_img,
                "reg_date": str(user.date),
                "access_token": access_token,
                "refresh_token": refresh_token
            }
        else:
            return {
                "message":"wrong credentials."
            },401

class UserLogoutAccess(Resource):
    @jwt_required
    def post(self):
        jti = get_raw_jwt()["jti"]
        try:
            revoked_token = RevokedTokenModel(jti)
            revoked_token.save_to_db()
            return {"message":"Access Token has been revoked."},200
        except:
            return {"message": "Something went wrong"},500


class UserLogoutRefresh(Resource):
    @jwt_refresh_token_required
    def post(self):
        jti = get_raw_jwt()["jti"]
        try:
            revoked_token = RevokedTokenModel(jti)
            revoked_token.save_to_db()
            return {"message":"Refresh Token has been revoked."},200
        except:
            return {"message":"Something went wrong"},500


class ConfirmEmail(Resource):
    parser = reqparse.RequestParser()
    parser.add_argument(
        "email", type=str, required=True, help="email cannot be blank."
    )

    def post(self):
        email = self.parser.parse_args()['email']
        user = UserModel.find_by_email(email=email)
        if user:
            confirm_email_owner(username=user.username, recipient=email)
            return{
                "message":"password reset code emailed to {}".format(email)
            },200

        else:
            return {
                "message":"no user with email {} can be found.".format(email)
            },404

class ResetPassword(Resource):
    parser = reqparse.RequestParser()
    parser.add_argument(
        "email", type=str, required=True, help="email cannot be blank."
    )
    parser.add_argument(
        "reset_code", type = str, required=True, help="reset_code cannot be blank."
    )
    parser.add_argument(
        "new_password", type = str, required=True, help="new_password cannot be blank."
    )

    def post(self):
        data = self.parser.parse_args()
        if data['email'] not in email_confirm_table.keys():
            return {
                "message":"no reset code associates with email {}".format(data['email'])
            },404
        if data['reset_code'] == email_confirm_table[data['email']]:
            # del email_confirm_table[data['email']]
            user = UserModel.find_by_email(data['email'])
            if user:
                user.password_hash= generate_password_hash(data['new_password'])
                user.save_to_db()
                return{
                    "message":"password updated successfully for {}".format(user.username)
                },200
            else:
                return {
                    "message":"user with email {} not found.".format(data['email'])
                },404
            

        else:
            return {
                "message":"Incorrect reset code."
            },401


class UpdateProfile(Resource):
    parser = reqparse.RequestParser()
    parser.add_argument(
        "new_email", type=str, required=False
    )
    parser.add_argument(
        "new_profile_img", type=str, required=False
    )
    parser.add_argument(
        "old_password", type=str, required=False
    )
    parser.add_argument(
        "new_password", type=str, required=False
    )
    @fresh_jwt_required
    def post(self):
        user_id =int(get_jwt_identity()['id'])
        user = UserModel.find_by_id(id=user_id)

        data = self.parser.parse_args()
        if data['new_email']:
            user.email = data['new_email']
        if data["new_profile_img"]:
            user.profile_img = data['new_profile_img']
        if data["old_password"] and data["new_password"]:
            if check_password_hash(user.password_hash, data['old_password']):
                user.password_hash = generate_password_hash(data['new_password'])
            else:
                return {
                    "message":"old password doesn't match record."
                }, 401
        try:
            user.save_to_db()
        except:
            return{
                "message":"something wrong happened updating database."
            },500

        return {
            "message":"profile updated successfully."
        }