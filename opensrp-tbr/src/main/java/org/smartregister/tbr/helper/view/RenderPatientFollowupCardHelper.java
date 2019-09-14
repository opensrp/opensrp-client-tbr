package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.BasePatientDetailActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.helper.FormOverridesHelper;
import org.smartregister.tbr.model.Result;
import org.smartregister.tbr.repository.ResultDetailsRepository;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;
import org.smartregister.util.DateUtil;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.TbrConstants;

import static org.smartregister.util.Utils.getValue;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderPatientFollowupCardHelper extends BaseRenderHelper {

    private ResultsRepository resultsRepository;

    private static final String TAG = RenderPatientFollowupCardHelper.class.getCanonicalName();

    public RenderPatientFollowupCardHelper(Context context, ResultDetailsRepository detailsRepository) {
        super(context, detailsRepository);
    }

    @Override
    public void renderView(final View view, final Map<String, String> patientDetails) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                try {

                    String baseEntityId = patientDetails.get(TbrConstants.KEY.BASE_ENTITY_ID);
                    Map<String, String> testResults = TbrApplication.getInstance().getResultsRepository().getLatestResult(baseEntityId);

                    Map<String, Result> testResultsAll = TbrApplication.getInstance().getResultsRepository().getLatestResultsAll(baseEntityId,null,false);

                    TextView weight = (TextView) view.findViewById(R.id.weight);
                    if (testResults.containsKey(ResultsRepository.WEIGHT) && testResults.get(ResultsRepository.WEIGHT) != null) {
                        weight.setText(" " +testResults.get(ResultsRepository.WEIGHT) + " kg");
                    }

                    TextView height = (TextView) view.findViewById(R.id.height);
                    if (testResults.containsKey(ResultsRepository.HEIGHT) && testResults.get(ResultsRepository.HEIGHT) != null) {
                        height.setText(" " + testResults.get(ResultsRepository.HEIGHT) + " cm");
                    }

                    Button weightHeight = (Button) view.findViewById(R.id.weight_height);
                    ImageView wightHeightImage = (ImageView) view.findViewById(R.id.weight_height_image);
                    weightHeight.setAllCaps(false);
                    if (testResults.containsKey(ResultsRepository.WEIGHT_HEIGHT_STATUS)  && testResults.get(ResultsRepository.WEIGHT_HEIGHT_STATUS) != null) {
                        String weightHeightString = testResults.get(ResultsRepository.WEIGHT_HEIGHT_STATUS);
                        if(weightHeightString.equalsIgnoreCase("normal")) {
                            weightHeight.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                            weightHeight.setText(R.string.weight_height_normal);
                        }
                        else if(weightHeightString.equalsIgnoreCase("Low weight-height")) {
                            weightHeight.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            weightHeight.setText(R.string.weight_height_low);
                        }
                        else if(weightHeightString.equalsIgnoreCase("Extremely low weight-height")) {
                            weightHeight.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            weightHeight.setText(R.string.weight_height_extremely_low);
                        }
/*                        else {
                            weightHeight.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                        }*/
                    }

                    Button heightAge = (Button) view.findViewById(R.id.height_age);
                    ImageView heightAgeImage = (ImageView) view.findViewById(R.id.height_age_image);
                    heightAge.setAllCaps(false);
                    if (testResults.containsKey(ResultsRepository.HEIGHT_AGE_STATUS) && testResults.get(ResultsRepository.HEIGHT_AGE_STATUS) != null) {
                        String heightAgeString = testResults.get(ResultsRepository.HEIGHT_AGE_STATUS);
                        heightAge.setText(heightAgeString);
                        if(heightAgeString.equalsIgnoreCase("normal")) {
                            heightAge.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                            heightAge.setText(R.string.height_age_normal);
                        }
                        else if(heightAgeString.equalsIgnoreCase("Low height-age")) {
                            heightAge.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            heightAge.setText(R.string.height_age_low);
                        }
                        else if(heightAgeString.equalsIgnoreCase("Extremely low height-age")) {
                            heightAge.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            heightAge.setText(R.string.height_age_extremely_low);
                        }
/*                        else {
                            heightAge.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                        }*/
                    }

                    Button haemoglobinLevel = (Button) view.findViewById(R.id.anemia_status);
                    if (testResults.containsKey(ResultsRepository.HAEMOGLOBIN) && testResults.get(ResultsRepository.HAEMOGLOBIN) != null) {
                        String haemoglobin = testResults.get(ResultsRepository.HAEMOGLOBIN);
                        haemoglobinLevel.setText(haemoglobin);
                        Float haemoglobinValue = Float.parseFloat(haemoglobin);
                        if(haemoglobinValue < 11){
                            haemoglobinLevel.setBackgroundResource(R.drawable.indictaors_display_red);
                            haemoglobinLevel.setText("Anemia");
                        } else {
                            haemoglobinLevel.setBackgroundResource(R.drawable.indictaors_display_blue);
                            haemoglobinLevel.setText("No Anemia");
                        }
                    }

                    TextView hgb = (TextView) view.findViewById(R.id.haemoglobin_level);
                    if (testResults.containsKey(ResultsRepository.HAEMOGLOBIN) && testResults.get(ResultsRepository.HAEMOGLOBIN) != null) {
                        String haemoglobin = testResults.get(ResultsRepository.HAEMOGLOBIN);
                        hgb.setText(haemoglobin);
                    }

                    TextView lastDate = (TextView) view.findViewById(R.id.date_last_recorded);
                    if (testResults.containsKey(ResultsRepository.DATE) && testResults.get(ResultsRepository.DATE) != null) {

                        Date date = new Date(Long.parseLong(testResults.get(ResultsRepository.DATE)));
                        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                        String dateText = df2.format(date);
                        lastDate.setText(" " + dateText);


                        List<String> illnesses = new ArrayList<String>();
                        if (testResults.containsKey(ResultsRepository.DIARREA) && testResults.get(ResultsRepository.DIARREA) != null) {
                            String diarrea = testResults.get(ResultsRepository.DIARREA);
                            if (diarrea.equalsIgnoreCase("yes"))
                                illnesses.add(context.getString(R.string.diarrae));
                        }

                        if (testResults.containsKey(ResultsRepository.MALARIA) && testResults.get(ResultsRepository.MALARIA) != null) {
                            String malaria = testResults.get(ResultsRepository.MALARIA);
                            if (malaria.equalsIgnoreCase("yes"))
                                illnesses.add(context.getString(R.string.malaria));
                        }

                        if (testResults.containsKey(ResultsRepository.COLD) && testResults.get(ResultsRepository.COLD) != null) {
                            String cold = testResults.get(ResultsRepository.COLD);
                            if (cold.equalsIgnoreCase("yes"))
                                illnesses.add(context.getString(R.string.cold_flu));
                        }

                        if (testResults.containsKey(ResultsRepository.PNEUMONIA) && testResults.get(ResultsRepository.PNEUMONIA) != null) {
                            String pneumonia = testResults.get(ResultsRepository.PNEUMONIA);
                            if (pneumonia.equalsIgnoreCase("yes"))
                                illnesses.add(context.getString(R.string.pneumonias));
                        }

                        if (testResults.containsKey(ResultsRepository.BRONCHITIS) && testResults.get(ResultsRepository.BRONCHITIS) != null) {
                            String tb = testResults.get(ResultsRepository.BRONCHITIS);
                            if (tb.equalsIgnoreCase("yes"))
                                illnesses.add(context.getString(R.string.bronchitis));
                        }


                        if (illnesses.size() == 0) {

                            Button illness = (Button) view.findViewById(R.id.illness_1);
                            illness.setAllCaps(false);
                            illness.setText(context.getString(R.string.no_illnesses));
                            illness.setVisibility(View.VISIBLE);
                            illness.setBackgroundResource(R.drawable.due_vaccine_blue_bg);

                            View illnessView = (View) view.findViewById(R.id.illness_card);
                            illnessView.setVisibility(View.GONE);

                        } else {

                            View illnessView = (View) view.findViewById(R.id.illness_card);
                            illnessView.setVisibility(View.VISIBLE);

                            if (illnesses.size() >= 1) {
                                Button illness = (Button) view.findViewById(R.id.illness_1);
                                illness.setAllCaps(false);
                                illness.setText(illnesses.get(0));
                                illness.setVisibility(View.VISIBLE);
                                illness.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {
                                Button illness = (Button) view.findViewById(R.id.illness_1);
                                illness.setVisibility(View.GONE);
                            }

                            if (illnesses.size() >= 2) {
                                Button illness = (Button) view.findViewById(R.id.illness_2);
                                illness.setAllCaps(false);
                                illness.setText(illnesses.get(1));
                                illness.setVisibility(View.VISIBLE);
                                illness.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {
                                Button illness = (Button) view.findViewById(R.id.illness_2);
                                illness.setVisibility(View.GONE);
                            }

                            if (illnesses.size() >= 3) {
                                Button illness = (Button) view.findViewById(R.id.illness_3);
                                illness.setAllCaps(false);
                                illness.setText(illnesses.get(2));
                                illness.setVisibility(View.VISIBLE);
                                illness.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {
                                Button illness = (Button) view.findViewById(R.id.illness_3);
                                illness.setVisibility(View.GONE);
                            }

                            if (illnesses.size() >= 4) {
                                Button illness = (Button) view.findViewById(R.id.illness_4);
                                illness.setAllCaps(false);
                                illness.setText(illnesses.get(3));
                                illness.setVisibility(View.VISIBLE);
                                illness.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {
                                Button illness = (Button) view.findViewById(R.id.illness_4);
                                illness.setVisibility(View.GONE);
                            }

                            if (illnesses.size() >= 5) {
                                Button illness = (Button) view.findViewById(R.id.illness_5);
                                illness.setAllCaps(false);
                                illness.setText(illnesses.get(4));
                                illness.setVisibility(View.VISIBLE);
                                illness.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {
                                Button illness = (Button) view.findViewById(R.id.illness_5);
                                illness.setVisibility(View.GONE);
                            }

                            if (illnesses.size() >= 6) {
                                Button illness = (Button) view.findViewById(R.id.illness_6);
                                illness.setAllCaps(false);
                                illness.setText(illnesses.get(5));
                                illness.setVisibility(View.VISIBLE);
                                illness.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {
                                Button illness = (Button) view.findViewById(R.id.illness_6);
                                illness.setVisibility(View.GONE);
                            }
                        }

                    }

                    Button nextVisitDate = (Button) view.findViewById(R.id.next_visit_date);
                    nextVisitDate.setAllCaps(false);
                    if (testResults.containsKey(ResultsRepository.NEXT_VISIT_DATE) && testResults.get(ResultsRepository.NEXT_VISIT_DATE) != null) {
                        String nextVisitDateString = testResults.get(ResultsRepository.NEXT_VISIT_DATE);

                        DateTime nextVisit = DateTime.parse(nextVisitDateString);

                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = dateFormat.format(nextVisit.toDate());
                        nextVisitDate.setText(strDate);

                        if(nextVisit.toDate().after(new DateTime().toDate()))
                            nextVisitDate.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                        else
                            nextVisitDate.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                    }

                    Button nextHealthCheckupDate = (Button) view.findViewById(R.id.next_health_checkup);
                    nextHealthCheckupDate.setAllCaps(false);
                    if (testResults.containsKey(ResultsRepository.NEXT_GROWTH_MONITORING_DATE) && testResults.get(ResultsRepository.NEXT_GROWTH_MONITORING_DATE) != null) {
                        String nextHealthCheckupDateString = testResults.get(ResultsRepository.NEXT_GROWTH_MONITORING_DATE);

                        DateTime nextHealthCheckup = DateTime.parse(nextHealthCheckupDateString);

                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = dateFormat.format(nextHealthCheckup.toDate());
                        nextHealthCheckupDate.setText(strDate);

                        if(nextHealthCheckup.toDate().after(new DateTime().toDate()))
                            nextHealthCheckupDate.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                        else
                            nextHealthCheckupDate.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                    }

                    Button nextDeworminDate = (Button) view.findViewById(R.id.next_deworming);
                    nextDeworminDate.setAllCaps(false);
                    if (testResults.containsKey(ResultsRepository.DEWORMING) && testResults.get(ResultsRepository.DEWORMING) != null) {

                        String dewormingString = testResults.get(ResultsRepository.DEWORMING);
                        if(dewormingString.equalsIgnoreCase("no")){

                            String dobString = patientDetails.get(Constants.KEY.DOB);
                            DateTime dewormingDate = DateTime.parse(dobString);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(dewormingDate.toDate());
                            cal.add(Calendar.MONTH, 24);

                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String strDate = dateFormat.format(cal.getTime());
                            nextDeworminDate.setText(strDate);

                            DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                            String strDate2 = dateFormat2.format(cal.getTime());
                            DateTime nextDDate = DateTime.parse(strDate2);
                            if(nextDDate.toDate().after(new DateTime().toDate()))
                                nextDeworminDate.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                            else
                                nextDeworminDate.setBackgroundResource(R.drawable.due_vaccine_red_bg);

                        } else{

                            String dewormingDateString = testResults.get(ResultsRepository.DEWORMING_DATE);
                            if(dewormingDateString != null){

                                DateTime dewormingDate = DateTime.parse(dewormingDateString);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(dewormingDate.toDate());
                                cal.add(Calendar.MONTH, 6);

                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String strDate = dateFormat.format(cal.getTime());
                                nextDeworminDate.setText(strDate);

                                DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                                String strDate2 = dateFormat2.format(cal.getTime());
                                DateTime nextDDate = DateTime.parse(strDate2);
                                if(nextDDate.toDate().after(new DateTime().toDate()))
                                    nextDeworminDate.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                                else
                                    nextDeworminDate.setBackgroundResource(R.drawable.due_vaccine_red_bg);

                            }

                        }

                    }

                    //Calculate - Age in months
                    String dobString = patientDetails.get(Constants.KEY.DOB);
                    DateTime dateTime = new DateTime(dobString);
                    Date dob = dateTime.toDate();
                    int ageInMonths = (int) Math.round(getAgeInMonths(dob, new Date()));

                    Button nextVaccineDate = (Button) view.findViewById(R.id.next_vaccine);
                    nextVaccineDate.setAllCaps(false);
                    boolean ageGT15 = false;
                    boolean vaccineDueFilled = false;
                    if(ageInMonths >= 15){

                        ageGT15 = true;

                        if (testResults.containsKey(ResultsRepository.FIFTEEN_ANTIAMARILICA) && testResults.get(ResultsRepository.FIFTEEN_ANTIAMARILICA) != null) {

                            Boolean dueVaccine = false;

                            if (!testResults.get(ResultsRepository.FIFTEEN_ANTIAMARILICA).equalsIgnoreCase("yes"))
                                dueVaccine = true;


                            if (dueVaccine) {
                                vaccineDueFilled = true;
                                DateTime vaccineDate = DateTime.parse(dobString);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(vaccineDate.toDate());
                                cal.add(Calendar.MONTH, 15);

                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String strDate = dateFormat.format(cal.getTime());
                                nextVaccineDate.setText(strDate);
                                nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {
                                vaccineDueFilled=true;
                                nextVaccineDate.setText(R.string.vaccines_completed);
                                nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_blue_bg);

                            }

                        }

                    }

                    if(ageInMonths >= 12){

                        if (testResults.containsKey(ResultsRepository.TWELVE_NEUMOCOCO) && testResults.get(ResultsRepository.TWELVE_SARAMPION) != null) {

                            Boolean dueVaccine = false;

                            if(!testResults.get(ResultsRepository.TWELVE_NEUMOCOCO).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(!testResults.get(ResultsRepository.TWELVE_SARAMPION).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(dueVaccine) {

                                vaccineDueFilled = true;
                                DateTime vaccineDate = DateTime.parse(dobString);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(vaccineDate.toDate());
                                cal.add(Calendar.MONTH, 12);

                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String strDate = dateFormat.format(cal.getTime());
                                nextVaccineDate.setText(strDate);
                                nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {

                                if(!vaccineDueFilled) {
                                    vaccineDueFilled = true;
                                    DateTime vaccineDate = DateTime.parse(dobString);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(vaccineDate.toDate());
                                    cal.add(Calendar.MONTH, 15);

                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String strDate = dateFormat.format(cal.getTime());
                                    nextVaccineDate.setText(strDate);

                                    nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                                }

                            }

                        }

                    }

                    if(ageInMonths >= 6){

                        if (testResults.containsKey(ResultsRepository.SIX_ANTIPOLIO) && testResults.get(ResultsRepository.SIX_ANTIPOLIO) != null) {

                            Boolean dueVaccine = false;

                            if(!testResults.get(ResultsRepository.SIX_ANTIPOLIO).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(!testResults.get(ResultsRepository.SIX_PENTAVALENTE).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(dueVaccine) {

                                vaccineDueFilled = true;
                                DateTime vaccineDate = DateTime.parse(dobString);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(vaccineDate.toDate());
                                cal.add(Calendar.MONTH, 6);

                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String strDate = dateFormat.format(cal.getTime());
                                nextVaccineDate.setText(strDate);
                                nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {
                                if(!vaccineDueFilled) {
                                    vaccineDueFilled = true;
                                    DateTime vaccineDate = DateTime.parse(dobString);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(vaccineDate.toDate());
                                    cal.add(Calendar.MONTH, 12);

                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String strDate = dateFormat.format(cal.getTime());
                                    nextVaccineDate.setText(strDate);

                                    nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                                }

                            }

                        }

                    }

                    if(ageInMonths >= 4){

                        if (testResults.containsKey(ResultsRepository.FOUR_ANTIPOLIO) && testResults.get(ResultsRepository.FOUR_ANTIPOLIO) != null) {

                            Boolean dueVaccine = false;

                            if(!testResults.get(ResultsRepository.FOUR_ANTIPOLIO).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(!testResults.get(ResultsRepository.FOUR_PENTAVALENTE).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(!testResults.get(ResultsRepository.FOUR_NEUMOCOCO).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(!testResults.get(ResultsRepository.FOUR_ROTAVIRUS).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(dueVaccine) {

                                vaccineDueFilled = true;
                                DateTime vaccineDate = DateTime.parse(dobString);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(vaccineDate.toDate());
                                cal.add(Calendar.MONTH, 4);

                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String strDate = dateFormat.format(cal.getTime());
                                nextVaccineDate.setText(strDate);
                                nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {
                                if(!vaccineDueFilled) {
                                    vaccineDueFilled = true;
                                    DateTime vaccineDate = DateTime.parse(dobString);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(vaccineDate.toDate());
                                    cal.add(Calendar.MONTH, 6);

                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String strDate = dateFormat.format(cal.getTime());
                                    nextVaccineDate.setText(strDate);

                                    nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                                }

                            }

                        }

                    }


                    if(ageInMonths >= 2){

                        if (testResults.containsKey(ResultsRepository.TWO_ANTIPOLIO) && testResults.get(ResultsRepository.TWO_ANTIPOLIO) != null) {

                            Boolean dueVaccine = false;

                            if(!testResults.get(ResultsRepository.TWO_ANTIPOLIO).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(!testResults.get(ResultsRepository.TWO_PENTAVALENTE).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(!testResults.get(ResultsRepository.TWO_NEUMOCOCO).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(!testResults.get(ResultsRepository.TWO_ROTAVIRUS).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(dueVaccine) {

                                vaccineDueFilled = true;
                                DateTime vaccineDate = DateTime.parse(dobString);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(vaccineDate.toDate());
                                cal.add(Calendar.MONTH, 2);

                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String strDate = dateFormat.format(cal.getTime());
                                nextVaccineDate.setText(strDate);
                                nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {
                                if(!vaccineDueFilled) {
                                    vaccineDueFilled = true;
                                    DateTime vaccineDate = DateTime.parse(dobString);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(vaccineDate.toDate());
                                    cal.add(Calendar.MONTH, 4);

                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String strDate = dateFormat.format(cal.getTime());
                                    nextVaccineDate.setText(strDate);

                                    nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                                }

                            }

                        }

                    }

                    if(ageInMonths >= 0){

                        if (testResults.containsKey(ResultsRepository.ZERO_TUBERCULOSIS) && testResults.get(ResultsRepository.ZERO_TUBERCULOSIS) != null) {

                            Boolean dueVaccine = false;

                            if(!testResults.get(ResultsRepository.ZERO_TUBERCULOSIS).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(!testResults.get(ResultsRepository.ZERO_ANTIHERPATITIS).equalsIgnoreCase("yes"))
                                dueVaccine = true;

                            if(dueVaccine) {
                                vaccineDueFilled = true;
                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String strDate = dateFormat.format(dob);
                                nextVaccineDate.setText(strDate);
                                nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                            } else {
                                if(!vaccineDueFilled) {
                                    vaccineDueFilled = true;
                                    DateTime vaccineDate = DateTime.parse(dobString);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(vaccineDate.toDate());
                                    cal.add(Calendar.MONTH, 2);

                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String strDate = dateFormat.format(cal.getTime());
                                    nextVaccineDate.setText(strDate);

                                    nextVaccineDate.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                                }
                            }

                        }

                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        });
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
}
