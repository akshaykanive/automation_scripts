"""
This code is developed for sending automated whatsapp messages along with an image.
This is intended to be used for Rotaract Purposes only.

Developed and maintained by: Rtn. PP. Rtr. Akshay Kumar Kanive M

In case of any queries, please write to akshaykanive@gmail.com
"""
#Import required modules
import xlrd
import os
# Importing the PIL library
from PIL import Image
from PIL import ImageDraw
from PIL import ImageFont
import json
import requests
import cv2
from pyzbar.pyzbar import decode


import qrcode

def replace_qr(pass_before, replacement_qr, pass_after):
  # Load the input image
  img = cv2.imread(pass_before)

  # Load the replacement QR code
  replacement_qr = cv2.imread(replacement_qr)

  # Decode QR codes in the image
  qr_codes = decode(img)

  # Loop through each detected QR code
  for qr in qr_codes:
      # Get the location of the QR code in the image
      x, y, w, h = qr.rect

      # Calculate the size difference between the detected QR code and the replacement QR code
      qr_width = w
      qr_height = h
      replacement_qr_width = replacement_qr.shape[1]
      replacement_qr_height = replacement_qr.shape[0]
      width_diff = qr_width - replacement_qr_width
      height_diff = qr_height - replacement_qr_height

      # If the detected QR code is larger than the replacement QR code, crop the detected QR code to match the size of the replacement QR code
      if width_diff > 0 and height_diff > 0:
          x += int(width_diff / 2)
          y += int(height_diff / 2)
          w = replacement_qr_width
          h = replacement_qr_height

      # If the detected QR code is smaller than the replacement QR code, resize the replacement QR code to match the size of the detected QR code
      elif width_diff < 0 and height_diff < 0:
          replacement_qr_resized = cv2.resize(replacement_qr, (w, h))
          replacement_qr_width = replacement_qr_resized.shape[1]
          replacement_qr_height = replacement_qr_resized.shape[0]
          replacement_qr = replacement_qr_resized

      # Replace the QR code with the replacement QR code
      img[y:y+h, x:x+w] = replacement_qr

  # Save the output image
  cv2.imwrite(pass_after, img)

def generate_qr_code(data, file_name):
    qr = qrcode.QRCode(
        version=1,
        box_size=6.5,
        border=2
    )
    qr.add_data(data)
    qr.make(fit=True)
    img = qr.make_image(fill_color="#c1004f", back_color="white")
    img.save(file_name)


wb = xlrd.open_workbook("OnSpot.xls")
sheet = wb.sheet_by_index(0)

row = sheet.nrows
col = sheet.ncols

for i in range(1, row):
    phone_no = sheet.cell_value(i, 2)

    name = sheet.cell_value(i, 1)

    payment_ref = "Holiyay_OnSpot_" + (str(sheet.cell_value(i, 0)).strip())[0:-2]
    # payment_ref = (str(sheet.cell_value(i, 3)).strip())[0:-2]

    generate_qr_code(payment_ref, payment_ref+".png")

    img = Image.open("pass_before.jpeg")

    # Call draw Method to add 2D graphics in an image
    I1 = ImageDraw.Draw(img)

    # Custom font style and font size
    myFont = ImageFont.truetype('./Montserrat-Bold.ttf', 30)
    myFont2 = ImageFont.truetype('./Montserrat-Bold.ttf', 15)

    # Add Text to an image
    I1.text((1250, 210), name, font=myFont, fill="#c1004f")
    I1.text((1390, 567), payment_ref, font=myFont2, fill="#c1004f")

    # Display edited image
    #   img.show()

    # Save the edited image
    img.save("./Generated_Pass/" + payment_ref+".png")

    replace_qr("./Generated_Pass/"+payment_ref+".png", payment_ref+".png", "./Generated_Pass/"+payment_ref+".png")
