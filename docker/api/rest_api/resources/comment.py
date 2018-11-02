from flask_restful import Resource, reqparse
from flask import request
from rest_api.models.comment import CommentModel
from flask_jwt_extended import(
    jwt_required,
    get_jwt_identity
)

class Comment(Resource):

    parser = reqparse.RequestParser()
    parser.add_argument(
        "text", type=str, required=True, help="text cannot be blank."
    )
    parser.add_argument(
        "user_id", type=str, required=True, help="user_id cannot be blank."
    )
    parser.add_argument(
        "vid", type=str, required=False
    )
    parser.add_argument(
        "parent_comment_id", type=int, required=False
    )
    # parser.add_argument(
    #     "top_comment_id", type=int, required=False
    # )
    def post(self):
        args = self.parser.parse_args()
 
        if args['vid']:
            comment = CommentModel(text=args['text'], user_id=args['user_id'], **{'vid':args['vid']})
            comment.save_to_db()


        elif args['parent_comment_id']:
            parent_comment= CommentModel.find_by_id(args['parent_comment_id'])
            if not parent_comment:
                return {
                    "message":"parent comment id not found."
                }, 404

            if parent_comment.top_comment_id == 0:
                top_comment_id = parent_comment.id
            else:
                top_comment_id = parent_comment.top_comment_id

            kwargs = {
                    'parent_comment_id':args['parent_comment_id'],
                    'top_comment_id':top_comment_id
                    }
            comment = CommentModel(
                text=args['text'],
                user_id=args['user_id'],
                **kwargs
                )
            comment.save_to_db()
        else:
            return {
                "message":"required parameter(s) missing."
            },400
        return {
            "message":"comment saved."
        },200

    def get (self):
        args = request.args
        if ('vid' not in args.keys() and 'parent_comment_id' not in args.keys() and 'top_comment_id' not in args.keys()):
            return {
                "message":"required parameter missing."
            }, 400
        
        elif 'vid' in args.keys():
            comments = CommentModel.find_all_by_vid(args['vid'])
        
        elif 'parent_comment_id' in args.keys():
            comments = CommentModel.find_all_by_parent_comment_id(int(args['parent_comment_id']))
        else:
            comments = CommentModel.find_all_by_top_comment_id(int(args['top_comment_id']))

        comment_list = [comment.to_json() for comment in comments]
        return {"comments":comment_list},200



class Comment_Loggedin(Resource):

    parser = reqparse.RequestParser()
    parser.add_argument(
        "text", type=str, required=True, help="text cannot be blank."
    )
    # parser.add_argument(
    #     "user_id", type=int, required=True, help="user_id cannot be blank."
    # )
    parser.add_argument(
        "vid", type=str, required=False
    )
    parser.add_argument(
        "parent_comment_id", type=int, required=False
    )
    # parser.add_argument(
    #     "top_comment_id", type=int, required=False
    # )

    @jwt_required
    def post(self):
        args = self.parser.parse_args()

        user_id = int(get_jwt_identity()['id'])

 
        if args['vid']:
            comment = CommentModel(text=args['text'], user_id=user_id, **{'vid':args['vid']})
            comment.save_to_db()


        elif args['parent_comment_id']:
            parent_comment= CommentModel.find_by_id(args['parent_comment_id'])
            if not parent_comment:
                return {
                    "message":"parent comment id not found."
                }, 404

            if parent_comment.top_comment_id == 0:
                top_comment_id = parent_comment.id
            else:
                top_comment_id = parent_comment.top_comment_id

            kwargs = {
                    'parent_comment_id':args['parent_comment_id'],
                    'top_comment_id':top_comment_id
                    }
            comment = CommentModel(
                text=args['text'],
                user_id=user_id,
                **kwargs
                )
            comment.save_to_db()
        else:
            return {
                "message":"required parameter(s) missing."
            },400
        return {
            "message":"comment saved."
        },200