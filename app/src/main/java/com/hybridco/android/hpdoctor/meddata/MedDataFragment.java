package com.hybridco.android.hpdoctor.meddata;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hybridco.android.hpdoctor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

public class MedDataFragment extends Fragment {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    LinkedHashMap<String, List<String>> expandableListDetail;
    private static Activity activity;
    public static final int PICKFILE_RESULT_CODE = 1;
    private Uri fileUri;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meddata_fragment, container, false);
        setHasOptionsMenu(true);

        activity = getActivity();

        activity.setTitle(activity.getString(R.string.drawer_menu_med_data));
        expandableListView = (ExpandableListView) view.findViewById(R.id.meddataExpandableListView);
        expandableListDetail = MedDataExpandableListData.getData(activity);
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new MedDataExpandableListAdapter(getActivity(), expandableListTitle,
                expandableListDetail, getActivity());
        expandableListView.setAdapter(expandableListAdapter);
        context = getContext();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.meddata_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /** Cases for top-right options menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            /* On upload data it allows the user to select a local file, which is then put into
            fileUri, a uri variable type */
            case R.id.meddata_upload_data:
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            break;
            case R.id.meddata_download_data:
                MedDataDownload.downloadData(getActivity());
            break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /** Cases for onActivityResult values */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            /* Uses the fileUri and puts the uploaded file content into a string, that is then
            transformed into a json object which is then compared with local jsonObject */
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    fileUri = data.getData();
                    String jsonString = MedDataFileUpload.getFileJson(fileUri, getActivity());
                    MedDataFileUpload.jsonUploadCompare(getActivity(), getActivity(), jsonString);
                    activity.recreate();
                }
                break;
        }
    }

}