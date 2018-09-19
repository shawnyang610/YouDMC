from rest_api import db
from datetime import datetime

class VideoModel(db.Model):
    __tablename__ = "videos"

    id = db.Column (db.Integer, primary_key=True)
    vid = db.Column (db.String, nullable =False)
    date = db.Column (db.DateTime, nullable=False, default=datetime.utcnow)
    is_deleted = db.Column (db.Integer, nullable=False, default=0)
    title = db.Column (db.String, nullable = False)
    author = db.Column (db.String, nullable = False)
    description = db.Column (db.String)
    video_date = db.Column (db.String)

    def __init__(self, vid, title, author, description, video_date):
        self.vid = vid
        self.title=title
        self.author=author
        self.description=description
        self.video_date = video_date
    
    @classmethod
    def find_by_id (cls, id):
        return cls.query.filter_by(id=id, is_deleted=0).first()

    @classmethod
    def find_by_vid (cls, vid):
        return cls.query.filter_by(vid=vid, is_deleted=0).first()

    @classmethod
    def find_by_author(cls, author):
        return cls.query.filter_by(author=author, is_deleted=0)

    def save_to_db (self):
        db.session.add(self)
        db.session.commit()

    def delete_from_db (self):
        self.is_deleted=1
        db.session.add(self)
        db.session.commit()