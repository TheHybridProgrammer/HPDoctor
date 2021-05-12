package com.hybridco.android.hpdoctor.meddata;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.hybridco.android.hpdoctor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MedDataExpandableListData {
    public static LinkedHashMap<String, List<String>> getData(Activity activity) {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();
        int group = 0, child = 0;

        List<String> medDataBody = new ArrayList<String>();
        medDataBody.add(activity.getString(R.string.med_data_weight) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBody.add(activity.getString(R.string.med_data_height) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBody.add(activity.getString(R.string.med_data_age) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBody.add(activity.getString(R.string.med_data_dateofbirth) + getJsonText(String.valueOf(group), String.valueOf(child++), activity)); // MAYBE WILL MODIFY WITH USER DATA
        medDataBody.add(activity.getString(R.string.med_data_sex) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBody.add(activity.getString(R.string.med_data_eyecolor) + getJsonText(String.valueOf(group++), String.valueOf(child++), activity));
        child = 0;

        List<String> medDataBlood = new ArrayList<String>();
        medDataBlood.add(activity.getString(R.string.med_data_blood_group) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBlood.add(activity.getString(R.string.med_data_blood_RBC) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBlood.add(activity.getString(R.string.med_data_blood_WBC) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBlood.add(activity.getString(R.string.med_data_blood_cholesterol) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBlood.add(activity.getString(R.string.med_data_blood_platelets) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBlood.add(activity.getString(R.string.med_data_blood_hematocrit) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBlood.add(activity.getString(R.string.med_data_blood_bloodglucose) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBlood.add(activity.getString(R.string.med_data_blood_calcium) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataBlood.add(activity.getString(R.string.med_data_blood_electrolytes) + getJsonText(String.valueOf(group++), String.valueOf(child++), activity));
        child = 0;

        List<String> medDataEyes= new ArrayList<String>();
        medDataEyes.add(activity.getString(R.string.med_data_eyes_diopter) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataEyes.add(activity.getString(R.string.med_data_eyes_acuity) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataEyes.add(activity.getString(R.string.med_data_eyes_refraction) + getJsonText(String.valueOf(group++), String.valueOf(child++), activity));
        child = 0;

        List<String> medDataKidneys= new ArrayList<String>();
        medDataKidneys.add(activity.getString(R.string.med_data_kidneys_ACR) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));
        medDataKidneys.add(activity.getString(R.string.med_data_kidneys_GFR) + getJsonText(String.valueOf(group), String.valueOf(child++), activity));

        expandableListDetail.put(activity.getString(R.string.med_data_body), medDataBody);
        expandableListDetail.put(activity.getString(R.string.med_data_blood), medDataBlood);
        expandableListDetail.put(activity.getString(R.string.med_data_eyes), medDataEyes);
        expandableListDetail.put(activity.getString(R.string.med_data_kidneys), medDataKidneys);
        Log.e("ER", expandableListDetail.toString());
        return expandableListDetail;
    }

    /** Return the json under string form */
    public static String getJsonText(String groupName, String childKey, Context context) {
        String jsonString, childText = null;

        try {
            jsonString = MedDataUtils.getJsonString(context);

            if (jsonString != null) {
                JSONObject jsonObjectGroup = new JSONObject(jsonString);
                JSONObject jsonObjectChild = new JSONObject(jsonObjectGroup.get(groupName).toString());
                childText = jsonObjectChild.get(childKey).toString();
            }
        } catch (JSONException e) {
            Log.e("JSON", e + " in MedDataExpandableListData.getJsonText ");
        }

        if (childText != null) {
            return childText;
        } else {
            return "";
        }
    }
}
