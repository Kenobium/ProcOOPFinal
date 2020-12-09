import os
import sys
from PIL import Image, ImageEnhance

try:
    image = Image.open(str(sys.argv[1]))
    enhancer = ImageEnhance.Contrast(image)
    factor = float(sys.argv[2])
    image2 = enhancer.enhance(factor)
    ext = os.path.splitext(str(sys.argv[1]))[1]
    image2.save("./cache/cr_temp" + ext, image.format)
except FileNotFoundError:
    print("This file does not exist. Try again.")
