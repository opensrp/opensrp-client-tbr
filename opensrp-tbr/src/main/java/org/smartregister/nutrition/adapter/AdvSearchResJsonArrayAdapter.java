package org.smartregister.nutrition.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.nutrition.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.provider.PatientRegisterProvider;
import org.smartregister.tbr.sync.ECSyncHelper;
import org.smartregister.tbr.sync.TbrClientProcessor;
import org.smartregister.tbr.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.TbrConstants;
import util.TbrSpannableStringBuilder;

/**
 * Created by Imran-PC on 09-May-18.
 */

public class AdvSearchResJsonArrayAdapter extends RecyclerView.Adapter<AdvSearchResJsonArrayAdapter.ViewHolder> {
    private JSONArray patDetails;
    private static final String DETECTED = "detected";
    private static final String NOT_DETECTED = "not_detected";
    private static final String INDETERMINATE = "indeterminate";
    private static final String ERROR = "error";
    private static final String NO_RESULT = "no_result";
    private static final String POSITIVE = "positive";
    private static final String NEGATIVE = "negative";
    private EventClientRepository ecRepository;

    private ForegroundColorSpan blackForegroundColorSpan;
    private ForegroundColorSpan redForegroundColorSpan;
    private JSONObject detailsMap = new JSONObject();
    private ProgressDialog progressDialog;
    private Context context;
    private View view;

