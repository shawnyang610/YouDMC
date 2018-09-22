from flask_restful import Resource
from flask import request
from rest_api.models.video import VideoModel # noqa
from rest_api.helper.text_extractor import VideoPageExtorTYDL
import youtube_dl
from flask import make_response, json


class VideoInfo(Resource):

    def get(self):
        args = request.args
        # if args["v"] doesnt exist: err msg
        if "vid" not in args.keys():
            return {
                "message":"invalid param. parameter vid is missing."
            },400
        if len(args["vid"])!=11:
            return {
                "message":"invalid vid. vid needs to be 11 characters long."
            }
        # if video exists in db: get video info from db
        video = VideoModel.find_by_vid(args['vid'])
        # if video doesn't exist in db:
        if not video:
            # get video info from youtube.com
            url = 'https://youtube.com/watch?v='+args["vid"]

            try:
                # extractor = VideoPageExtorPafy(url)
                extractor = VideoPageExtorTYDL(url)
                # video doesn't exist on youtube:
                # if not extractor.is_valid():
                #     return {
                #         "message":"video doesn't exist."
                #     },404
                # video exists on youtube:
                title = extractor.get_title()
                author = extractor.get_author()
                date = extractor.get_date()
                description = extractor.get_description()
            except (IOError, youtube_dl.utils.DownloadError):
                return {
                    "message":"video doesn't exist."
                },404
            video = VideoModel(
                vid=args["vid"],
                title=title,
                author=author,
                description=description,
                video_date=date
                )
            # save info into db
            video.save_to_db()

        # return video info
        res= {
            "vid":video.vid,
            "title":video.title,
            "date":video.video_date,
            "author":video.author,
            "description":video.description
        }
        response = make_response(json.dumps(res))
        response.headers["content-type"]="application/json"

        return response
