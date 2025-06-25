 Budget Tracker App
 
https://github.com/Siyamazi/PROG7313POE 
https://youtu.be/5POraCP_7gg 

A powerful yet simple Android budget tracking app designed to help users manage their monthly expenses, set financial goals, and visualize spending habits. Developed with Kotlin and Firebase the app is optimized for both functionality and user experience.

 Features

Expense Tracking
- Add/Delete Expenses** with fields: `Title`, `Amount`, `Category`, `Note`, 
- A list of the added expense can then be view with a timestamp of when it was created
- After the expenses have be created the user can go to the expense list remove any unwanted expenses 

 Expense Summary
- Interactive Pie Chart visualization of spending by category.
- Total spent per category displayed clearly for insights as a percentage.
- Helps users understand where most of their money is going.
- Interactive and automatically updates based on current month.

 Budget Goals
- Users can set a Monthly Budget Goal.
- Notifications or warnings if the budget is close to or has been exceeded.

 Categories Management
- Built-in default categories: Food, Transport, Electronics, Fast Food, etc.
- Users can create **custom categories** under the category button.
- Filter expenses by category.

 Cloud Sync with Firebase
- All user data (expenses, categories, profile images) stored securely in Firebase Firestore.
- Firebase Storage used for storing profile.


 User Profile
- Upload a profile picture.
- Profile image displayed on the Dashboard.

 Date-based Filtering
- Expenses can be filtered by specific date.
- Enables focused analysis of spending during specific timeframes.

 Floating Action Buttons (FABs)
- Custom FAB navigation:
  - Bottom-left FAB → Summary Page
  - Bottom-center FAB → Dashboard
  - Top-left FAB → Back (if not on dashboard)

---

 Tech Stack

- **Language**: Kotlin
- Architecture: MVVM (Model-View-ViewModel)
- Cloud Backend: Firebase Firestore
- Image Storage: Firebase Storage
- UI: Android Views with custom layouts
- Authentication 

App personal features 
- In the expense list the user can swipe left to delete an a aexpense thathas been added
- A warning pop up message is show to the user when the total expense is greater than the monthly set budget
- A user can uplaod a profile picture  by tapping on the profile picture icon found on the top right of the app's screen


