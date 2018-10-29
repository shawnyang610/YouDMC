from rest_api import db
from datetime import datetime
# from rest_api.models.user import UserModel # noqa

class CommentModel(db.Model):
    __tablename__="comments"

    id = db.Column(db.Integer, primary_key=True)
    date = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    text = db.Column(db.String)
    ratings = db.relationship("RatingModel")

    # a comment has either (parent_comment_id and top_comment_id) or a single vid
    top_comment_id = db.Column(db.Integer, nullable=False)
    parent_comment_id = db.Column(db.Integer, nullable=False)

    vid =db.Column(db.String)

    user_id = db.Column(db.Integer, db.ForeignKey("users.id"))
    user = db.relationship ("UserModel")
    is_deleted = db.Column(db.Integer, default=0)

    def __init__(self, text, user_id, **kwargs):
        self.text = text
        self.user_id=user_id
        if 'parent_comment_id' in kwargs.keys():
            self.parent_comment_id=kwargs['parent_comment_id']
        else:
            self.parent_comment_id=0

        if 'top_comment_id' in kwargs.keys():
            self.top_comment_id=kwargs['top_comment_id']
        else:
            self.top_comment_id=0

        if 'vid' in kwargs.keys():
            self.vid = kwargs['vid']
        else:
            self.vid = 0

    
    def to_json(self):
        ratings_count = [rating.rating for rating in self.ratings]
        return {
            "id":self.id,
            "date":str(self.date),
            "text":self.text,
            "user_id":self.user_id,
            "username":self.user.username,
            "top_comment_id": self.top_comment_id,
            "parent_comment_id": self.parent_comment_id,
            "like":ratings_count.count(1),
            "dislike":ratings_count.count(-1),
            "count": CommentModel.query.filter_by(top_comment_id=self.id, is_deleted=0).count()
        }

    @classmethod
    def find_by_id (cls, id):
        return cls.query.filter_by(id=id, is_deleted=0).first()
    
    @classmethod
    def find_all_by_vid (cls, vid):
        return cls.query.filter_by(vid=vid, is_deleted=0).order_by(cls.date)

    @classmethod
    def find_all_by_parent_comment_id(cls, parent_comment_id):
        return cls.query.filter_by(parent_comment_id=parent_comment_id, is_deleted=0).order_by(cls.date)

    @classmethod
    def find_all_by_top_comment_id(cls, top_comment_id):
        return cls.query.filter_by(top_comment_id=top_comment_id, is_deleted=0).order_by(cls.date)


    def save_to_db (self):
        db.session.add(self)
        db.session.commit()

    def delete_from_db (self):
        self.is_deleted=1
        db.session.add(self)
        db.session.commit()
    