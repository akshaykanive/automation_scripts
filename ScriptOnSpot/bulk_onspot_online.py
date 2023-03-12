from firebase_admin import db
from firebase_admin import credentials
import firebase_admin
import pywhatkit
import xlrd
import os

def insert_to_firebase(name, id, temp):
    databaseURL = 'https://holiyay-2a97e-default-rtdb.firebaseio.com/'
    cred_obj = firebase_admin.credentials.Certificate('./holiyay-2a97e-firebase-adminsdk-xlcyk-5ff6a5e4f7.json')
    if(temp == 1):
        default_app = firebase_admin.initialize_app(cred_obj, {'databaseURL': databaseURL})
    ref_users = db.reference("/Users")

    user = {id : {"Name": name, "Food": {"Thandai": "No", "VadaPav":"No", "Tang":"No"}, "CheckedIn": "No"}}
    ref_users.update(user)

message="""
Warm Rotaract Greetings!

Thank you for registering for HoliYay!
Here is your pass for the event. Please ensure to check the rules and regulations for the event.
https://bit.ly/holiyay2023

Awaiting to see you there!
"""

wb = xlrd.open_workbook("Book4.xls")
sheet = wb.sheet_by_index(0)

row = sheet.nrows
col = sheet.ncols

for i in range(1, row):
    phone_no = "+91" + str(sheet.cell_value(i, 2))[0:10]

    name = sheet.cell_value(i, 1)

    payment_ref = "Holiyay_OnSpot_" + (str(sheet.cell_value(i, 0)).strip())[0:-2]
    # payment_ref = (str(sheet.cell_value(i, 3)).strip())[0:-2]

    print(payment_ref)
    insert_to_firebase(name, payment_ref, i)

    path = "./Generated_Pass/"+payment_ref+".png"

    print(phone_no)
    pywhatkit.sendwhats_image(phone_no,path,message,tab_close=True,close_time=8)

    os.system("del " + payment_ref + ".png")