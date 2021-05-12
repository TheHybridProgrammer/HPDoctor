package com.hybridco.android.hpdoctor.meddata;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MedDataFileUpload {

    /** Reads a file inside external storage and returns it as a string */
    public static String getFileJson(Uri fileUri, Context context) {
        String jsonString = "";

        try {
            InputStream is = context.getContentResolver().openInputStream(fileUri);
            int i;
            char c;
            while((i = is.read())!=-1) {

                // converts integer to character and appends it to jsonString
                c = (char)i;
                jsonString = jsonString + c;
            }
        } catch (IOException ioe) {
            Log.e("FileReader", ioe.toString());
        }
        if (jsonString != "") {
            return jsonString;
        } else return "ERROR";
    }

    /** Returns a jsonObject from a jsonString */
    public static JSONObject getJsonObject(String jsonString) {
        JSONObject jsonObjectGroup = null;
        try {
            if (jsonString != null) {
                jsonObjectGroup = new JSONObject(jsonString);
            }
        } catch (JSONException e) {
            Log.e("JSON", "Can't write to json in writeJsonToChild");
        }
        return jsonObjectGroup;
    }

    /** Compares an uploaded jsonObject with local json object if any, else it becomes the object */
    public static void jsonUploadCompare (Activity activity, Context context,
                                          String uploadedJsonString) {
        String localJsonString = MedDataUtils.getJsonString(context);

        if (localJsonString != null) {
            JSONObject uploadedJsonObjectChild, uploadedJsonObjectGroup =
                    getJsonObject(uploadedJsonString);
            JSONArray uploadedJsonChildKeys, uploadedJsonGroupKeys = uploadedJsonObjectGroup.names();
            String uploadedJsonGroupKey, uploadedJsonChildKey, uploadedJsonChildValue;

            JSONObject localJsonObjectGroup = getJsonObject(localJsonString);

            for (int i = 0; i < uploadedJsonGroupKeys.length(); i++) {

                try {
                    uploadedJsonObjectChild = (JSONObject) uploadedJsonObjectGroup.get
                            (uploadedJsonGroupKeys.getString(i));

                    // Gets child keys
                    uploadedJsonChildKeys = uploadedJsonObjectChild.names();

                    // Gets current group key
                    uploadedJsonGroupKey = uploadedJsonGroupKeys.get(i).toString();

                    for (int j = 0; j < uploadedJsonChildKeys.length(); j++) {

                        // Gets current child value & key
                        uploadedJsonChildKey = uploadedJsonChildKeys.get(j).toString();
                        uploadedJsonChildValue = uploadedJsonObjectChild.get
                                (uploadedJsonChildKeys.getString(j)).toString();

                        // Replaces local medical data with uploaded data
                        if (compareJsons(localJsonObjectGroup, uploadedJsonGroupKey,
                                uploadedJsonChildKey, uploadedJsonChildValue)) {
                            MedDataUtils.writeToJsonChild(activity, context, uploadedJsonGroupKey,
                                    uploadedJsonChildKey, uploadedJsonChildValue);
                        }

                    }

                }catch(JSONException e) {
                    Log.e("JSON", e + " in method jsonUploadCompare");
                }
            }
        } else {
            MedDataUtils.writeToFile(uploadedJsonString, activity);
        }
    }

    /** Returns true if json objects have common group&child key and common child key value
     *  is not null, otherwise false */
    public static boolean compareJsons(JSONObject localJsonObjectGroup,
                                          String uploadedJsonGroupKey,
                                          String uploadedJsonChildKey,
                                          String uploadedJsonChildValue) {
        JSONObject localJsonObjectChild;
        String localJsonChildValue;
        boolean replaceValue = false;

        try {
            localJsonObjectChild = new JSONObject(localJsonObjectGroup.get(uploadedJsonGroupKey).
                    toString());
            localJsonChildValue = localJsonObjectChild.get(uploadedJsonChildKey).toString();

            if (localJsonChildValue.replaceAll("\\s+","") != "" &&
                    uploadedJsonChildValue != localJsonChildValue) {
                    replaceValue = true;
            } else {
                replaceValue = false;
            }

        }catch(JSONException e) {
            Log.e("JSON", e + " in method compareJsons");
            replaceValue = false;
        }

        return replaceValue;

    }
}