    public AdvSearchResJsonArrayAdapter(JSONArray ja,Context context,View view){
        this.patDetails = ja;
        this.context = context;
        this.view = view;
        this.ecRepository = TbrApplication.getInstance().getEventClientRepository();
        blackForegroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(android.R.color.black));
        redForegroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(android.R.color.holo_red_light));
        initializeProgressDialog(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.register_presumptive_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            detailsMap = (JSONObject) patDetails.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String age = Utils.getDuration(getValue(detailsMap,"birthdate"));
        PatientRegisterProvider.fillValue(holder.tvPatientName,getValue(detailsMap,"firstName"));
        PatientRegisterProvider.fillValue(holder.tvParticipantId, getValue((JSONObject) getValueFromJsonObj(detailsMap,"identifiers"),"TBREACH ID"));
        PatientRegisterProvider.fillValue(holder.gender,getValue(detailsMap,"gender"));
        PatientRegisterProvider.fillValue(holder.age,age.substring(0,age.indexOf("y")));
        Map<String,JSONObject> testResults = new HashMap<>();
        try {
            testResults = getLatestResults((JSONArray) detailsMap.get("events"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TbrSpannableStringBuilder stringBuilder  = new TbrSpannableStringBuilder();
        boolean hasXpert = populateXpertResult(testResults, stringBuilder, true);
        populateSmearResult(stringBuilder, getResultByKey(testResults,"Smear Result",TbrConstants.RESULT.TEST_RESULT), hasXpert, false); //testResults.get(TbrConstants.RESULT.TEST_RESULT), hasXpert, false);
        populateCultureResults(stringBuilder, getResultByKey(testResults,"Culture Result",TbrConstants.RESULT.CULTURE_RESULT));// testResults.get(TbrConstants.RESULT.CULTURE_RESULT));
        populateXrayResults(stringBuilder, getResultByKey(testResults,"X-Ray Result",TbrConstants.RESULT.XRAY_RESULT)); // testResults.get(TbrConstants.RESULT.XRAY_RESULT));

        if (stringBuilder.length() > 0) {
            holder.tvDetails.setVisibility(View.VISIBLE);
            holder.tvDetails.setText("");
            holder.tvDetails.append(stringBuilder);
        } else
            holder.tvDetails.setVisibility(View.GONE);

        holder.result_lnk.setVisibility(View.GONE);
        holder.diagnose_lnk.setVisibility(View.GONE);

        JSONObject localClientObj = ecRepository.getClientByBaseEntityId(getValue(detailsMap,"baseEntityId") != null ? getValue(detailsMap,"baseEntityId") : "");
        holder.claim_lnk.setVisibility(View.VISIBLE);
        if(localClientObj != null) {
            holder.claim_lnk.setVisibility(View.GONE);
            holder.tvExists.setVisibility(View.VISIBLE);
        }
        else{
            holder.claim_lnk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressDialog();
                    try {
                        saveClients(context, (JSONObject) getValueFromJsonArray(patDetails, position));
                        Snackbar.make(view,"Client saved successfully",Snackbar.LENGTH_LONG).show();
                        holder.claim_lnk.setTextColor(Color.parseColor("#808080"));
                        holder.claim_lnk.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(view,"Some error occurred. Client could not be saved",Snackbar.LENGTH_LONG).show();
                    }finally{
                        hideProgressDialog();
                    }
                }
            });
        }
    }

    private void hideProgressDialog() {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    private void saveClients(Context context, JSONObject dm) throws Exception{
        List<JSONObject> eventsList = new ArrayList<>();
        ECSyncHelper ecSyncHelper = ECSyncHelper.getInstance(context);
        JSONArray events = (JSONArray)getValueFromJsonObj(dm,"events");
        dm.remove("events");
        JSONArray patArray = new JSONArray();
        patArray.put(dm);
        ecSyncHelper.batchSave(events,patArray);
        for(int i=0; i<events.length(); i++){
            JSONObject eventJO;
            String eventType = (getValue((JSONObject)getValueFromJsonArray(events,i),("eventType")));
            if( eventType.equalsIgnoreCase("Screening") || eventType.equalsIgnoreCase("TB Diagnosis")|| eventType.equalsIgnoreCase("Treatment Initiation")){
                eventJO = (JSONObject)getValueFromJsonArray(events,i);
                eventJO.put("client",dm);
                eventsList.add(eventJO);
            }
            else
                eventsList.add((JSONObject) getValueFromJsonArray(events,i));
        }

        TbrClientProcessor.getInstance(context).processClient(eventsList);


    }

    private Map<String,JSONObject> getLatestResults(JSONArray events) {
        Map<String,JSONObject> resultsMap = new HashMap<>();
        String key;
        JSONObject event = new JSONObject();
        for(int i=0; i<events.length(); i++){
            try {
                event = (JSONObject)events.get(i);
                key = event.getString("eventType");
            } catch (JSONException e) {
                e.printStackTrace();
                key = null;
            }
            if(resultsMap.containsKey(key)){
                //compare dates of the existing object (resultsMap) and the object in loop (event)
                Date d1 = getDateFromJsonObject(event);
                Date d2 = getDateFromJsonObject(resultsMap.get(key));
                if((d1 != null && d2 != null) && d1.compareTo(d2) > 0)
                    resultsMap.put(key,event);
            }
            else{
                try {
                    resultsMap.put(key,(JSONObject)events.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultsMap;
    }

    private Date getDateFromJsonObject(JSONObject event) {
        JSONArray obsArray = new JSONArray();
        try {
            obsArray = (JSONArray)event.get("obs");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        for(int i=0; i<obsArray.length(); i++){
            JSONObject obsJsonObj=null;
            try {
                obsJsonObj = (JSONObject) obsArray.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            String fieldCode = getValue(obsJsonObj,"fieldCode");
            if(fieldCode != null && fieldCode.equalsIgnoreCase("TBR:sample collection date")){
                try {
                    JSONArray valuesArray = (JSONArray)obsJsonObj.get("values");
                    try {
                        Date date = new SimpleDateFormat("yyy-MM-dd").parse((String)valuesArray.get(0));
                        return date;
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return this.patDetails.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvPatientName, tvParticipantId, tvDetails, result_lnk, diagnose_lnk, age, gender, claim_lnk, tvExists;
        private Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.tvPatientName = (TextView) itemView.findViewById(R.id.patient_name);
            this.tvParticipantId = (TextView) itemView.findViewById(R.id.participant_id);
            this.tvDetails = (TextView) itemView.findViewById(R.id.result_details);
            this.result_lnk = (TextView) itemView.findViewById(R.id.result_lnk);
            this.diagnose_lnk = (TextView) itemView.findViewById(R.id.diagnose_lnk);
            this.gender = (TextView) itemView.findViewById(R.id.gender);
            this.age = (TextView) itemView.findViewById(R.id.age);
            this.claim_lnk = (TextView) itemView.findViewById(R.id.claim_lnk);
            this.tvExists = (TextView) itemView.findViewById(R.id.tv_already_exists);
        }
    }

    private boolean populateXpertResult(Map<String, JSONObject> testResults, TbrSpannableStringBuilder stringBuilder, boolean withOtherResults) {
        if (testResultsHasKey(testResults, "GeneXpert Result",TbrConstants.RESULT.MTB_RESULT)){
            stringBuilder.append(withOtherResults ? "Xpe " : "MTB ");
            String mtbResult = getResultByKey(testResults, "GeneXpert Result",TbrConstants.RESULT.MTB_RESULT);
            processXpertResult(mtbResult, stringBuilder);
            if (testResultsHasKey(testResults,"GeneXpert Result",TbrConstants.RESULT.ERROR_CODE)){
                stringBuilder.append(" ");
                stringBuilder.append(getResultByKey(testResults, "GeneXpert Result",TbrConstants.RESULT.ERROR_CODE), blackForegroundColorSpan);
            } else if (testResultsHasKey(testResults, "GeneXpert Result",TbrConstants.RESULT.RIF_RESULT)) {
                stringBuilder.append(withOtherResults ? "/" : "\nRIF ");
                processXpertResult(getResultByKey(testResults, "GeneXpert Result",TbrConstants.RESULT.RIF_RESULT),stringBuilder);
            }
            return true;
        }
        return false;
    }

    private boolean testResultsHasKey(Map<String,JSONObject> testResults, String eventType, String key) {
        JSONObject obj = testResults.get(eventType);
        JSONArray obsArray = null;
        try {
            obsArray = (JSONArray)obj.get("obs");
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            return false;
        }
        if(obsArray == null) {
            return false;
        }
        else{
            for(int i=0; i<obsArray.length(); i++){
                JSONObject jo = getJSONObjectfromJSONArray(obsArray,i);
                if(getValue(jo,"formSubmissionField").equalsIgnoreCase(key)){
                    return true;
                }
            }
            return false;
        }
    }

    private String getResultByKey(Map<String, JSONObject> testResults, String eventType, String key) {
        JSONObject obj = testResults.get(eventType);
        JSONArray obsArray = null;
        try {
            obsArray = (JSONArray)obj.get("obs");
        } catch (JSONException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            return null;
        }
        if(obsArray == null)
            return null;
        else{
            for(int i=0; i<obsArray.length(); i++){
                JSONObject jo = getJSONObjectfromJSONArray(obsArray,i);
                if(getValue(jo,"formSubmissionField").equalsIgnoreCase(key)){
                    JSONArray ja = (JSONArray) getValueFromJsonObj(jo,"humanReadableValues");
                    Object o = getValueFromJsonArray(ja,0);
                    return ( o == null ? null : (String)o);
                }
            }
            return null;
        }
    }

    private void processXpertResult(String result, TbrSpannableStringBuilder stringBuilder) {
        if (result == null)
            return;
        switch (result) {
            case DETECTED:
                stringBuilder.append("+ve", redForegroundColorSpan);
                break;
            case NOT_DETECTED:
                stringBuilder.append("-ve", blackForegroundColorSpan);
                break;
            case INDETERMINATE:
                stringBuilder.append("?", blackForegroundColorSpan);
                break;
            case ERROR:
                stringBuilder.append("err", blackForegroundColorSpan);
                break;
            case NO_RESULT:
                stringBuilder.append("No result", blackForegroundColorSpan);
                break;
            default:
                break;
        }
    }

    private void populateSmearResult(TbrSpannableStringBuilder stringBuilder, String result, boolean hasXpert, boolean smearOnlyColumn) {
        if (result == null) return;
        else if (hasXpert && !smearOnlyColumn)
            stringBuilder.append(",\n");
        if (!smearOnlyColumn)
            stringBuilder.append("Smr ");
        switch (result) {
            case "one_plus":
                stringBuilder.append("1+", redForegroundColorSpan);
                break;
            case "two_plus":
                stringBuilder.append("2+", redForegroundColorSpan);
                break;
            case "three_plus":
                stringBuilder.append("3+", redForegroundColorSpan);
                break;
            case "scanty":
                stringBuilder.append(smearOnlyColumn ? "Scanty" : "Sty", redForegroundColorSpan);
                break;
            case "negative":
                stringBuilder.append(smearOnlyColumn ? "Negative" : "Neg", blackForegroundColorSpan);
                break;
            default:
        }
    }

    private void populateXrayResults(TbrSpannableStringBuilder stringBuilder, String result) {
        if (result == null)
            return;
        else if (stringBuilder.length() > 0)
            stringBuilder.append(", ");
        stringBuilder.append("CXR ");
        if ("indicative".equals(result))
            stringBuilder.append("Ind", redForegroundColorSpan);
        else
            stringBuilder.append("NInd", blackForegroundColorSpan);

    }

    private void populateCultureResults(TbrSpannableStringBuilder stringBuilder, String result) {
        if (result == null)
            return;
        else if (stringBuilder.length() > 0)
            stringBuilder.append("\n");
        stringBuilder.append("Cul ");
        if (result.equalsIgnoreCase(POSITIVE))
            stringBuilder.append(WordUtils.capitalizeFully(result).substring(0, 3), redForegroundColorSpan);
        if (result.equalsIgnoreCase(NEGATIVE))
            stringBuilder.append(WordUtils.capitalizeFully(result).substring(0, 3), blackForegroundColorSpan);
    }

    private String getValue(JSONObject obj, String key){
        try {
            return (String)obj.get(key);
        } catch (JSONException e) {
            return null;
        }

    }

    private JSONObject getJSONObjectFromJSONObject(JSONObject jo, String key){
        JSONObject j = null;
        try {
            j = jo.getJSONObject(key);
            return j;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject getJSONObjectfromJSONArray(JSONArray ja, int pos){
        JSONObject j = null;
        try {
            j = ja.getJSONObject(pos);
            return j;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONArray getJSONArrayfromJSONObject(JSONObject ja, String key){
        JSONArray j = null;
        try {
            j = ja.getJSONArray(key);
            return j;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object getValueFromJsonObj(JSONObject jo, String key){
        try {
            return jo.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    private Object getValueFromJsonArray(JSONArray ja, int pos){
        try {
            return ja.get(pos);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void initializeProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Please wait...");
    }
}
