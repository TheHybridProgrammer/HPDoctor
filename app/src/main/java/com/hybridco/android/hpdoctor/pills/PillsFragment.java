package com.hybridco.android.hpdoctor.pills;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hybridco.android.hpdoctor.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PillsFragment extends Fragment implements View.OnClickListener{

    private static Activity activity;
    public static final int PICKFILE_RESULT_CODE = 1;
    ArrayList<Boolean> isCheckedList;
    List<String> pillName;
    List<Integer> pillQuantity;
    PillsListAdapter adapter;
    ListView lView;
    Button mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton,
            saturdayButton, sundayButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pills_fragment, container, false);
        activity = getActivity();

        activity.setTitle(activity.getString(R.string.drawer_menu_pills));

        ConstraintLayout constraintLayout = view.findViewById(R.id.pills_buttons_constraint_layout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet = PillsUtils.setDayButtonsOrder(constraintSet);
        constraintSet.applyTo(constraintLayout);

        mondayButton = view.findViewById(R.id.button_monday);
        mondayButton.setOnClickListener(this::onClick);

        tuesdayButton = view.findViewById(R.id.button_tuesday);
        tuesdayButton.setOnClickListener(this::onClick);

        wednesdayButton = view.findViewById(R.id.button_wednesday);
        wednesdayButton.setOnClickListener(this::onClick);

        thursdayButton = view.findViewById(R.id.button_thursday);
        thursdayButton.setOnClickListener(this::onClick);

        fridayButton = view.findViewById(R.id.button_friday);
        fridayButton.setOnClickListener(this::onClick);

        saturdayButton = view.findViewById(R.id.button_saturday);
        saturdayButton.setOnClickListener(this::onClick);

        sundayButton = view.findViewById(R.id.button_sunday);
        sundayButton.setOnClickListener(this::onClick);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        setCurrentDayPills(day);

        //handle listview and assign adapter
        lView = (ListView) view.findViewById(R.id.pills_listview_current_pills);
        lView.setAdapter(adapter);

        FloatingActionButton addPillsButton = view.findViewById(R.id.add_pills);
        addPillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PillsAddPillActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == 1) {
                    activity.recreate();
                }

                break;
        }
    }

    public void setCurrentDayPills(Integer day) {
        switch(day) {
            case Calendar.MONDAY:
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Monday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Monday");
                restoreBackgrounds();
                mondayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Monday", activity, activity);
                break;

            case Calendar.TUESDAY:
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Tuesday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Tuesday");
                restoreBackgrounds();
                tuesdayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Tuesday", activity, activity);
                break;

            case Calendar.WEDNESDAY:
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Wednesday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Wednesday");
                restoreBackgrounds();
                wednesdayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName,"Wednesday", activity, activity);
                break;

            case Calendar.THURSDAY:
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Thursday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Thursday");
                restoreBackgrounds();
                thursdayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Thursday", activity, activity);
                break;

            case Calendar.FRIDAY:
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Friday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Friday");
                restoreBackgrounds();
                fridayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Friday", activity, activity);
                break;

            case Calendar.SATURDAY:
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Saturday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Saturday");
                restoreBackgrounds();
                saturdayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Saturday", activity, activity);
                break;

            case Calendar.SUNDAY:
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Sunday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Sunday");
                restoreBackgrounds();
                sundayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Sunday", activity, activity);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_monday:
                pillName.clear();
                pillQuantity.clear();
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Monday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Monday");
                restoreBackgrounds();
                mondayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Monday", activity, activity);
                adapter.notifyDataSetChanged();
                lView.setAdapter(adapter);
                break;

            case R.id.button_tuesday:
                pillName.clear();
                pillQuantity.clear();
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Tuesday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Tuesday");
                restoreBackgrounds();
                tuesdayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Tuesday", activity, activity);
                adapter.notifyDataSetChanged();
                lView.setAdapter(adapter);
                break;

            case R.id.button_wednesday:
                pillName.clear();
                pillQuantity.clear();
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Wednesday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Wednesday");
                restoreBackgrounds();
                wednesdayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Wednesday", activity, activity);
                adapter.notifyDataSetChanged();
                lView.setAdapter(adapter);
                break;

            case R.id.button_thursday:
                pillName.clear();
                pillQuantity.clear();
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Thursday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Thursday");
                restoreBackgrounds();
                thursdayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Thursday", activity, activity);
                adapter.notifyDataSetChanged();
                lView.setAdapter(adapter);
                break;

            case R.id.button_friday:
                pillName.clear();
                pillQuantity.clear();
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Friday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Friday");
                restoreBackgrounds();
                fridayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Friday", activity, activity);
                adapter.notifyDataSetChanged();
                lView.setAdapter(adapter);
                break;

            case R.id.button_saturday:
                pillName.clear();
                pillQuantity.clear();
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Saturday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Saturday");
                restoreBackgrounds();
                saturdayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Saturday", activity, activity);
                adapter.notifyDataSetChanged();
                lView.setAdapter(adapter);
                break;

            case R.id.button_sunday:
                pillName.clear();
                pillQuantity.clear();
                pillName = PillsJsonFileUtils.getSelectedDayPillsNames(activity,"Sunday");
                pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, "Sunday");
                restoreBackgrounds();
                sundayButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                adapter = new PillsListAdapter(pillName, "Sunday", activity, activity);
                adapter.notifyDataSetChanged();
                lView.setAdapter(adapter);
                break;
        }
    }

    public void restoreBackgrounds() {
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        mondayButton.setBackgroundColor(color);
        tuesdayButton.setBackgroundColor(color);
        wednesdayButton.setBackgroundColor(color);
        thursdayButton.setBackgroundColor(color);
        fridayButton.setBackgroundColor(color);
        saturdayButton.setBackgroundColor(color);
        sundayButton.setBackgroundColor(color);
    }
}