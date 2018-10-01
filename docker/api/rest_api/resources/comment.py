from flask_restful import Resource, reqparse
from flask import request
from rest_api.models.comment import CommentModel

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
        "parent_comment_id", type=str, required=False
    )
    def post(self):
        args = self.parser.parse_args()
        if  ('text' not in args.keys() or
            'user_id' not in args.keys() or
            ('vid' not in args.keys() and 'parent_comment_id' not in args.keys())):
            return {
                "message":"required parameter(s) missing."
            },400
        elif 'vid' in args.keys():
            comment = CommentModel(text=args['text'], user_id=args['user_id'], **{'vid':args['vid']})
            comment.save_to_db()
        else: # 'parent_comment_id' in args.keys()
            comment = CommentModel(text=args['text'], user_id=args['user_id'], **{'vid':args['parent_comment_id']})
            comment.save_to_db()
        return {
            "message":"comment saved."
        },200

    def get (self):
        args = request.args
        if ('vid' not in args.keys() and 'parent_comment_id' not in args.keys()):
            return {
                "message":"required parameter missing."
            }, 400
        
        elif 'vid' in args.keys():
            comments = CommentModel.find_all_by_vid(args['vid'])
        
        else:
            comments = CommentModel.find_all_by_parent_comment_id(args['parent_comment_id'])

        if comments is None:
            return {
                "message":"no comments found"
            }, 204
        
        comment_list = [comment.to_json() for comment in comments]
        return {"comments":comment_list},200


