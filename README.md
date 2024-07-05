# CEG-Prints
CEG Prints - Android Mobile application to take printouts from Amenities centre, Anna University Chennai - 600025

* Spending more time in queue just for a printout?
* Waiting soo long for ur pendrive to scan?

* No need to wait for long anymore.. No pendrive.. No email.. Just directly print from ur mobile

* Here comes our Android app “Print”

* Install the app -> Select ur files in phone -> Scan QR code in the monitor….
And….. boomm !!
-> Your printed file is ready!!!!!!!!

Don’t forget to pay your money ;P

Installation Instructions

CEG TECH FORUM (CTF) PROJECTS HAS CREATED A MOBILE APP THAT PROVIDES YOU THE FACILITY TO PRINT YOUR FILES DIRECTLY FROM YOUR PHONE.

APP INSTALLATION<br/>
STEP 1: CONNECT TO OUR WIFI NETWORK  “AMENITIES”

PRINTING FILE USING THE APP <br/>
STEP 1: CONNECT TO OUR WIFI NETWORK   “AMENITIES”<br/>
STEP 2: USE THE ‘Print’ APP AND SELECT THE FILE FROM YOUR PHONE TO PRINT AND CLICK ‘PRINT’.<br/>
STEP 3: SELECT THE PRINTING PROPERTIES PROVIDED. (No.of.copies) (Single-sided/double-sided)<br/>
STEP 4: SCAN THE QR CODE DISPLAYED IN THE MONITOR. (adjust your phone’s distance from the monitor to scan properly)<br/>
Your file will be printed. Click OK to redirect to the home page. <br/>

Sreenshots:<br/>
Android application:
<div style="display: flex; justify-content: space-around;">
    <img src="https://github.com/ashwin63/CEG-Prints/assets/26385060/f2ba5fe0-51e6-40e3-bf37-3db7a1d5e22d" alt="Image 1" style="width: 23%;"/>
    <img src="https://github.com/ashwin63/CEG-Prints/assets/26385060/5efa2c37-f558-47be-9c3a-e5f7b1045c18" alt="Image 2" style="width: 23%;"/>
    <img src="https://github.com/ashwin63/CEG-Prints/assets/26385060/85dc0fe1-54e8-4ae2-895a-03db78bb82e8" alt="Image 3" style="width: 23%;"/>
    <img src="https://github.com/ashwin63/CEG-Prints/assets/26385060/3310990a-529b-401f-a320-2ab701d6508f" alt="Image 4" style="width: 23%;"/>
</div>

Windows application: 
<img width="1405" alt="Screenshot 2024-07-05 at 11 01 14 AM" src="https://github.com/ashwin63/CEG-Prints/assets/26385060/ab8af1db-33b5-4946-9a6b-51990b32f6f6">

Implementation details: This project is implemented in 2 parts. 
Part 1: 1 mobile application called 'CEG Prints' was provided to the consumers to select the files and choose different options to print ( No of pages, Black/ Color) and scan a QR code in the printing center they wish to initiate the process.
Part 2: A windows application was built and deployed in the printing centres. This has 2 functionalities again, 1 of which is to provide a QR code to be scanned by the users to initiate the printing process. Second of which is to print the files selected by the users in the mobile application and print them using the printer connected to the system locally.

Details:
Mobile application: Built using Java in Android Studio, this project has 4 different screens displayed to the user.
The users can select the files the files to print using the default file explorer and this will be displayed in a list view with card layout. These cards can be clicked to take a quick look at the files selected and can also be removed using swipe gestures. This first page handles and displays all the files selected by user and moves to second screen when user clicks on print. After which, user needs to scan a QR code which will send the files to a server hosted in firebase.

Windows application: This application generates a new QR code every 30 seconds to avoid abuse by the users. This QR code will be embedded with a unique key ( a combination of date,day and some private key chars) and will be stored in a server, which is checked whenever a mobile user tries to print files. Additionally, the application also checks periodically for any files available to print and once files are available, it reads the metadata of file to understand the print options chosen by user and prints them using the local printer connected to them.

Tech utilized: Java, Android Studio, Server creation using Tomcat, Firebase, Python

