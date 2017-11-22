package org.smartregister.tbr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.smartregister.tbr.R;
import org.smartregister.tbr.adapter.ServiceHistoryAdapter;
import org.smartregister.tbr.model.ServiceHistory;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public class PresumptivePatientDetailActivity extends BasePatientDetailActivity {
    private ArrayList<ServiceHistory> serviceHistoryArrayList;
    private ListView listView;
    private static ServiceHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presumptive_patient_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        String title = intent.getStringExtra(Constants.INTENT_KEY.REGISTER_TITLE);
        if (title != null && !title.toString().isEmpty()) {
            getSupportActionBar().setTitle(title);
        }

        listView = (ListView) findViewById(R.id.serviceHistoryListView);

        serviceHistoryArrayList = new ArrayList<>();

        serviceHistoryArrayList.add(new ServiceHistory("07 Jun 2017", "GeneXpert Result"));
        serviceHistoryArrayList.add(new ServiceHistory("03 Jun 2017", "Smear Result"));
        serviceHistoryArrayList.add(new ServiceHistory("10 May 2017", "GeneXpert Result"));
        serviceHistoryArrayList.add(new ServiceHistory("12 Apr 2017", "Culture Result"));
        serviceHistoryArrayList.add(new ServiceHistory("24 Jan 2017", "X-Ray Result"));
        serviceHistoryArrayList.add(new ServiceHistory("16 Jan 2017", "Registration"));

        adapter = new ServiceHistoryAdapter(serviceHistoryArrayList, getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ServiceHistory serviceHistory = serviceHistoryArrayList.get(position);

                Snackbar.make(view, serviceHistory.getFormName() + " Filled on : " + "\n" + serviceHistory.getDate(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });


        TextView recordResults = (TextView) findViewById(R.id.recordResultsTextView);
        recordResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showToast(getApplicationContext(), "Recording patient results ...");
            }
        });

        processViews();
    }

    private void processViews() {
        Map<String, String> patientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP);
        TextView tbReachIdTextView = (TextView) findViewById(R.id.tbReachIdTextView);
        tbReachIdTextView.setText("#" + patientDetails.get("tbreach_id"));
        TextView clientAgeTextView = (TextView) findViewById(R.id.clientAgeTextView);
        String dobString = patientDetails.get("dob");
        String formattedAge = "";
        if (!TextUtils.isEmpty(dobString)) {
            DateTime dateTime = new DateTime(dobString);
            Date dob = dateTime.toDate();
            long timeDiff = Calendar.getInstance().getTimeInMillis() - dob.getTime();

            if (timeDiff >= 0) {
                formattedAge = DateUtil.getDuration(timeDiff);
            }
        }
        clientAgeTextView.setText(formattedAge);
        TextView clientNameTextView = (TextView) findViewById(R.id.clientNameTextView);
        clientNameTextView.setText(patientDetails.get("first_name") + " " + patientDetails.get("last_name"));
        TextView clientGenderTextView = (TextView) findViewById(R.id.clientGenderTextView);
        clientGenderTextView.setText(WordUtils.capitalize(patientDetails.get("gender")));
    }


}
