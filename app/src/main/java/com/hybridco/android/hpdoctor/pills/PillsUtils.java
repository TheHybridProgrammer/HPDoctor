package com.hybridco.android.hpdoctor.pills;

import com.hybridco.android.hpdoctor.R;
import com.hybridco.android.hpdoctor.utilities.Utilities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PillsUtils {

    /** Returns a jsonObject in order to skip catch */
    public static JSONObject getJsonObject(String jsonString) {
        JSONObject jsonObject = null;
        try {
            if (jsonString != null) {
                jsonObject = new JSONObject(jsonString);
            }
        } catch (JSONException e) {
            Log.e("JSON", e + " in pills getJsonObject");
        }
        return jsonObject;
    }

    /** Returns the content of the PillsData.json under string form */
    public static String getJsonString(Context context) {
        File file = new File(context.getFilesDir(), "PillsData.json");
        String jsonString = null;
        try {
            FileReader fileReader = new FileReader(file);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();

            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            jsonString = stringBuilder.toString();

        } catch (IOException ioe) {
            Log.e("FileWriter", ioe + " in pills getJsonString");
        }
        return jsonString;
    }

    /** Writes a new pill into PillsData.json */
    public static void writeToJson(Activity activity, Context context, String pillName, Integer quantity,
                            Boolean monday, Boolean Tuesday, Boolean Wednesday, Boolean Thursday,
                            Boolean Friday, Boolean Saturday, Boolean Sunday,
                                   ArrayList<String> notificationItems) {

        String jsonString = getJsonString(context);
        JSONObject jsonPillNameObject;
        if (jsonString != null) {
            jsonPillNameObject = getJsonObject(jsonString);
        } else {
            jsonPillNameObject = new JSONObject();
        }

        try {
            JSONObject jsonPillContents = new JSONObject();
            jsonPillContents.put("OriginalQuantity", quantity);
            jsonPillContents.put("CurrentQuantity", quantity);

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            jsonPillContents.put("Current date", formattedDate);

            jsonPillContents.put("IsChecked", false);

            jsonPillContents.put("Monday", monday);
            jsonPillContents.put("Tuesday", Tuesday);
            jsonPillContents.put("Wednesday", Wednesday);
            jsonPillContents.put("Thursday", Thursday);
            jsonPillContents.put("Friday", Friday);
            jsonPillContents.put("Saturday", Saturday);
            jsonPillContents.put("Sunday", Sunday);

            for(int i = 0; i < notificationItems.size(); i++ ) {
                jsonPillContents.put("Notification"+i, notificationItems.get(i));
            }

            jsonPillNameObject.put(pillName, jsonPillContents);
        } catch (JSONException e) {
            Log.e("JSON", e + " in PillsUtils.writeToJson");
        }
        jsonString = jsonPillNameObject.toString();
        Utilities.writeToFile(jsonString, activity, "PillsData.json");
    }

    public static void removeJsonEntry(Activity activity, Context context, String toDelete) {
        String jsonString = getJsonString(context);

        JSONObject jsonPillNameObject;
        if (jsonString != null) {
            jsonPillNameObject = getJsonObject(jsonString);
        } else {
            jsonPillNameObject = new JSONObject();
        }

        jsonPillNameObject.remove(toDelete);

        jsonString = jsonPillNameObject.toString();

        Utilities.writeToFile(jsonString, activity, "PillsData.json");
    }

    /** Switch case to arrange buttons depending on current day */
    public static ConstraintSet setDayButtonsOrder (ConstraintSet constraintSet) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int buttonMonday = R.id.button_monday, buttonTuesday = R.id.button_tuesday,
                buttonWednesday = R.id.button_wednesday, buttonThursday = R.id.button_thursday,
                buttonFriday = R.id.button_friday, buttonSaturday = R.id.button_saturday,
                buttonSunday = R.id.button_sunday;

        switch(day) {
            case Calendar.MONDAY:
                constraintSet = setButtonsDaySwitchCase(constraintSet, buttonMonday, buttonTuesday,
                        buttonWednesday, buttonThursday, buttonFriday, buttonSaturday, buttonSunday);
                break;

            case Calendar.TUESDAY:
                constraintSet = setButtonsDaySwitchCase(constraintSet, buttonTuesday, buttonWednesday,
                        buttonThursday, buttonFriday, buttonSaturday, buttonSunday, buttonMonday);
                break;

            case Calendar.WEDNESDAY:
                constraintSet = setButtonsDaySwitchCase(constraintSet, buttonWednesday, buttonThursday,
                        buttonFriday, buttonSaturday, buttonSunday, buttonMonday, buttonTuesday);
                break;

            case Calendar.THURSDAY:
                constraintSet = setButtonsDaySwitchCase(constraintSet, buttonThursday, buttonFriday,
                        buttonSaturday, buttonSunday, buttonMonday, buttonTuesday, buttonWednesday);
                break;

            case Calendar.FRIDAY:
                constraintSet = setButtonsDaySwitchCase(constraintSet, buttonFriday, buttonSaturday,
                        buttonSunday, buttonMonday, buttonTuesday, buttonWednesday, buttonThursday);
                break;

            case Calendar.SATURDAY:
                constraintSet = setButtonsDaySwitchCase(constraintSet, buttonSaturday, buttonSunday,
                        buttonMonday, buttonTuesday, buttonWednesday, buttonThursday, buttonFriday);
                break;

            case Calendar.SUNDAY:
                constraintSet = setButtonsDaySwitchCase(constraintSet, buttonSunday, buttonMonday,
                        buttonTuesday, buttonWednesday, buttonThursday, buttonFriday, buttonSaturday);
                break;
        }

        return constraintSet;
    }

    /** Receives 7 button integers and a ConstraintSet and connects each end to previous
     * button start, then return the ConstrainSet */
    public static ConstraintSet setButtonsDaySwitchCase(ConstraintSet constraintSet, Integer button1,
                                                 Integer button2, Integer button3, Integer button4,
                                                 Integer button5, Integer button6, Integer button7) {

        constraintSet.connect(button1, ConstraintSet.START, R.id.pills_buttons_constraint_layout, ConstraintSet.START);
        constraintSet.connect(button2, ConstraintSet.START, button1, ConstraintSet.END);
        constraintSet.connect(button3, ConstraintSet.START, button2, ConstraintSet.END);
        constraintSet.connect(button4, ConstraintSet.START, button3, ConstraintSet.END);
        constraintSet.connect(button5, ConstraintSet.START, button4, ConstraintSet.END);
        constraintSet.connect(button6, ConstraintSet.START, button5, ConstraintSet.END);
        constraintSet.connect(button7, ConstraintSet.START, button6, ConstraintSet.END);

        return constraintSet;
    }

    public static boolean checkCurrentDate(Context context, String pillName) {
        Boolean isCurrentDate = false;
        Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_WEEK);

        switch (currentDay) {
            case Calendar.MONDAY:
                if (PillsJsonFileUtils.getCurrentDateBool(context, pillName, "Monday")) {
                    isCurrentDate = true;
                }
                break;

            case Calendar.TUESDAY:
                if (PillsJsonFileUtils.getCurrentDateBool(context, pillName, "Tuesday")) {
                    isCurrentDate = true;
                }
                break;

            case Calendar.WEDNESDAY:
                if (PillsJsonFileUtils.getCurrentDateBool(context, pillName, "Wednesday")) {
                    isCurrentDate = true;
                }
                break;

            case Calendar.THURSDAY:
                if (PillsJsonFileUtils.getCurrentDateBool(context, pillName, "Thursday")) {
                    isCurrentDate = true;
                }
                break;

            case Calendar.FRIDAY:
                if (PillsJsonFileUtils.getCurrentDateBool(context, pillName, "Friday")) {
                    isCurrentDate = true;
                }
                break;

            case Calendar.SATURDAY:
                if (PillsJsonFileUtils.getCurrentDateBool(context, pillName, "Saturday")) {
                    isCurrentDate = true;
                }
                break;

            case Calendar.SUNDAY:
                if (PillsJsonFileUtils.getCurrentDateBool(context, pillName, "Sunday")) {
                    isCurrentDate = true;
                }
                break;

        }
        return isCurrentDate;
    }
}
