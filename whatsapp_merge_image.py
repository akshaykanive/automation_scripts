"""
This code is developed for sending automated whatsapp messages along with an image.
This is intended to be used for Rotaract Purposes only.

Developed and maintained by: Rtn. PP. Rtr. Akshay Kumar Kanive M

In case of any queries, please write to akshaykanive@gmail.com
"""
#Import required modules
import pywhatkit

#Get the phone numbers. Not sure what happends if non-whatsapp number is given.
phone_nos=["+919611261259"]
#Get the names of the participipants
names=["Akshay"]
#Get the payment reference for each participants
payment_ref=["dummy_payment_id"]
path="/Users/akshaykanive/Downloads/rotacamp.png"

for i in range(len(phone_nos)):
    message="Hello "+names[i]+",\nThank you for registerting for Rotaract Dist Conference.\n\n*Payment Reference:* "+payment_ref[i]+"\nPlease find the attached pass for the same. \nThis is an automated message. Please do not reply.😂 \n"
    pywhatkit.sendwhats_image(phone_nos[i],path,message,tab_close=True)