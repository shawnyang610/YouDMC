from flask_restful import Resource, reqparse
from flask import request
from werkzeug.security import generate_password_hash, check_password_hash
from rest_api.models.user import UserModel
from flask_jwt_extended import (
    create_access_token,
    create_refresh_token,
    jwt_required,
    jwt_refresh_token_required,
    get_raw_jwt,
    get_jwt_identity
)
from rest_api.models.jwt import RevokedTokenModel

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

    def post(self):
        data = self.parser.parse_args()
        role = "USER"
        profile_img = "default.jpg"
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

            access_token = create_access_token(identity=identity, fresh=True)
            refresh_token = create_refresh_token(identity=identity)
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

        except:
            return {
                "message":"something went wrong during user registration."
            },500


class UserLogin(Resource):
    user_parser = reqparse.RequestParser()
    user_parser.add_argument(
        'username', type=str, required=True, help='username cannot be blank.'
    )
    user_parser.add_argument(
        'password', type=str, required=True, help='password cannot be blank.'
    )
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
            access_token = create_access_token(identity=identity, fresh=True)
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




# class UserInfo(Resource):
#     # parser=reqparse.RequestParser()
#     # parser.add_argument(
#     #     "username", type=str, required=False, help="username cannot be blank."
#     # )
#     # parser.add_argument(
#     #     "email", type=str, required=False, help="email cannot be blank."
#     # )

#     def get(self):
#         args = request.args
#         if 'username' in args.keys():
#             # get user by username
#             user = UserModel.find_by_username(args['username'])


#         elif 'email' in args.keys():
#             # get user by email
#             user = UserModel.find_by_email(args['email'])
#         else:
#             return {
#                 "message":"error, provide either username or email parameter."
#             }
        
#         if not user:
#             return {
#                 "message":"user not found."
#             },404

#         return {
#             "username":user.username,
#             "email":user.email,
#             "registration_date":str(user.date),
#             "profile_img":user.profile_img
#         }