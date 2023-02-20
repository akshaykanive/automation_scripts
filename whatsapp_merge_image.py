"""
This code is developed for sending automated whatsapp messages along with an image.
This is intended to be used for Rotaract Purposes only.

Developed and maintained by: Rtn. PP. Rtr. Akshay Kumar Kanive M

In case of any queries, please write to akshaykanive@gmail.com
"""
#Import required modules
import pywhatkit
import json

#Get the phone numbers. Not sure what happends if non-whatsapp number is given.
phone_nos=["+917892209752","+919725400338","+918970935199","+916203953049","+919817526747","+918884781268","+918050529348","+919606333566","+918296137566","+917204489655","+918861004710","+918277208895","+919831505081","+917204961211"]
print("Send the message for ",len(phone_nos), "people")
#Get the names of the participipants
names=["Prasanna"]
#Get the payment reference for each participants
#payment_ref=["dummy_payment_id"]

with open('lions.json','r') as f:
    data = json.load(f)
# for i in data:
#     if i["President Contact"]:
#         print(i)

path="/Users/akshaykanive/Desktop/holi_poster.jpeg"

message="""
Warm Rotaract Greetings,

Hope you're doing well. 

We, the Rotaract club of Bangalore Jayanagar and Rotaract Bangalore West are bringing to you, the biggest and the first-ever Holi Celebration Event of RI District 3190 happening on 04 March 2023.  

The Individual registrations are now open at rotaractjayanagar.org

We are looking forward for your collaboration, the collaboration criteria is as follows:

📌 *Collaboration Criteria* - 15 registrations for institution-based clubs and 10 registrations for community-based clubs.

We have special offers for bulk registrations. 
Looking forward to hosting your club, come join us to celebrate Holi not only to spread joy but also to serve a cause.

Please get in touch with regards to the registrations.
"""

for i in range(len(data)):
    #message="Hello "+names[i]+",\nThank you for registerting for Rotaract Dist Conference.\n\n*Payment Reference:* "+payment_ref[i]+"\nPlease find the attached pass for the same. \nThis is an automated message. Please do not reply.😂 \n"
    if i["President Contact"]:
        pywhatkit.sendwhats_image(i["President Contact"],path,message,tab_close=True,close_time=8)