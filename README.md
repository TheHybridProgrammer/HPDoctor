# Hybrid Pocket Doctor - HPDoctor
**HPDoctor** is an Android app that combines multiple features such as:
### A navigation drawer for moving trough the application's features.
![Navigation Drawer](/README_resources/NavigationDrawer.png)<br/>
This is the main activity of the app. It's made from a NavigationDrawer that contains a menu and a header.
The header has a sign-in button when the user isn't signed in and a user avatar, name and two buttons,
one for cloud upload and another for cloud download when the user is logged-in. The authentication
and cloud sync is done trough Firebase.
The menu itself contains a switch case that either stars a fragment for pills reminder and medical data
or an activity intent for maps and settings.
