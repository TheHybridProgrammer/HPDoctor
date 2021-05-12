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

public class PillsAddPillActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    String notificationString;
    Integer quantity = 1;
    EditText pillName;
    Activity activity;

    ArrayList<String> notificationItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);

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

        setContentView(R.layout.pills_adder_activity);


        pillName = findViewById(R.id.pills_pillname);

        // PILL COUNTER
        EditText pillCount = findViewById(R.id.pills_count);
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
        ToggleButton tuesdayButton = findViewById(R.id.pills_tuesday);
        ToggleButton wednesdayButton = findViewById(R.id.pills_wednesday);
        ToggleButton thursdayButton = findViewById(R.id.pills_thursday);
        ToggleButton fridayButton = findViewById(R.id.pills_friday);
        ToggleButton saturdayButton = findViewById(R.id.pills_saturday);
        ToggleButton sundayButton = findViewById(R.id.pills_sunday);

        // ACTIVITY EXITERS
        // Add Pill
        Button addPill = findViewById(R.id.pills_add_pill);
        addPill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pillName.getText().toString().isEmpty() != true &&
                        pillName.getText().toString().replaceAll(" ", "").isEmpty() != true
                        && ( mondayButton.isChecked() || tuesdayButton.isChecked() ||
                        wednesdayButton.isChecked() || thursdayButton.isChecked() ||
                        fridayButton.isChecked() || saturdayButton.isChecked() ||
                        sundayButton.isChecked())) {

                    if (!PillsJsonFileUtils.searchForPill(activity, pillName.getText().toString())) {
                        // Create alarm
                        Calendar c = Calendar.getInstance();

                        String pillKeyName;
                        int diferentiator;

                        for (int i = 0; i < notificationItems.size(); i++) {
                            pillKeyName = pillName.getText().toString();
                            diferentiator = notificationItems.get(i).indexOf(":");

                            int hour = Integer.parseInt(notificationItems.get(i).substring(0, diferentiator));

                            int minute = Integer.parseInt(notificationItems.get(i).substring(
                                    notificationItems.get(i).lastIndexOf(":") + 1));

                            for (int j = 1; j < 7; j++) {
                                if (PillsNotificationHelper.checkIfDayIsSelected(j, mondayButton.isChecked(),
                                        tuesdayButton.isChecked(), wednesdayButton.isChecked(),
                                        thursdayButton.isChecked(), fridayButton.isChecked(),
                                        saturdayButton.isChecked(), sundayButton.isChecked())) {

                                    //                                c.set(Calendar.DAY_OF_WEEK, j);
                                    c.set(Calendar.HOUR_OF_DAY, hour);
                                    c.set(Calendar.MINUTE, minute);
                                    c.set(Calendar.SECOND, 0);

                                    startAlarm(c, pillKeyName, i, j,
                                            Integer.parseInt(pillCount.getText().toString()));
                                }
                            }
                        }
                        PillsUtils.writeToJson(activity, activity, pillName.getText().toString(),
                                Integer.parseInt(pillCount.getText().toString()),
                                mondayButton.isChecked(), tuesdayButton.isChecked(),
                                wednesdayButton.isChecked(), thursdayButton.isChecked(),
                                fridayButton.isChecked(), saturdayButton.isChecked(),
                                sundayButton.isChecked(), notificationItems);

                        finish();
                    } else {
                        Toast toast = Toast.makeText(activity,
                                activity.getString(R.string.pills_duplicate_pill), Toast.LENGTH_SHORT);
                        toast.show();
                    }

                } else {
                    Toast toast = Toast.makeText(activity,
                            activity.getString(R.string.pills_fields_left_empty), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        // Cancel add pill
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

    /** Sets up a new alarm */
    public void startAlarm(Calendar c, String key, Integer dayCount, Integer notifNr, Integer quantity) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        SharedPreferences.Editor prefs = getSharedPreferences("pillsPreferences", MODE_PRIVATE).edit();
        prefs.putString("key", key);
        int alarmName = (key + notifNr + dayCount.toString()).hashCode();
        prefs.putInt("alarmName", alarmName);
        prefs.apply();

        Intent intent = new Intent(this, PillsAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmName, intent, 0);

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) + 1 - dayCount;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 3, pendingIntent);
    }

}
