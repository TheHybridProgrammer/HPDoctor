package com.hybridco.android.hpdoctor.meddata;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MedDataUtils {
    static JSONObject jsonObjectGroup;
    static JSONObject jsonObjectChild;

    /** Writes to a json object and then stores it in MedicalData.json */
    public static void writeToJsonChild(Activity activity, Context context, String groupName,
                                        String childName, String input) {

        try {
            String jsonString = getJsonString(context);
            if (jsonString != null) {
                jsonObjectGroup = new JSONObject(jsonString);
                if(jsonObjectGroup.has(groupName)) {
                    jsonObjectChild = new JSONObject(jsonObjectGroup.get(groupName).toString());
                    jsonObjectChild.put(childName, input);
                } else {
                    jsonObjectChild = new JSONObject();
                    jsonObjectChild.put(childName, input);
                }
                jsonObjectGroup.put(groupName, jsonObjectChild);
            } else {
                jsonObjectGroup = new JSONObject();
                jsonObjectChild = new JSONObject();
                jsonObjectChild.put(childName, input);
                jsonObjectGroup.put(groupName, jsonObjectChild);
            }
        } catch (JSONException e) {
            Log.e("JSON", e + " in writeJsonToChild");
        }

        String jsonString = jsonObjectGroup.toString();
        writeToFile(jsonString, activity);
    }

    /** Returns the string inside the json file/object */
    public static String getJsonString(Context context) {
        File file = new File(context.getFilesDir(), "MedicalData.json");
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
        Log.e("FileWriter", ioe + " in MedDataUtils.getJsonString");
        }
        return jsonString;
    }

    /** Returns everything left to the : if : exists else the entire string*/
    public static String clearChildString(String childText) {
        int charStopper = childText.indexOf(":");
        if (charStopper != -1) {
            return childText.substring(0, charStopper);
        } else {
            return childText;
        }
    }

    /** Writes to MedicalData.json file */
    public static void writeToFile(String jsonString, Activity activity) {
        File file = new File(activity.getFilesDir(), "MedicalData.json");
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonString);
            bufferedWriter.close();
        } catch (IOException ioe) {
            Log.e("FileWriter", ioe + "in MedDataUtils.writeToFile");
        }
    }
}