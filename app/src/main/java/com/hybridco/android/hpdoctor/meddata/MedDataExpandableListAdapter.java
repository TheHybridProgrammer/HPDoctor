package com.hybridco.android.hpdoctor.meddata;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.hybridco.android.hpdoctor.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MedDataExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> expandableListTitle;
    private LinkedHashMap<String, List<String>> expandableListDetail;
    Activity activity;

    public MedDataExpandableListAdapter(Context context, List<String> expandableListTitle,
                                        LinkedHashMap<String, List<String>> expandableListDetail,
                                        Activity activity) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.activity = activity;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.meddata_list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.infoExpandedListItem);
        expandedListTextView.setText(expandedListText);

        // Pop-up to modify selected child
        expandedListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Removes the character `:` of child string and everything to it's right
                String childTitle = MedDataUtils.clearChildString(expandedListTextView.getText().
                        toString());

                AlertDialog.Builder alert = new AlertDialog.Builder(activity);

                alert.setTitle(childTitle);

                final EditText input = new EditText(activity);
                alert.setView(input);

                alert.setPositiveButton(activity.getString(R.string.med_data_ok),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MedDataUtils.writeToJsonChild(activity, context,
                                String.valueOf(groupPosition), String.valueOf(childPosition),
                                input.getText().toString());

                        // Delete hashmap, refill it with the new data and refresh list view
                        expandableListDetail = null;
                        expandableListDetail = MedDataExpandableListData.getData(activity);
                        notifyDataSetChanged();
                    }
                });

                alert.setNegativeButton(activity.getString(R.string.med_data_cancel),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.show();

            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.meddata_list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.infoListTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

}
