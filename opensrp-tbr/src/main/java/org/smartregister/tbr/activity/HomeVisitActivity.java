package org.smartregister.tbr.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.smartregister.tbr.R;
import org.smartregister.tbr.adapter.ViewPagerAdapter;
import org.smartregister.util.DateUtil;

import java.util.Calendar;
import java.util.Date;


public class HomeVisitActivity extends AppCompatActivity implements View.OnClickListener {

    ViewPager viewPager;

    Button previousButton;
    Button nextButton;
    TextView stepNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_visit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.home_visit));

        previousButton = (Button) findViewById(R.id.previous_btn);
        nextButton = (Button) findViewById(R.id.next_btn);

        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        previousButton.setVisibility(View.GONE);
        stepNumber = (TextView) findViewById(R.id.step_no);

        String dobString = getIntent().getStringExtra("dob");
        DateTime dob = DateTime.parse(dobString);
        int ageInMonths = roundToInteger(getAgeInMonths(dob.toDate(),new Date()));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, ageInMonths);
        viewPager.setAdapter(viewPagerAdapter);

        if(viewPager.getAdapter().getCount() == 1) {
            nextButton.setVisibility(View.GONE);
        }


        stepNumber.setText("1/"+viewPager.getAdapter().getCount());

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            // optional
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            // optional
            @Override
            public void onPageSelected(int position) {

                int pos = position + 1;

                stepNumber.setText(pos+"/"+viewPager.getAdapter().getCount());

                if (position == 0) {
                    previousButton.setVisibility(View.GONE);
                    nextButton.setVisibility(View.VISIBLE);
                }
                else if(viewPager.getAdapter().getCount()-1 == position) {
                    previousButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.GONE);
                } else {
                    previousButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);
                }

            }

            // optional
            @Override
            public void onPageScrollStateChanged(int state) { }
        });


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
            case R.id.previous_btn:
                if (viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                }
                break;
            case R.id.next_btn:
                if (viewPager.getAdapter().getCount() > viewPager.getCurrentItem()) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                }
                break;
        }

    }

    public static double getAgeInMonths(Date dateOfBirth, Date weighingDate) {
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dateOfBirth);
        standardiseCalendarDate(dobCalendar);

        Calendar weighingCalendar = Calendar.getInstance();
        weighingCalendar.setTime(weighingDate);
        standardiseCalendarDate(weighingCalendar);

        double result = 0;
        if (dobCalendar.getTimeInMillis() <= weighingCalendar.getTimeInMillis()) {
            result = ((double) (weighingCalendar.getTimeInMillis() - dobCalendar.getTimeInMillis())) / 2629746000l;
        }

        return result;
    }

    private static void standardiseCalendarDate(Calendar calendarDate) {
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
    }

    public static int roundToInteger(double d){
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        return d<0 ? -i : i;
        /*if(result<0.5){
            return d<0 ? -i : i;
        }else{
            return d<0 ? -(i+1) : i+1;
        }*/
    }

}
