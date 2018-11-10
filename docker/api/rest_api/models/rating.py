from rest_api import db

class RatingModel(db.Model):
    __tablename__ = "ratings"

    id = db.Column(db.Integer, primary_key=True)
    comment_id = db.Column(db.Integer, db.ForeignKey("comments.id"))
    user_id = db.Column(db.Integer, db.ForeignKey("users.id"))
    rating = db.Column(db.Integer, nullable=False)

    def __init__(self, comment_id, user_id, rating):
        self.comment_id=comment_id
        self.user_id=user_id
        self.rating=rating
    
    def save_to_db(self):
        db.session.add(self)
        db.session.commit()

    @classmethod
    def does_exist(cls, user_id, comment_id):
        rating = cls.query.filter_by(user_id=user_id, comment_id=comment_id)
        return rating.count()>0
    
    @classmethod
    def find_by_comment_id_user_id(cls, comment_id, user_id):
        return cls.query.filter_by(comment_id=comment_id, user_id=user_id).first()