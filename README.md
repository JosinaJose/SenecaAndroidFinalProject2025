# SenecaAndroidFinalProject2025

## Problem Definition

**Problem Summary**  
As a startup grows, managing employee email accounts manually becomes difficult. Creating accounts, assigning company domains, and generating secure passwords is time-consuming and prone to mistakes, which can reduce productivity.  

**Solution**  
Safe Mail is an Android application developed using **Kotlin** and **Jetpack Compose**. It automates email account creation, applies a secure password policy, and manages employee credentials efficiently.  

**Modules:**  
- Admin  
- Employee  
- News  

---

## System Overview

Safe Mail provides administrators with a secure and efficient way to manage employee emails while staying updated with business-related news.  

**Databases Used:**  
- **MySQL:** Stores admin signup, login, and profile data.  
- **Firebase Firestore:** Stores and manages employee data with real-time access.  
- **Room Database:** Stores workspace features (Sticky Notes, To-Do tasks, reminders, events) and saved news articles locally.  

This multi-database approach ensures separation, performance, and security.

---

## Application Features

### App Launch & Admin Authentication
- Custom splash screen and logo.  
- Admin login with email and password.  
- Admin account creation includes company name, personal info, and secure password.  

### Home Screen
- Connect business tools like Outlook and Slack.  
- View active/inactive employees.  
- Edit employee details and manage admin profile.  

### Navigation
- Bottom Bar with Home, Staff, Workspace (+), News, and More.  
- Workspace includes To-Do List, Sticky Notes, and productivity tools.  

### Staff Module
- Add employees with auto-generated email and passwords.  
- Emails use the company domain.  
- Password policy applied for security.  
- Data stored securely in Firebase Firestore.  

### News Module
- Fetches content using [NewsAPI](https://newsapi.org/).  
- Categories: Business, Technology, Health, Environment, Science.  
- Search and save articles for later reading using Room Database.  

---

## How to Use the Application

### Prerequisites
Before running Safe Mail, make sure you have:  
- Android Studio (latest version)  
- VS Code for backend development  
- Node.js & npm  
- MySQL & MySQL Workbench  
- Firebase account  
- Git  

### Setup Instructions

1. **Clone the Project:**  
2. **Backend Setup:**
   - Navigate to /Backend folder
   - Install dependencies:
     
     ```
      npm install
     ```
   - Create a .env file with the following variables:
       ### Backend Environment Variables

Create a `.env` file in the backend folder and set the following:

```env
DB_HOST=localhost
DB_USER=<your_mysql_username>
DB_PASSWORD=<your_mysql_password>
PORT=3000
```
   Ensure safemail database exists in MySQL.
Firebase Setup:

 ### Create a Firebase project and enable Firestore.

Register the Android app in Firebase.

Download google-services.json and place it in the /app directory.

### News API Configuration:

Obtain an API key from NewsAPI
.

Add it to Constants.kt in the components package.
