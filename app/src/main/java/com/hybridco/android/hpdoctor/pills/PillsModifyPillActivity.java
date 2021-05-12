package com.hybridco.android.hpdoctor.pills;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.hybridco.android.hpdoctor.R;
import com.hybridco.android.hpdoctor.utilities.Utilities;

import java.util.ArrayList;
import java.util.Calendar;

public class PillsModifyPillActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    String notificationString, originalPillName;
    Integer quantity;
    EditText pillName;
    Activity activity;
    Calendar c = Calendar.getInstance();
    Integer diferentiator;

    ArrayList<String> notificationItems = new ArrayList<String>();
    ArrayList<String> toDeleteNotificationItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);

        Bundle bundle = getIntent().getExtras();
        String pillNameExtra = bundle.getString("PillName");
        originalPillName = pillNameExtra;

        activity = this;

        //SharedPreference init
        SharedPreferences sharedPref =
                this.getSharedPreferences("com.hybridco.android.hpdoctor_preferences",
                        MODE_PRIVATE);

        String langValue = sharedPref.getString("language_list", "");
        Resources resources = getResources();
        if (langValue.equals("1")) {
            Utilities.changeLang("en", resources);
        } else if (langValue.equals("2")) {
            Utilities.changeLang("ro", resources);
        }

        setContentView(R.layout.pills_modifier_activity);

        pillName = findViewById(R.id.pills_pillname);
        pillName.setText(pillNameExtra);

        // PILL COUNTER
        EditText pillCount = findViewById(R.id.pills_count);
        quantity = PillsJsonFileUtils.getPillOriginalQuantity(activity, pillNameExtra);
        pillCount.setText(quantity.toString());

        Button incrementQuantity = findViewById(R.id.pills_increment_button);
        incrementQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt(pillCount.getText().toString());
                if (quantity < 99) {
                    quantity++;
                    pillCount.setText(quantity.toString());
                } else {
                    Toast toast = Toast.makeText(activity, activity.getString(R.string.pills_max_pillcount), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        Button decrementQuantity = findViewById(R.id.pills_decrement_button);
        decrementQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt(pillCount.getText().toString());
                if (quantity > 1) {
                    quantity--;
                    pillCount.setText(quantity.toString());
                } else {
                    Toast toast = Toast.makeText(activity, activity.getString(R.string.pills_min_pillcount), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        // NOTIFICATIONS
        Button addNotification = findViewById(R.id.pills_add_notification);
        addNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notificationItems.size() < 6) {
                    DialogFragment timePicker = new PillsTimePickerFragment();
                    timePicker.show(getSupportFragmentManager(), "time picker");
                } else {
                    Toast toast = Toast.makeText(activity, activity.getString(R.string.pills_max_notifications), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        ListView notificationsList = findViewById(R.id.pills_notification_list);
        notificationItems = PillsJsonFileUtils.getNotificationItems(activity, bundle.getString("PillName"));
        adapter = new ArrayAdapter<String>(this,
                R.layout.pills_listview_layout, R.id.pills_listview_textview, notificationItems);
        notificationsList.setAdapter(adapter);

        // NOTIFICATIONS DELETE NOTIFICATION
        notificationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle(notificationItems.get(index));

                alert.setMessage(activity.getString(R.string.pills_delete_message));

                alert.setPositiveButton(activity.getString(R.string.pills_delete),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                toDeleteNotificationItems.add(notificationItems.get(index));
                                notificationItems.remove(index);
                                adapter.notifyDataSetChanged();
                            }
                        });

                alert.setNegativeButton(activity.getString(R.string.pills_back),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });

                alert.show();
            }
        });

        // DAY SELECTOR
        ToggleButton mondayButton = findViewById(R.id.pills_monday);
        mondayButton.setChecked((Boolean) PillsJsonFileUtils.getPillDayCheck(activity, bundle.getString("PillName"), "Monday"));

        ToggleButton tuesdayButton = findViewById(R.id.pills_tuesday);
        tuesdayButton.setChecked(PillsJsonFileUtils.getPillDayCheck(activity, bundle.getString("PillName"), "Tuesday"));

        ToggleButton wednesdayButton = findViewById(R.id.pills_wednesday);
        wednesdayButton.setChecked((Boolean) PillsJsonFileUtils.getPillDayCheck(activity, bundle.getString("PillName"), "Wednesday"));

        ToggleButton thursdayButton = findViewById(R.id.pills_thursday);
        thursdayButton.setChecked(PillsJsonFileUtils.getPillDayCheck(activity, bundle.getString("PillName"), "Thursday"));

        ToggleButton fridayButton = findViewById(R.id.pills_friday);
        fridayButton.setChecked(PillsJsonFileUtils.getPillDayCheck(activity, bundle.getString("PillName"), "Friday"));

        ToggleButton saturdayButton = findViewById(R.id.pills_saturday);
        saturdayButton.setChecked(PillsJsonFileUtils.getPillDayCheck(activity, bundle.getString("PillName"), "Saturday"));

        ToggleButton sundayButton = findViewById(R.id.pills_sunday);
        sundayButton.setChecked(PillsJsonFileUtils.getPillDayCheck(activity, bundle.getString("PillName"), "Sunday"));

        // ACTIVITY EXITERS
        // Delete Pill button
        Button deletePill = findViewById(R.id.pills_delete_button);
        deletePill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notificationItems = PillsJsonFileUtils.getNotificationItems(activity, pillName.getText().toString());
                for (int i = 0; i < notificationItems.size(); i++) {
                    diferentiator = notificationItems.get(i).indexOf(":");

                    int hour = Integer.parseInt(notificationItems.get(i).substring(0, diferentiator));

                    int minute = Integer.parseInt(notificationItems.get(i).substring(
                            notificationItems.get(i).lastIndexOf(":") + 1));

                    for (int j = 1; j < 7; j ++) {
                        if (PillsNotificationHelper.checkIfDayIsSelected(j, mondayButton.isChecked(),
                                tuesdayButton.isChecked(), wednesdayButton.isChecked(),
                                thursdayButton.isChecked(), fridayButton.isChecked(),
                                saturdayButton.isChecked(), sundayButton.isChecked())) {

//                                c.set(Calendar.DAY_OF_WEEK, j);
                            c.set(Calendar.HOUR_OF_DAY, hour);
                            c.set(Calendar.MINUTE, minute);
                            c.set(Calendar.SECOND, 0);
                            cancelAlarm(c, pillName.getText().toString(), i, j,
                                    Integer.parseInt(pillCount.getText().toString()));
                        }
                    }
                    PillsUtils.removeJsonEntry(activity, activity, pillName.getText().toString());
                }
                PillsUtils.removeJsonEntry(activity, activity, pillName.getText().toString());
                finish();
            }
        });

        // Modify pill button
        Button modifyButton = findViewById(R.id.pills_modify_button);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pillName.getText().toString().isEmpty() != true &&
                        pillName.getText().toString().replaceAll(" ", "").isEmpty() != true
                        && ( mondayButton.isChecked() || tuesdayButton.isChecked() ||
                        wednesdayButton.isChecked() || thursdayButton.isChecked() ||
                        fridayButton.isChecked() || saturdayButton.isChecked() ||
                        sundayButton.isChecked())) {

                    if (!originalPillName.equals(pillName.getText().toString()) &&
                            PillsJsonFileUtils.searchForPill(activity, pillName.getText().toString())) {
                        Toast toast = Toast.makeText(activity,
                                activity.getString(R.string.pills_modify_duplicate_pill), Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        PillsJsonFileUtils.modifyPill(activity, activity, bundle.getString("PillName"),
                                pillName.getText().toString(), Integer.parseInt(pillCount.getText().toString()),
                                mondayButton.isChecked(), tuesdayButton.isChecked(), wednesdayButton.isChecked(),
                                thursdayButton.isChecked(), fridayButton.isChecked(), saturdayButton.isChecked(),
                                sundayButton.isChecked(), notificationItems);

                        for (int i = 0; i < notificationItems.size(); i++) {
                            diferentiator = notificationItems.get(i).indexOf(":");

                            int hour = Integer.parseInt(notificationItems.get(i).substring(0, diferentiator));

                            int minute = Integer.parseInt(notificationItems.get(i).substring(
                                    notificationItems.get(i).lastIndexOf(":") + 1));

                            for (int j = 1; j < 7; j++) {
                                if (PillsNotificationHelper.checkIfDayIsSelected(j, mondayButton.isChecked(),
                                        tuesdayButton.isChecked(), wednesdayButton.isChecked(),
                                        thursdayButton.isChecked(), fridayButton.isChecked(),
                                        saturdayButton.isChecked(), sundayButton.isChecked())) {

                                    c.set(Calendar.HOUR_OF_DAY, hour);
                                    c.set(Calendar.MINUTE, minute);
                                    c.set(Calendar.SECOND, 0);

                                    startAlarm(c, pillName.getText().toString(), i, j,
                                            Integer.parseInt(pillCount.getText().toString()));
                                }
                            }
                        }

                        for (int i = 0; i < toDeleteNotificationItems.size(); i++) {
                            diferentiator = toDeleteNotificationItems.get(i).indexOf(":");

                            int hour = Integer.parseInt(toDeleteNotificationItems.get(i).substring(0, diferentiator));

                            int minute = Integer.parseInt(toDeleteNotificationItems.get(i).substring(
                                    toDeleteNotificationItems.get(i).lastIndexOf(":") + 1));

                            for (int j = 1; j < 7; j++) {
                                if (PillsNotificationHelper.checkIfDayIsSelected(j, mondayButton.isChecked(),
                                        tuesdayButton.isChecked(), wednesdayButton.isChecked(),
                                        thursdayButton.isChecked(), fridayButton.isChecked(),
                                        saturdayButton.isChecked(), sundayButton.isChecked())) {

                                    c.set(Calendar.HOUR_OF_DAY, hour);
                                    c.set(Calendar.MINUTE, minute);
                                    c.set(Calendar.SECOND, 0);
                                    cancelAlarm(c, pillName.getText().toString(), i, j,
                                            Integer.parseInt(pillCount.getText().toString()));
                                }
                            }
                        }
                        finish();
                    }

                } else {
                Toast toast = Toast.makeText(activity,
                        activity.getString(R.string.pills_fields_left_empty), Toast.LENGTH_SHORT);
                toast.show();
            }
            }
        });

        // Cancel button
        Button cancelButton = findViewById(R.id.pills_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        if (minute > 9) {
            notificationString = hourOfDay + ":" + minute;
        } else {
            notificationString = hourOfDay + ":0" + minute;
        }
        if (notificationItems.size() < 6) {
            notificationItems.add(notificationString);
        }
        adapter.notifyDataSetChanged();
    }

    /** Starts a notification alarm */
    public void startAlarm(Calendar c, String key, Integer dayCount, Integer notifNr, Integer quantity) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        SharedPreferences.Editor prefs = getSharedPreferences("pillsPreferences", MODE_PRIVATE).edit();
        prefs.putString("key", key);
        prefs.putInt("quantity", quantity);
        int alarmName = (key + notifNr + dayCount.toString()).hashCode();

        prefs.putInt("alarmName", alarmName);
        prefs.apply();

        Intent intent = new Intent(this, PillsAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmName, intent, 0);

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) + 1 - dayCount;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 3, pendingIntent);
    }

    /** Cancels a notification alarm */
    private void cancelAlarm(Calendar c, String key, Integer dayCount, Integer notifNr, Integer quantity) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        SharedPreferences.Editor prefs = getSharedPreferences("pillsPreferences", MODE_PRIVATE).edit();
        prefs.putString("key", key);
        prefs.putInt("quantity", quantity);
        int alarmName = (key + notifNr + dayCount.toString()).hashCode();

        prefs.putInt("alarmName", alarmName);
        prefs.apply();

        Intent intent = new Intent(this, PillsAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmName, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
}
