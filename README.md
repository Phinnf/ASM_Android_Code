# 💰 BudgetWise Solutions

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)

> A comprehensive personal finance and productivity application designed for students.
---
## 📖 Table of Contents
- [About the Project](#-about-the-project)
- [Key Features](#-key-features)
- [Screenshots](#-screenshots)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Installation](#-installation)
- [Contributors](#-contributors)

---

## 📱 About the Project

**BudgetWise Solutions** is a native Android application developed to help users manage their personal finances effectively. It addresses the common problem of overspending by allowing users to track daily expenses, set category-based budgets, and visualize their spending habits through dynamic charts.

Beyond finance, the app serves as a "Student Productivity Hub" by integrating a To-Do List and a Calculator into a single interface.

---

## ✨ Key Features

### 🔐 User Management
* **Secure Authentication:** Login and Register with email validation.
* **Auto-Login:** "Remember Me" functionality using SharedPreferences.
* **Profile Management:** Edit name, password, or permanently delete account data.

### 💸 Financial Tracking
* **Expense Logging:** Add expenses with categories (Food, Rent, Transport, etc.) and dates.
* **Budget Control:** Set monthly limits for specific categories.
* **Visual Analytics:**
    * **Bar Charts:** View spending by Day, Week, Month, or Year.
    * **Traffic Light System:** Progress bars turn Green 🟢, Amber 🟠, or Red 🔴 based on budget usage.

### 🛠️ Productivity Tools
* **Smart Notes:** A To-Do list with add, delete, and "strikethrough" completion features.
* **In-App Calculator:** Perform quick math without leaving the app.
* **Dark Mode:** Toggle between Light and Dark themes for comfortable viewing.

---

## 📸 Screenshots

| **Login & Auth** | **Dashboard** | **Expense Tracking** |
|:---:|:---:|:---:|
| <img src="screenshots/login.png" width="200"> | <img src="drawable/dashboard.png" width="200"> | <img src="drawable/add_expense.png" width="200"> |
| *Secure Login* | *Financial Overview* | *Add Transactions* |

| **Analytics** | **Notes** | **Settings** |
|:---:|:---:|:---:|
| <img src="drawable/analytics.png" width="200"> | <img src="drawable/notes.png" width="200"> | <img src="drawable/settings.png" width="200"> |
| *Budget Health* | *Task Manager* | *Dark Mode & Profile* |

> *Note: Replace the image paths above with your actual screenshot files.*

---

## 🛠 Tech Stack

* **Language:** Java (JDK 8+)
* **Database:** SQLite (Custom `DatabaseHelper` class)
* **IDE:** Android Studio Ladybug / Koala
* **Libraries:**
    * **MPAndroidChart:** For rendering Bar Charts.
    * **Rhino:** For evaluating mathematical expressions in the Calculator.
    * **Material Design:** For UI components (Cards, Floating Action Buttons, Bottom Navigation).

---

## 🏗 Architecture

The project follows a **MVC (Model-View-Controller)** pattern:

* **📂 Model:**
    * `DatabaseHelper.java`: Manages SQL tables (Users, Expenses, Budgets, Notes).
    * POJO Classes: `Note.java`, `HistoryItem.java`.
* **📱 View:**
    * XML Layouts (Activity & Recycler Items).
    * `ExpenseAdapter.java`, `NoteAdapter.java`.
* **🎮 Controller:**
    * Activities (`DashboardActivity`, `BudgetActivity`, etc.) handle business logic and user interaction.

---

## 🚀 Installation

1.  **Clone the repo:**
    ```bash
    git clone [https://github.com/YourUsername/BudgetWise-Solutions.git](https://github.com/YourUsername/BudgetWise-Solutions.git)
    ```
2.  **Open in Android Studio:**
    * File -> Open -> Select the cloned folder.
3.  **Sync Gradle:**
    * Allow Android Studio to download dependencies (MPAndroidChart, etc.).
4.  **Run the App:**
    * Connect an Android device or use the Emulator (API Level 24+ recommended).

---

## 👥 Contributors
* **Student Name: Phu Tuong Long, Ho Duc Duong
* **Tutor:** Do Trung Anh
---
*This project is submitted as part of the BTEC Level 5 Higher National Diploma in Computing.*