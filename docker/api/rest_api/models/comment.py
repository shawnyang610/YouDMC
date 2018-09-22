from rest_api import db
from datetime import datetime
# from rest_api.models.user import UserModel # noqa

class CommentModel(db.Model):
    __tablename__="comments"

    id = db.Column(db.Integer, primary_key=True)
    date = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    text = db.Column(db.String)
    parent_comment_id = db.Column(db.Integer, nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey("users.id"))
    is_deleted = db.Column(db.Integer, default=0)

    def __init__(self, text, parent_comment_id, user_id):
        self.text = text
        self.parent_comment_id=parent_comment_id
        self.user_id=user_id
    
    @classmethod
    def find_by_id (cls, id):
        return cls.query.filter_by(id=id, is_deleted=0).first()

    def save_to_db (self):
        db.session.add(self)
        db.session.commit()

    def delete_from_db (self):
        self.is_deleted=1
        db.session.add(self)
        db.session.commit()
    