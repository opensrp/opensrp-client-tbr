package org.smartregister.tbr.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.smartregister.domain.Response;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.tbr.R;
import org.smartregister.tbr.adapter.AdvSearchResJsonArrayAdapter;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static org.smartregister.util.Log.logError;

/**
 * Created by Imran-PC on 03-May-18.
 */

public class AdvancedSearchResultsFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {
    private RecyclerView presumptiveRecyclerView,posRecyclerView,inTreatmentRecyclerView;
    private TextView included,numResults, presumptiveNoResults, positiveNoResults, intreatmentNoResults;
    private ArrayList<HashMap<String,String>> presumptiveData, positiveData, treatmentData;
    private FetchClientsTask remoteLoginTask;
    private ProgressDialog progressDialog;
    private HashMap<String,Object> model;
    private View view;
    private String url;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_advanced_search_results, container, false);

        included = (TextView) view.findViewById(R.id.tv_included);
        numResults = (TextView) view.findViewById(R.id.tv_numResults);
        presumptiveNoResults = (TextView) view.findViewById(R.id.tv_presumptive_no_results);
        positiveNoResults = (TextView) view.findViewById(R.id.tv_pos_no_results);
        intreatmentNoResults = (TextView) view.findViewById(R.id.tv_intreatment_no_results);
        presumptiveRecyclerView = (RecyclerView) view.findViewById(R.id.presumptive_list);
        posRecyclerView = (RecyclerView) view.findViewById(R.id.pos_list);
        inTreatmentRecyclerView = (RecyclerView) view.findViewById(R.id.treatment_list);
        initializeProgressDialog();
        Bundle bundle = this.getArguments();
        String text = "";
        model = (HashMap<String, Object>) bundle.get("model");
        if(bundle != null){
            text = prepareTextToInclude(model);
        }
        included.setText(text);
        presumptiveData = new ArrayList<>();
        positiveData = new ArrayList<>();
        treatmentData = new ArrayList<>();
        url = prepareUrl(model);
        remoteLoginTask = new FetchClientsTask();
        remoteLoginTask.execute();
        return view;
    }

    private String prepareTextToInclude(HashMap<String,Object> map){
        StringBuilder text = new StringBuilder();
        text.append("Include: ");
        text.append("'");
        if(map.containsKey("presumptive")){
            text.append("Presumptive, ");
        }
        if(map.containsKey("positive"))
            text.append("Positive, ");
        if(map.containsKey("inTreatment"))
            text.append("In-treatment, ");
        if(text.charAt(text.length() - 1) == '\'')
            text.deleteCharAt(text.length() - 1);
        else {
            text.deleteCharAt(text.length()-2);

            text.append("';");
        }
        if(map.containsKey("firstName"))
            text.append(" First name: '" + map.get("firstName") + "';");
        if(map.containsKey("lastName"))
            text.append(" Last name: '" + map.get("lastName") + "';");
        if(map.containsKey("participantId"))
            text.append(" Participant Id: '" + map.get("participantId") + "';");
        if(map.containsKey("gender"))
            text.append(" Gender: '" + map.get("gender").toString().toLowerCase() + "';");
        if(map.containsKey("ageGroup"))
            text.append(" Age Group: '" + map.get("ageGroup") + "';");

        return text.toString();
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Please wait...");
    }

    private String prepareUrl(HashMap<String,Object> model){
        boolean first = true;
        StringBuilder url = new StringBuilder();
        url.append(getResources().getString(R.string.opensrp_url)+ "/rest/advanceSearch/advanceSearchBy?q=");
        if(model.containsKey("ageGroup")){
            appendToUrl(url,"ageGroup",(String)model.get("ageGroup"),first);
            first = false;
        }
        if(model.containsKey("gender")){
            appendToUrl(url,"gender",(String)model.get("gender"),first);
            first = false;
        }
        if(model.containsKey("firstName")){
            appendToUrl(url,"firstName",(String)model.get("firstName"), first);
            first = false;
        }
        if(model.containsKey("presumptive")){
            appendToUrl(url,"presumptive","true",first);
            first = false;
        }
        if(model.containsKey("positive")){
            appendToUrl(url,"positive","true",first);
            first = false;
        }
        if(model.containsKey("inTreatment")){
            appendToUrl(url,"intreatment","true",first);
            first = false;
        }
        if(model.containsKey("participantId")){
            appendToUrl(url,"participantID",(String)model.get("participantId"),first);
            first = false;
        }
        if(model.containsKey("phoneNumber")){
            appendToUrl(url,"phoneNumber",(String)model.get("phoneNumber"),first);
            first = false;
        }
        if(model.containsKey("lastName")){
            appendToUrl(url,"lastName",(String)model.get("lastName"),first);
            first = false;
        }
        return url.toString();
    }

    private void appendToUrl(StringBuilder url, String key, String value, boolean first){
        if(!first){
            url.append("%20AND%20");
        }
        url.append(key+":"+value);
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onCreation() {

    }

    private class FetchClientsTask extends AsyncTask<Void, Void, JSONObject> {

        private FetchClientsTask() {
        }

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            Response resp = null;
            try {
                resp = context().getHttpAgent().fetch(url);
            }catch(Exception e){
                e.printStackTrace();
            }
            if (resp.isFailure()) {
                logError(url + " not returned data");
                return null;
            }
            JSONObject j = null;
            try {
                j = new JSONObject((String)resp.payload());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return j;
        }


        @Override
        protected void onPostExecute(JSONObject resp) {
            try {
                numResults.setText("" + getTotalClients(resp) + " result(s) found");
                if(resp == null){
                    Snackbar.make(view,"Some error occurred",Snackbar.LENGTH_LONG).show();
                }
                if (resp != null && resp.has("presumptive")) {
                    preparePresumptiveRecycleView(resp);
                } else {
                    showView(presumptiveNoResults);
                }
                if (resp != null && resp.has("positive")) {
                    preparePositiveRecycleView(resp);
                } else {
                    showView(positiveNoResults);
                }
                if (resp != null && resp.has("intreatment")) {
                    prepareInTreatmentRecycleView(resp);
                } else {
                    showView(intreatmentNoResults);
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                hideProgressDialog();
            }
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {

            if (getActivity().isDestroyed()) { // Fix not attached to window manager Exception
                return;
            }
            progressDialog.dismiss();
        }
    }

    private void showView(View view) {
        if(view != null){
            view.setVisibility(View.VISIBLE);
        }
    }

    private void prepareInTreatmentRecycleView(JSONObject resp) {
        try {
            showView(inTreatmentRecyclerView);
            AdvSearchResJsonArrayAdapter adap = new AdvSearchResJsonArrayAdapter((JSONArray) resp.get("intreatment"),getActivity(),this.view);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            inTreatmentRecyclerView.setLayoutManager(mLayoutManager);
            inTreatmentRecyclerView.setItemAnimator(new DefaultItemAnimator());
            inTreatmentRecyclerView.setAdapter(adap);
            adap.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void preparePresumptiveRecycleView(JSONObject resp) {
        try {
            showView(presumptiveRecyclerView);
            AdvSearchResJsonArrayAdapter adap = new AdvSearchResJsonArrayAdapter((JSONArray) resp.get("presumptive"),getActivity(),this.view);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            presumptiveRecyclerView.setLayoutManager(mLayoutManager);
            presumptiveRecyclerView.setItemAnimator(new DefaultItemAnimator());
            presumptiveRecyclerView.setAdapter(adap);
            adap.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void preparePositiveRecycleView(JSONObject resp) {
        try {
            showView(posRecyclerView);
            AdvSearchResJsonArrayAdapter adap = new AdvSearchResJsonArrayAdapter((JSONArray) resp.get("positive"),getActivity(),this.view);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            posRecyclerView.setLayoutManager(mLayoutManager);
            posRecyclerView.setItemAnimator(new DefaultItemAnimator());
            posRecyclerView.setAdapter(adap);
            adap.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getTotalClients(JSONObject obj) {
        Iterator<?> it = obj.keys();
        Integer count = 0;
        while(it.hasNext()){
            String key = (String)it.next();
            try {
                count += obj.getJSONArray(key).length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return count.toString();
    }

}
