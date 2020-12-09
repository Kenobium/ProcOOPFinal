import os
import sys
from PIL import Image, ImageFilter

try:
    image = Image.open(str(sys.argv[1]))
    f = image.format
    image = image.convert("L").filter(ImageFilter.FIND_EDGES)
    ext = os.path.splitext(str(sys.argv[1]))[1]
    image.save("./cache/e_temp" + ext, f)
except FileNotFoundError:
    print("This file does not exist. Try again.")
