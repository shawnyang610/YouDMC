from flask_restful import Resource, reqparse
from rest_api.models.rating import RatingModel
from flask_jwt_extended import (
    jwt_required,
    get_jwt_identity
)

class RateComment(Resource):
    parser = reqparse.RequestParser()
    parser.add_argument(
        'comment_id', type=int, required=True, help="comment_id cannot be blank."
    )
    # parser.add_argument(
    #     'user_id', type=int, required=True, help="user_id cannot be blank."
    # )
    parser.add_argument(
        'rating', type=int, required=True, help="raing cannot be blank."
    )

    @jwt_required
    def post(self):

        user_id = int(get_jwt_identity()['id'])

        data = self.parser.parse_args()

        if data['rating'] !=1 and data['rating'] !=-1:
            return {
                "message":"rating can only have value of 1 or -1."
            },400
        if RatingModel.does_exist(user_id=user_id, comment_id=data['comment_id']):
            return{
                "message":"each user can only rate a comment once."
            },400
        rating = RatingModel(
            comment_id=data['comment_id'],
            user_id=user_id,
            rating=data['rating']
        )
        rating.save_to_db()
        return {
            "message":"rating saved."
        }
