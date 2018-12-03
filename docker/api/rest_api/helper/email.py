from flask_mail import Mail, Message
from rest_api import app, email_confirm_table
import random
import string

# Email notifications for registration confirmation
def registration_confirmation(username, recipient):
    mail = Mail(app)
    msg = Message("Welcome to youcmt.com!", sender="info.youcmt@gmail.com", recipients=[recipient])
    msg.body = "Hi {}, Thank you for creating a new account with us!".format(username)
    # msg.html = "<b>HTML</b>"
    try:
        mail.send(msg)
    except:
        # do nothing in case of bad email
        return
    return

# generate password recovery code randomly
def random_code_generator(n):
    ret = ''.join(random.choice(string.ascii_uppercase + string.digits + string.ascii_lowercase) for _ in range(n))
    return ret

# sends randome recovery code to requestor's email
def confirm_email_owner(username, recipient):
    code = random_code_generator(7)
    email_confirm_table[recipient]= code
    mail = Mail(app)
    msg = Message("Password reset code", sender="info.youcmt@gmail.com", recipients=[recipient])
    msg.body = "Hi, {} Your password reset code: {}".format(username, code)
    try:
        mail.send(msg)
    except:
        # do nothing in case of bad email
        return
    return