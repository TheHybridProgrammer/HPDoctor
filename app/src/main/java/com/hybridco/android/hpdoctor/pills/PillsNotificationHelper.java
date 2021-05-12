package com.hybridco.android.hpdoctor.pills;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.hybridco.android.hpdoctor.MainNavigationDrawerActivity;
import com.hybridco.android.hpdoctor.R;

import java.util.Calendar;

public class PillsNotificationHelper extends ContextWrapper {
    public static String channelID;
    public static String channelName;
    public int alarmName;

    private NotificationManager mManager;
    SharedPreferences prefs = getSharedPreferences("pillsPreferences", MODE_PRIVATE);

    /** Calls the creation of notification channel and sets the alarm name from outside the class
     * using shared preferences */
    public PillsNotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            channelID = prefs.getString("key", "No name defined"); //"No name defined" is the default value.
            channelName = prefs.getString("key", "No name defined");
            alarmName = prefs.getInt("alarmName", 0);
            createChannel();
        }
    }

    /** Creates a notification channel */
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName,
                NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        Intent resultIntent = new Intent(this, MainNavigationDrawerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, alarmName, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Este timpul să iei " + channelName + ".")
                .setSmallIcon(R.mipmap.ic_hpdoctor)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
    }

    public static boolean checkIfDayIsSelected(int day, boolean monday, boolean tuesday, boolean wednesday,
                                    boolean thursday, boolean friday, boolean saturday,
                                    boolean sunday) {
        switch (day) {
            case 1:
                if (sunday == true) return true;
                break;
            case 2:
                if (monday == true) return true;
                break;
            case 3:
                if (tuesday == true) return true;
                break;
            case 4:
                if (wednesday == true) return true;
                break;
            case 5:
                if (thursday == true) return true;
                break;
            case 6:
                if (friday == true) return true;
                break;
            case 7:
                if (saturday == true) return true;
                break;
        }
        return false;
    }
}
