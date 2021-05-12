package com.hybridco.android.hpdoctor.meddata;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hybridco.android.hpdoctor.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MedDataDownload {

    public static void downloadData(Activity activity) {
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        try {

            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            InputStream in = new FileInputStream("/data/data/com.hybridco.android.hpdoctor/files/" + "MedicalData.json");
            OutputStream out = new FileOutputStream("/storage/self/primary/Download/" + "MedicalData.json");

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            Toast toast = Toast.makeText(activity, activity.getString(R.string.med_data_download_succesful), Toast.LENGTH_SHORT);
            toast.show();

            }  catch (FileNotFoundException fnfe1) {
                Log.e("tag", fnfe1.getMessage());
                Toast toast = Toast.makeText(activity, activity.getString(R.string.med_data_download_unsuccesful), Toast.LENGTH_SHORT);
                toast.show();
            }
            catch (Exception e) {
                Log.e("tag", e.getMessage());
                Toast toast = Toast.makeText(activity, activity.getString(R.string.med_data_download_unsuccesful), Toast.LENGTH_SHORT);
                toast.show();
            }
    }
}
