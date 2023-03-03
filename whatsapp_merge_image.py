"""
This code is developed for sending automated whatsapp messages along with an image.
This is intended to be used for Rotaract Purposes only.

Developed and maintained by: Rtn. PP. Rtr. Akshay Kumar Kanive M

In case of any queries, please write to akshaykanive@gmail.com
"""
#Import required modules
import pywhatkit

message="""
Warm Rotaract Greetings!

Thank you for registering for HoliYay!
Here is your pass for the event. Please ensure to check the rules and regulations for the event.
https://bit.ly/holiyay2023

Awaiting to see you there!
"""

#Read the registration file
reg_file = open("holi_2.csv", "r")

for registration in reg_file:
    registration=registration.strip()
    reg_info=registration.split(",")
    
    #Get the phone numbers. Not sure what happends if non-whatsapp number is given.
    phone_no=reg_info[3] 
    #phone_no="+919060375749"

    #Get the names of the participipants
    names=reg_info[1]

    #Get the payment reference for each participants
    payment_ref=reg_info[2]
    path="./Pass/"+payment_ref+".png"
    
    pywhatkit.sendwhats_image(phone_no,path,message,tab_close=True,close_time=8)