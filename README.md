# Hybrid Pocket Doctor - HPDoctor
**HPDoctor** is an Android app that combines multiple medical features.

## Attention! In order for this app to work you need to create a keys.xml file in res/values and have it contain a string resource called google_map_key that holds the Google Cloud API key taken from the account.
## Attention! In order for this app to work you need to copy google-services.json into this project from the firebase project settings.
## These were done to prevent malicious uses of the API keys!

### A navigation drawer for moving trough the application's features.
![Navigation Drawer](/README_resources/NavigationDrawer.png)<br/>
This is the main activity of the app. It's made from a NavigationDrawer that contains a menu and a header.<br/>
The header has a sign-in button when the user isn't signed in ````and a user avatar, name and two buttons, one for cloud upload and another for cloud download when the user is logged-in. The authentication and cloud sync is done trough Firebase.
The menu itself contains a switch case that either stars a fragment for pills reminder and medical data or an activity intent for maps and settings.

### Pills Reminder
It has the option to change currently displayed pills by pressing one of the day buttons which are ordered according to the current day. This is done trough a Constraint Layout.
Only the current quantities of the current day pills can be modified and once it hits 0 the checkbox gets checked and the pill name gets cut through.
The data is store in a json file.<br/>
![Pill Reminder Fragment](/README_resources/PillsReminder.png)

To modify a pill you click it's name and to add a pill you press the + button, both opening a new activity.
![Pill Reminder Modifier](/README_resources/PillsReminderModify.png)<br/>

### Medical Data
Stores and modifies the medical data of the user.
The data is also stored in a json file in order to be able to download and upload data sets.
![Medical Data](/README_resources/MedicalData.png)<br/>

### Map
Uses Google Cloud Map Services to show an interactive map to the user that can be moved trough.<br/>
![Map Activity](/README_resources/Map.png)<br/>
Also uses Google Cloud Location Services to show nearby medical locations to the user and their respective names.
![Map Nearby](/README_resources/MapNearby.png)<br/>

### Settings
Uses preferences to save and display the app settings.<br/>
![Settings Activity](/README_resources/Settings.png)<br/>

