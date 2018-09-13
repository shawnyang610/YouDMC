import sys
import os

#### add current path to system path ####
cur_abs_path = os.path.abspath(os.curdir)
if  cur_abs_path not in sys.path:
    print('...missing directory in PYTHONPATH... added!')
    sys.path.append(cur_abs_path)

#### drop all existing tables and create new from models ####
from rest_api import db # noqa
db.drop_all()
from rest_api.models.video import VideoModel # noqa
from rest_api.models.user import UserModel # noqa
from rest_api.models.comment import CommentModel # noqa
db.create_all()

#### init db with essential data ####

# create first user as admin


# create second user as anonymous



print('...done!')