import re
import urllib
from pafy import pafy

class VideoPageExtor():

    # default constructor
    # param:
    # src: webpage source code
    def __init__(self, url):
        req = urllib.request.Request(url, headers={'User-Agent':'Mozilla'})
        response = urllib.request.urlopen(req)
        src_byte = response.read()
        self.src = src_byte.decode()
    
    def is_valid(self):
        # extract using regex
        pat = re.compile(r"""Video unavailable""")
        match = re.search(pat, self.src)
        # unavailable = re.search("Video unavailable", self.src)
        if match:
            return False
        else:
            return True

    # extract title
    def get_title(self):
        # extract using regex
        # match = re.search("(?<=\"title\":\").*(?=\",\"user_display_image)", self.src)
        pat = re.compile(r'(?<="title":").+(?=",")')
        match = pat.findall(self.src)
        if not match:
            title = "unknown title"
        else:
            title = match[0]
        return title

    # extract author
    def get_author(self):
        # extract using regex
        # match = re.search("(?<=\"author\":\").*(?=\",\"watermark\":\")", self.src)
        pat = re.compile(r'(?<="author":").*(?=",")')
        match = pat.findall(self.src)
        if not match:
            author = "unknown author"
        else:
            author = match[0]
        return author

class VideoPageExtorPafy():

    # default constructor
    # param:
    # src: webpage source code
    def __init__(self, url):
        self.vid = pafy.new(url)
    
    def is_valid(self):
        pass

    # extract title
    def get_title(self):
        return self.vid.title

    # extract author
    def get_author(self):
        return self.vid.author

    def get_date(self):
        return self.vid.published

    def get_description(self):
        return self.vid.description