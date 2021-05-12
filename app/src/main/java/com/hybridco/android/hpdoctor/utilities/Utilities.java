package com.hybridco.android.hpdoctor.utilities;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hybridco.android.hpdoctor.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class Utilities {

    /** Sets language based on parameter lang */
    public static void changeLang(String lang, Resources resources) {
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = new Locale(lang);
        resources.updateConfiguration(config, dm);
    }

    /** Writes a string to file */
    public static void writeToFile(String inputString, Activity activity, String fileName) {
        File file = new File(activity.getFilesDir(), fileName);
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(inputString);
            bufferedWriter.close();
        } catch (IOException ioe) {
            Log.e("FileWriter", ioe + " in writeToFile");
        }
    }

    public static void uploadToFirebase(String userName, Activity activity) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Uri meddataFile = Uri.fromFile(new File("/data/data/com.hybridco.android.hpdoctor/files/MedicalData.json"));
        StorageReference meddataRef = storageRef.child(userName + "/" + meddataFile.getLastPathSegment());
        UploadTask uploadTask = meddataRef.putFile(meddataFile);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast toast = Toast.makeText(activity, activity.getString(R.string.drawer_menu_data_upload_fail), Toast.LENGTH_SHORT);
                toast.show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast toast = Toast.makeText(activity, activity.getString(R.string.drawer_menu_data_upload_succesful), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public static void downloadFromFirebase(String userName, Activity activity) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference meddataRef = storageRef.child(userName + "/MedicalData.json");

        File localFile = new File("/data/data/com.hybridco.android.hpdoctor/files/", "MedicalData.json");

        meddataRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast toast = Toast.makeText(activity, activity.getString(R.string.drawer_menu_data_download_succesful), Toast.LENGTH_SHORT);
                toast.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast toast = Toast.makeText(activity, activity.getString(R.string.drawer_menu_data_download_fail), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
