import os
import sys
from PIL import Image, ImageOps

try:
    image = Image.open(str(sys.argv[1]))
    factor = int(float((sys.argv[2])))
    image2 = ImageOps.posterize(image.convert('RGB'), factor)
    ext = os.path.splitext(str(sys.argv[1]))[1]
    image2.save("./cache/p_temp" + ext, image.format)
except FileNotFoundError:
    print("This file does not exist. Try again.")
