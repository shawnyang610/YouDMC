from flask_restful import Resource
from datetime import datetime
#### provides system time
class DateTime (Resource):
    
    def get(self):
        dt = str(datetime.utcnow())
        return {"datetime" : dt}