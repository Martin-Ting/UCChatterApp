UCR-ChatterApp
==============
 This application was developed for CS166 Database Management Systems.

Overview
==============
 The motivation for this project was to apply the knowledge learned in CS166 to create a database system for a chat application. I created this Android application as a different way to interact with the database because I found that the command line application required for the project did not provide the same user experience as a mobile application would. 
 There are two clients, one is a command line Java application and the other is an Android mobile application. The backend consists of Java web services and the PostgreSQL database. The Java command line application uses jdbc to connect to PostgreSQL and the Android application uses the web services, nicknames, Hermes to gather data from the PostgreSQL database.
 
Tools used
==============
+ Anndroid Developer Tools for Eclipse IDE
+ NetBeans
+ PostgreSQL
+ Vim

File Structure
==============
+ SQL
  * Folder sql contains all the SQL scripts for the system
  * Folder data contains mock data 
+ Backend/PGcomWS
  * This NeBeans project contains the source code for the web service deployment that the app uses to communicate with PostgreSQL
+ Android
  * This Android project contains a simple application that supports basic chat application functionality
+ Docs
  * This folder contains the documentation produced over the process of this project.
