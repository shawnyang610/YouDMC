from rest_api import db
from datetime import datetime

class UserModel(db.Model):
    __tablename__ = "users"

    id = db.Column(db.Integer, primary_key=True)
    status = db.Column(db.String(10), default="ACTIVE")
    is_deleted = db.Column(db.Integer, default=0)
    date = db.Column(db.DateTime, nullable=False, default= datetime.utcnow)
    role = db.Column(db.String(15), nullable=False)
    username = db.Column(db.String(25), unique=True, nullable = False)
    password_hash = db.Column(db.String(128), nullable = False)
    email = db.Column(db.String, nullable=False,unique = True)
    profile_img = db.Column(db.String, nullable=False)

    def __init__ (self, role, username, password_hash, email, profile_img):
        self.role = role
        self.username = username
        self.password_hash = password_hash
        self.email = email
        self.profile_img = profile_img
    

    @classmethod
    def find_by_id (cls, id):
        return cls.query.filter_by(id=id, is_deleted=0).first()
    
    @classmethod
    def find_by_username (cls, username):
        return cls.query.filter_by(username=username, is_deleted=0).first()
    
    @classmethod
    def find_by_email (cls, email):
        return cls.query.filter_by(email=email, is_deleted=0).first()

    def save_to_db (self):
        db.session.add(self)
        db.session.commit()

    def delete_from_db (self):
        self.is_deleted=1
        db.session.add(self)
        db.session.commit()