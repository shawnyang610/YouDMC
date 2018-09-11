from rest_api import db
from datetime import datetime

class VideoModel(db.Model):
    __tablename__ = "videos"

    id = db.Column (db.Integer, primary_key=True)
    date = db.Column (db.DateTime, nullable=False, default=datetime.utcnow)
    is_deleted = db.Column (db.Integer, nullable=False, default=0)
    url = db.Column (db.String, nullable =False)
    title = db.Column (db.String, nullable = False)

    def __init__(self, url, title):
        self.url=url
        self.title=title
    
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