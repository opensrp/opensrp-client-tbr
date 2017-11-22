package org.smartregister.tbr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.tbr.R;
import org.smartregister.tbr.adapter.ServiceHistoryAdapter;
import org.smartregister.tbr.model.ServiceHistory;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.ArrayList;
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
        tbReachIdTextView.setText(Utils.formatIdentifier(patientDetails.get(Constants.KEY.TBREACH_ID)));

        TextView clientAgeTextView = (TextView) findViewById(R.id.clientAgeTextView);
        String dobString = patientDetails.get(Constants.KEY.DOB);
        String formattedAge = Utils.getFormattedAgeString(dobString);
        clientAgeTextView.setText(formattedAge);

        TextView clientNameTextView = (TextView) findViewById(R.id.clientNameTextView);
        String fullName = patientDetails.get(Constants.KEY.FIRST_NAME) + " " + patientDetails.get(Constants.KEY.LAST_NAME);
        clientNameTextView.setText(fullName);
        TextView clientGenderTextView = (TextView) findViewById(R.id.clientGenderTextView);
        clientGenderTextView.setText(WordUtils.capitalize(patientDetails.get(Constants.KEY.GENDER)));

        TextView clientInitalsTextView = (TextView) findViewById(R.id.clientInitalsTextView);
        clientInitalsTextView.setText(Utils.getShortInitials(fullName));

        if (patientDetails.get(Constants.KEY.GENDER).equals(Constants.GENDER.MALE)) {
            clientInitalsTextView.setBackgroundColor(getResources().getColor(R.color.male_light_blue));
            clientInitalsTextView.setTextColor(getResources().getColor(R.color.male_blue));

        } else {
            clientInitalsTextView.setBackgroundColor(getResources().getColor(R.color.female_light_pink));
            clientInitalsTextView.setTextColor(getResources().getColor(R.color.female_pink));
        }

        Button removePatientButton = (Button) findViewById(R.id.removePatientButton);
        removePatientButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
    }

}
