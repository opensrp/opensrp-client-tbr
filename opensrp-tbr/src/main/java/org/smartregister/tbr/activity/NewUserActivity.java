package org.smartregister.tbr.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.tbr.R;
import org.smartregister.tbr.adapter.ViewPagerAdapter;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.service.HttpAgentExtended;

import java.util.Calendar;
import java.util.Date;


public class NewUserActivity extends Activity implements View.OnClickListener {

    private TextView firstName;
    private TextView lastName;
    private TextView username;
    private TextView password1;
    private TextView password2;

    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;

    protected static ProgressDialog loading;

    private Button createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_user);

        loading = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);

        firstName = (TextView)findViewById(R.id.firstname);
        lastName = (TextView)findViewById(R.id.lastname);
        username = (TextView)findViewById(R.id.username);

        password1 = (TextView)findViewById(R.id.password1);
        password2 = (TextView)findViewById(R.id.password2);

        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);

        createAccount = (Button) findViewById(R.id.createAccountBtn);

        createAccount.setOnClickListener(this);

        this.setFinishOnTouchOutside(false);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }


    @Override
    public void onClick(View view) {

        if(view == createAccount){

            Boolean error = false;
            if(firstName.getText().toString().equals("")) {
                firstName.setError("Required Field");
                error = true;
            }
            if(lastName.getText().toString().equals("")) {
                lastName.setError("Required Field");
                error = true;
            }
            if(username.getText().toString().equals("")){
                username.setError("Required Field");
                error = true;
            }
            if(password1.getText().toString().equals("")){
                password1.setError("Required Field");
                error = true;
            }
            if(password2.getText().toString().equals("")){
                password2.setError("Required Field");
                error = true;
            }

            if(!password1.getText().toString().equals(password2.getText().toString())){
                password2.setError("Password Mismatch");
                error = true;
            }

            if(!error){

                AsyncTask<String, String, String> submissionTask = new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /*loading.setInverseBackgroundForced(true);*/
                                loading.setIndeterminate(true);
                                loading.setCancelable(false);
                                loading.setMessage("Creating account...");
                                loading.show();
                            }
                        });

                        String result = "";
                        try {
                            result = createAccount();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return result;

                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                    }

                    ;

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        loading.dismiss();
                        if (result.equals("SUCCESS")) {
                            TbrApplication.getInstance().getUserRepository().saveUser(username.getText().toString(),password1.getText().toString());
                            finish();
                        } else  if (result.equals("FAILURE")) {

                            Toast toast=Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_SHORT);
                            toast.setMargin(50,50);
                            toast.show();

                        } else {

                            Toast toast=Toast.makeText(getApplicationContext(),result ,Toast.LENGTH_SHORT);
                            toast.setMargin(50,50);
                            toast.show();

                        }
                    }
                };
                submissionTask.execute("");

            }


        }

    }

    private String createAccount() throws JSONException {


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", password1.getText().toString());
        jsonObject.put("username", username.getText().toString());

        JSONObject personObj = new JSONObject();
        JSONArray names = new JSONArray();

        JSONObject preferredName = new JSONObject();
        preferredName.put("givenName", firstName.getText().toString());
        preferredName.put("familyName", lastName.getText().toString());
        names.put(preferredName);

        int selectedId = radioSexGroup.getCheckedRadioButtonId();
        radioSexButton = (RadioButton) findViewById(selectedId);

        if(radioSexButton.getText().equals(getString(R.string.female)))
            personObj.put("gender", "F");
        else
            personObj.put("gender", "M");

        personObj.put("names", names);

        jsonObject.put("person",personObj);

        JSONArray roles = new JSONArray();
        roles.put("8d94f280-c2cc-11de-8d13-0010c6dffd0f");

        jsonObject.put("roles", roles);


        String baseUrl = TbrApplication.getInstance().getContext().
                configuration().dristhiBaseURL();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        }

        baseUrl = baseUrl.replace("opensrp","openmrs");

        HttpAgentExtended httpAgentExtended = new HttpAgentExtended(getApplicationContext(), TbrApplication.getInstance().getContext().allSettings(), TbrApplication.getInstance().getContext().allSharedPreferences(), TbrApplication.getInstance().getContext().configuration());
        Response<String> resp = httpAgentExtended.post(baseUrl + "/ws/rest/v1/user", jsonObject.toString());

        if(resp.isFailure())
            return "FAILURE";
        else {

            JSONObject obj = new JSONObject(resp.payload());
            if(obj.has("error")){

               return obj.getJSONObject("error").getString("message");

            }
            else
                return "SUCCESS";
        }

    }





}
