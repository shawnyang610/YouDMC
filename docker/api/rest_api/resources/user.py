from flask_restful import Resource, reqparse
from werkzeug.security import generate_password_hash
from rest_api.models.user import UserModel

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
            return {
                "message":"user registered!"
            },201
        except:
            return {
                "message":"something went wrong during user registration."
            },500


class UserInfo(Resource):
    parser=reqparse.RequestParser()
    parser.add_argument(
        "username", type=str, required=False, help="username cannot be blank."
    )
    parser.add_argument(
        "email", type=str, required=False, help="email cannot be blank."
    )

    def post(self):
        data = self.parser.parse_args()
        if 'username' in data.keys():
            # get user by username
            user = UserModel.find_by_username(data['username'])


        elif 'email' in data.keys():
            # get user by email
            user = UserModel.find_by_email(data['email'])
        else:
            return {
                "message":"error, provide either username or email parameter."
            }
        
        if not user:
            return {
                "message":"user not found."
            },404

        return {
            "username":user.username,
            "email":user.email,
            "registration date":str(user.date),
            "profile_img":user.profile_img
        }