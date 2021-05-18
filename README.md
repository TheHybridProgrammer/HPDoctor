# Hybrid Pocket Doctor - HPDoctor
**HPDoctor** is an Android app that combines multiple features such as:
### A navigation drawer for moving trough the application's features.
![Navigation Drawer](/README_resources/NavigationDrawer.png)<br/>
This is the main activity of the app. It's made from a NavigationDrawer that contains a menu and a header.<br/>
The header has a sign-in button when the user isn't signed in and a user avatar, name and two buttons,
one for cloud upload and another for cloud download when the user is logged-in. The authentication
and cloud sync is done trough Firebase.
The menu itself contains a switch case that either stars a fragment for pills reminder and medical data<br/>
or an activity intent for maps and settings.

### Pills Reminder
It has the option to change currently displayed pills by pressing one of the day buttons which are <br/>
ordered according to the current day. This is done trough a Constraint Layout.</br>
Only current day the pill quantity of the current day pills can be modified and once it hits 0 </br>
the checkbox gets checked and the pill name gets cut through.<br/>
![Pill Reminder Fragment](/README_resources/PillsReminder.png)<br/>

To add or modify a pill
![Pill Reminder Modifier](/README_resources/PillsReminderModify.png)<br/>

### Medical Data
![Medical Data](/README_resources/MedicalData.png)<br/>

### Map
![Map Activity](/README_resources/Map.png)<br/>

![Map Nearby](/README_resources/MapNearby.png)<br/>

### Settings
![Settings Activity](/README_resources/Settings.png)<br/>

