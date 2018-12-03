from flask_restful import Resource
from flask_jwt_extended import (
    create_access_token,
    jwt_refresh_token_required,
    get_jwt_identity,
    jwt_required
)



#### used to get a new access_token with an existing refresh_token
class TokenRefresh(Resource):
    
    @jwt_refresh_token_required
    def post(self):
        identity = get_jwt_identity()
        access_token = create_access_token(identity=identity)
        return {"access_token":access_token}

#### checks wether or not an access_token is valid
#### for debug use only
class AccessTokenCheck(Resource):

    @jwt_required
    def get(self):
        return {
            "message":"token is valid."
        },200
