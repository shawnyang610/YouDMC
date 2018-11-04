from flask_mail import Mail, Message
from rest_api import app

def registration_confirmation(username, recipient):
    mail = Mail(app)
    msg = Message("Welcome to youcmt.com!", sender="info.youcmt@gmail.com", recipients=[recipient])
    msg.body = "Hi {}, Thank you for creating a new account with us!".format(username)
    # msg.html = "<b>HTML</b>"
    mail.send(msg)
    return