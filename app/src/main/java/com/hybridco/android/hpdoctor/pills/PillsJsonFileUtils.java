package com.hybridco.android.hpdoctor.pills;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.hybridco.android.hpdoctor.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class PillsJsonFileUtils {
    public static List<String> getSelectedDayPillsNames (Context context, String day) {
        List<String> pillsNamesList = new ArrayList<>();

        try {
            String jsonString = PillsUtils.getJsonString(context);
            JSONObject jsonPillNameObject;
            JSONObject jsonPillContentObject;

            if (jsonString == null) {
                return pillsNamesList;
            } else {
                jsonPillNameObject = PillsUtils.getJsonObject(jsonString);
            }

            Iterator<String> keys = jsonPillNameObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
                if (Boolean.parseBoolean(jsonPillContentObject.get(day).toString())) {
                    pillsNamesList.add(key);
                }
            }

        } catch (JSONException e) {
                Log.e("JSON", e + " in pillsJsonFileUtils.getSelectedDayPillNames");
        }
        return pillsNamesList;
    }

    /** Returns all current quantities for selected day pills */
    public static List<Integer> getSelectedDayPillsQuantity (Context context, String day) {
        List<Integer> pillsQuantityList= new ArrayList<>();

        try {
            String jsonString = PillsUtils.getJsonString(context);
            JSONObject jsonPillNameObject;
            JSONObject jsonPillContentObject;

            if (jsonString == null) {
                return pillsQuantityList;
            }else {
                jsonPillNameObject = PillsUtils.getJsonObject(jsonString);
            }

            Iterator<String> keys = jsonPillNameObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
                if (Boolean.parseBoolean(jsonPillContentObject.get(day).toString())) {
                    pillsQuantityList.add(Integer.parseInt(jsonPillContentObject.get("CurrentQuantity").toString()));
                }
            }

        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.getSelectedDayPillsQuantity");
        }
        return pillsQuantityList;
    }

    public static void modifyPillCurrentQuantity(Activity activity, Context context, String key, Integer quantity) {
        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject = new JSONObject();
        JSONObject jsonPillContentObject;
        try {

            jsonPillNameObject = PillsUtils.getJsonObject(jsonString);

            jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
            jsonPillContentObject.put("CurrentQuantity", quantity);
            jsonPillNameObject.put(key, jsonPillContentObject);

        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.modifyPillCUrentQuantity");
        }
        jsonString = jsonPillNameObject.toString();
        Utilities.writeToFile(jsonString, activity, "PillsData.json");
    }

    public static Integer getPillOriginalQuantity(Context context, String key) {
        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject;
        JSONObject jsonPillContentObject;
        Integer originalQuantity = 0;
        try {
            jsonPillNameObject = PillsUtils.getJsonObject(jsonString);

            jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
            originalQuantity = Integer.parseInt(jsonPillContentObject.get("OriginalQuantity").toString());

        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.modifyPillCUrentQuantity");
        }
        return originalQuantity;
    }

    public static Boolean getPillDayCheck(Context context, String key, String day) {
        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject = new JSONObject();
        JSONObject jsonPillContentObject;
        Boolean dayIsChecked = true;

        try {
            jsonPillNameObject = PillsUtils.getJsonObject(jsonString);

            jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
            return(Boolean.parseBoolean(jsonPillContentObject.get(day).toString()));

        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.modifyPillCUrentQuantity");
        }
        return dayIsChecked;
    }

    public static void modifyPill(Activity activity, Context context, String originalPillName,
                                  String currentPillName, Integer quantity, Boolean monday,
                                  Boolean Tuesday, Boolean Wednesday, Boolean Thursday,
                                  Boolean Friday, Boolean Saturday, Boolean Sunday,
                                  ArrayList<String> notificationItems) {

        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject = new JSONObject();
        JSONObject jsonPillContentObject;
        try {
            jsonPillNameObject = PillsUtils.getJsonObject(jsonString);
            jsonPillContentObject = new JSONObject();
            jsonPillContentObject.put("OriginalQuantity", quantity);
            jsonPillContentObject.put("CurrentQuantity", quantity);

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            jsonPillContentObject.put("Current date", formattedDate);

            jsonPillContentObject.put("Monday", monday);
            jsonPillContentObject.put("Tuesday", Tuesday);
            jsonPillContentObject.put("Wednesday", Wednesday);
            jsonPillContentObject.put("Thursday", Thursday);
            jsonPillContentObject.put("Friday", Friday);
            jsonPillContentObject.put("Saturday", Saturday);
            jsonPillContentObject.put("Sunday", Sunday);

            for(int i = 0; i < notificationItems.size(); i++ ) {
                jsonPillContentObject.put("Notification"+i, notificationItems.get(i));
            }

            if(originalPillName == currentPillName) {
                jsonPillNameObject.put(originalPillName, jsonPillContentObject);
            } else {
                jsonPillNameObject.remove(originalPillName);
                jsonPillNameObject.put(currentPillName, jsonPillContentObject);
            }
        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.modifyPillCUrentQuantity");
        }
        jsonString = jsonPillNameObject.toString();
        Utilities.writeToFile(jsonString, activity, "PillsData.json");
    }

    public static ArrayList<String> getNotificationItems(Context context, String key) {
        ArrayList<String> notificationItems = new ArrayList<String>();

        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject = new JSONObject();
        JSONObject jsonPillContentObject;
        try {
            jsonPillNameObject = PillsUtils.getJsonObject(jsonString);
            jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
            for (int i = 0; i < 6; i++) {
                notificationItems.add(jsonPillContentObject.get("Notification"+i).toString());
            }

        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.modifyPillCUrentQuantity");
        }
        return notificationItems;
    }

    public static Boolean getIsChecked(Context context, String key) {
        Boolean isChecked = false;
        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject = new JSONObject();
        JSONObject jsonPillContentObject;
        try {
            jsonPillNameObject = PillsUtils.getJsonObject(jsonString);
            jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
            isChecked = Boolean.parseBoolean(jsonPillContentObject.get("IsChecked").toString());

        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.modifyPillCUrentQuantity");
        }
        return isChecked;
    }

    public static ArrayList<Boolean> getSelectedDayIsCheckedList (Context context, String day) {
        ArrayList<Boolean> isCheckedList = new ArrayList<Boolean>();
        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject = new JSONObject();
        JSONObject jsonPillContentObject;

        try {
            if (jsonString == null) {
                return isCheckedList;
            }else {
                jsonPillNameObject = PillsUtils.getJsonObject(jsonString);
            }

            Iterator<String> keys = jsonPillNameObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
                if (Boolean.parseBoolean(jsonPillContentObject.get(day).toString())) {
                    isCheckedList.add(Boolean.parseBoolean(jsonPillContentObject.get("IsChecked").toString()));
                }
            }

        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.getSelectedDayIsCheckedList");
        }
        return isCheckedList;
    }

    public static void modifyPillIsChecked(Activity activity, Context context, String key, Boolean isChecked) {
        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject = new JSONObject();
        JSONObject jsonPillContentObject;

        try {
            jsonPillNameObject = PillsUtils.getJsonObject(jsonString);

            jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
            jsonPillContentObject.put("IsChecked", isChecked);
            jsonPillNameObject.put(key, jsonPillContentObject);

        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.modifyPillCUrentQuantity");
        }
        jsonString = jsonPillNameObject.toString();
        Utilities.writeToFile(jsonString, activity, "PillsData.json");
    }

    public static Boolean getCurrentDateBool(Context context, String key, String day) {
        Boolean currentDate = false;
        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject;

        try {
            if (jsonString == null) {
                return false;
            } else {
                jsonPillNameObject = PillsUtils.getJsonObject(jsonString);
                JSONObject jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
                currentDate = Boolean.parseBoolean(jsonPillContentObject.get(day).toString());
            }
        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.getCurrentDate");
        }

        return currentDate;
    }

    public static String getCurrentDateString(Context context, String key) {
        String currentDate = "";
        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject;

        try {
            jsonPillNameObject = PillsUtils.getJsonObject(jsonString);
            JSONObject jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
            currentDate = (jsonPillContentObject.get("Current date").toString());
        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.getCurrentDate");
        }

        return currentDate;
    }

    public static void modifyPillCurrentDate(Activity activity, Context context, String key) {
        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject = new JSONObject();
        JSONObject jsonPillContentObject;
        try {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String currentDate = df.format(c);

            jsonPillNameObject = PillsUtils.getJsonObject(jsonString);
            jsonPillContentObject = (JSONObject) jsonPillNameObject.get(key);
            jsonPillContentObject.put("Current date", currentDate);
            jsonPillNameObject.put(key, jsonPillContentObject);

        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.modifyPillCUrentDate");
        }
        jsonString = jsonPillNameObject.toString();
        Utilities.writeToFile(jsonString, activity, "PillsData.json");
    }

    public static boolean searchForPill(Context context, String key) {
        String jsonString = PillsUtils.getJsonString(context);
        JSONObject jsonPillNameObject;
        boolean pillFound = false;

        try {
            jsonPillNameObject = PillsUtils.getJsonObject(jsonString);
            if (jsonPillNameObject.get(key) != null) {
                pillFound = true;
            }
        } catch (JSONException e) {
            Log.e("JSON", e + " in pillsJsonFileUtils.modifyPillCurrentDate");
        }
        return pillFound;
    }
}
