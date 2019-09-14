package org.smartregister.tbr.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.tbr.R;
import org.smartregister.tbr.adapter.ViewPagerAdapter;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.util.Utils;
import org.smartregister.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    TextView reportHeading;
    TableLayout tableLayout;

    Button getButton;

    TextView totalChildren;
    TextView totalHVs;
    TextView totalMalnutrition;
    TextView totalAcuteMalnutrition;
    TextView totalAnemia;
    TextView totalDiarrhea;
    TextView totatMalaria;

    Spinner monthSpinner;
    Spinner yearSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.report));

        reportHeading = (TextView) findViewById(R.id.report_heading);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        getButton = (Button) findViewById(R.id.getButton);

        totalChildren = (TextView) findViewById(R.id.totalChildren);
        totalHVs = (TextView) findViewById(R.id.totalHV);
        totalMalnutrition = (TextView) findViewById(R.id.totalMalnutrition);
        totalAcuteMalnutrition = (TextView) findViewById(R.id.totalAcuteMalnutrition);
        totalAnemia = (TextView) findViewById(R.id.totalAnemia);
        totalDiarrhea = (TextView) findViewById(R.id.totalDiarrhea);
        totatMalaria = (TextView) findViewById(R.id.totalMalaria);
        monthSpinner = (Spinner) findViewById(R.id.month);
        yearSpinner = (Spinner) findViewById(R.id.year);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[6];
        years[0] = "";
        years[1] = String.valueOf(year);
        for(int i=2; i<6; i ++){

            year--;
            years[i] = String.valueOf(year);

        }


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        years); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        yearSpinner.setAdapter(spinnerArrayAdapter);

        getButton.setOnClickListener(this);

        reportHeading.setVisibility(View.GONE);
        tableLayout.setVisibility(View.GONE);

        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(TbrApplication.getInstance().getApplicationContext()));
        Utils.setLocale(new Locale(allSharedPreferences.getPreference("locale")));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.getButton:

                String monthString = "";
                int month = monthSpinner.getSelectedItemPosition();
                if(month >= 1 && month < 10)
                    monthString = "0"+month;
                else if(month >= 10)
                    monthString = ""+month;

                int year = yearSpinner.getSelectedItemPosition();
                String yearInString = yearSpinner.getSelectedItem().toString();
                String monthInString = monthSpinner.getSelectedItem().toString();

                if(year == 0){
                    Toast.makeText(getApplicationContext(),"Select atleast year to continue.",Toast.LENGTH_SHORT).show();
                } else {


                    reportHeading.setText("Numbers for year " + yearInString + ((month == 0 )? "" : (" and month ") + monthInString  ));
                    reportHeading.setVisibility(View.VISIBLE);
                    tableLayout.setVisibility(View.VISIBLE);

                    HashMap<String, Integer> hashMap = TbrApplication.getInstance().getResultsRepository().getTotalNumbers(monthString,yearInString);
                    totalChildren.setText(String.valueOf(hashMap.get("totalChildren")));
                    totalHVs.setText(String.valueOf(hashMap.get("totalHVs")));
                    totalMalnutrition.setText(String.valueOf(hashMap.get("totalMalnutrition")));
                    totalAnemia.setText(String.valueOf(hashMap.get("totalAnemia")));
                    totalDiarrhea.setText(String.valueOf(hashMap.get("totalDiarrhea")));
                    totalAcuteMalnutrition.setText(String.valueOf(hashMap.get("totalAcuteMalnutrition")));
                    totatMalaria.setText(String.valueOf(hashMap.get("totalMalaria")));

                    yearSpinner.setSelection(0);
                    monthSpinner.setSelection(0);

                }

                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(TbrApplication.getInstance().getApplicationContext()));
        Utils.setLocale(new Locale(allSharedPreferences.getPreference("locale")));
    }
}
