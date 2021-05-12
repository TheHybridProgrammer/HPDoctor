 package com.hybridco.android.hpdoctor.pills;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hybridco.android.hpdoctor.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PillsListAdapter extends BaseAdapter implements ListAdapter {

    private List<String> pillName;
    private List<Integer> pillQuantity;
    private Context context;
    public static final int PICKFILE_RESULT_CODE = 1;
    Activity activity;
    ArrayList<Boolean> isCheckedList;
    Boolean isChecked;


    public PillsListAdapter(List<String> pillName, String day, Context context, Activity activity) {
        this.pillName = pillName;
        this.pillQuantity = PillsJsonFileUtils.getSelectedDayPillsQuantity(activity, day);
        this.context = context;
        this.activity = activity;
        this.isCheckedList = PillsJsonFileUtils.getSelectedDayIsCheckedList(activity, day);
    }

    @Override
    public int getCount() {
        return pillName.size();
    }

    @Override
    public Object getItem(int pos) {
        return null;
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.pills_list_template_layout, null);
        }

        isChecked = PillsJsonFileUtils.getIsChecked(activity, pillName.get(position));

        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.pills_list_item);
        listItemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PillsModifyPillActivity.class);
                intent.putExtra("PillName", listItemText.getText().toString());
                activity.startActivityForResult(intent, 1);
            }
        });
        listItemText.setText(pillName.get(position));

        // Pill current day reseter
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = df.format(c);

        if(!PillsJsonFileUtils.getCurrentDateString(context, listItemText.getText().toString()).equals(currentDate)) {
            PillsJsonFileUtils.modifyPillCurrentDate(activity, context,
                    listItemText.getText().toString());
            PillsJsonFileUtils.modifyPillCurrentQuantity(activity, context,
                    listItemText.getText().toString(),
                    PillsJsonFileUtils.getPillOriginalQuantity(activity, listItemText.getText().toString()));
            PillsJsonFileUtils.modifyPillIsChecked(activity, activity,
                    listItemText.getText().toString(), false);
        }

        TextView pillsQuantity = view.findViewById(R.id.pills_list_quantity);
        pillsQuantity.setText(pillQuantity.get(position).toString());

        //Handle buttons and add onClickListeners
        Button incBtn = (Button)view.findViewById(R.id.pills_list_increment_button);
        Button decBtn = (Button)view.findViewById(R.id.pills_list_decrement_button);

        CheckBox pillCheckBox = view.findViewById(R.id.pills_list_checkbox);
        pillCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!pillCheckBox.isChecked()) {
                    PillsJsonFileUtils.modifyPillIsChecked(activity, activity,
                            listItemText.getText().toString(), false);

                    PillsJsonFileUtils.modifyPillCurrentQuantity(activity, activity,
                            listItemText.getText().toString(),
                            PillsJsonFileUtils.getPillOriginalQuantity(activity,
                                    listItemText.getText().toString()));
                } else {
                    PillsJsonFileUtils.modifyPillIsChecked(activity, activity,
                            listItemText.getText().toString(), true);

                    PillsJsonFileUtils.modifyPillCurrentQuantity(activity, activity,
                            listItemText.getText().toString(), 0);
                }

                activity.recreate();
            }
        });

        pillCheckBox.setChecked(isChecked);

        if(pillCheckBox.isChecked()) {
            pillCheckBox.setChecked(true);
            incBtn.setVisibility(View.INVISIBLE);
            decBtn.setVisibility(View.INVISIBLE);
            listItemText.setPaintFlags(listItemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else if (!PillsUtils.checkCurrentDate(context, listItemText.getText().toString())) {
            incBtn.setVisibility(View.INVISIBLE);
            decBtn.setVisibility(View.INVISIBLE);
            pillCheckBox.setVisibility(View.INVISIBLE);
            listItemText.setPaintFlags(0);

        } else {
            incBtn.setVisibility(View.VISIBLE);
            decBtn.setVisibility(View.VISIBLE);
            listItemText.setPaintFlags(0);

            incBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.valueOf(pillsQuantity.getText().toString()) <
                            PillsJsonFileUtils.getPillOriginalQuantity(activity,
                                    listItemText.getText().toString())) {

                        pillQuantity.set(position, Integer.valueOf(pillsQuantity.getText().toString()) + 1);
                        PillsJsonFileUtils.modifyPillCurrentQuantity(activity, activity,
                                listItemText.getText().toString(),
                                Integer.valueOf(pillsQuantity.getText().toString()) + 1);
                    } else {
                        Toast toast = Toast.makeText(activity,
                                "Can't add more pills than the original quantity assigned", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    notifyDataSetChanged();
                }
            });

            decBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.valueOf(pillsQuantity.getText().toString()) > 0) {

                        if (Integer.valueOf(pillsQuantity.getText().toString()) == 1) {
                            PillsJsonFileUtils.modifyPillIsChecked(activity, activity,
                                    listItemText.getText().toString(), true);
                            notifyDataSetChanged();
                            activity.recreate();
                        }

                        pillQuantity.set(position, Integer.valueOf(pillsQuantity.getText().toString()) - 1);
                        PillsJsonFileUtils.modifyPillCurrentQuantity(activity, activity,
                                listItemText.getText().toString(),
                                Integer.valueOf(pillsQuantity.getText().toString()) - 1);
                    }
                    notifyDataSetChanged();
                }
            });

        }
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == 1) {
                    activity.recreate();
                }

                break;
        }
    }
}
