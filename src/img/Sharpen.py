import os
import sys
from PIL import Image, ImageFilter

try:
    image = Image.open(str(sys.argv[1]))
    f = image.format
    image = image.filter(ImageFilter.SHARPEN)
    ext = os.path.splitext(str(sys.argv[1]))[1]
    image.save("./cache/s_temp" + ext, f)
except FileNotFoundError:
    print("This file does not exist. Try again.")
