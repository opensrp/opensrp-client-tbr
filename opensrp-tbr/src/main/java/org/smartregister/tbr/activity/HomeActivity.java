package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.africastalking.AfricasTalking;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.configurableviews.model.MainConfig;
import org.smartregister.domain.FetchStatus;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.BaseEvent;
import org.smartregister.tbr.event.EnketoFormSaveCompleteEvent;
import org.smartregister.tbr.event.LanguageConfigurationEvent;
import org.smartregister.tbr.event.SyncEvent;
import org.smartregister.tbr.event.TriggerSyncEvent;
import org.smartregister.tbr.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.tbr.fragment.HomeFragment;
import org.smartregister.tbr.sync.ECSyncHelper;
import org.smartregister.tbr.util.Utils;

import java.util.Calendar;
import java.util.Date;

import static org.smartregister.tbr.util.Constants.INTENT_KEY.LAST_SYNC_TIME_STRING;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public class HomeActivity extends BaseActivity {
    private static final String TAG = HomeActivity.class.getCanonicalName();
    private View refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.round_white_background);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            processView();

        }

        /*// Initialize
        String username = "YOUR_USERNAME";    // use 'sandbox' for development in the test environment
        String apiKey = "c5a419b0799fca9b856d750632af1915e4c23a4ecb4885c5b1b00fbba916bcd0";       // use your sandbox app API key for development in the test environment
        AfricasTalking.initialize(username, apiKey);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateLastSync((TextView) findViewById(R.id.registerLastSyncTime));
    }

    private void populateLastSync(TextView textView) {
        textView.setText("Last sync: " + Utils.formatDate(new Date(ECSyncHelper.getInstance(this).getLastCheckTimeStamp()), "MMM dd HH:mm"));
    }

    //
    public void manualSync(View view) {
        view.startAnimation(Utils.getRotateAnimation());
        TriggerSyncEvent viewConfigurationSyncEvent = new TriggerSyncEvent();
        viewConfigurationSyncEvent.setManualSync(true);
        postEvent(viewConfigurationSyncEvent);
        if (view != null) {
            TextView textView = (TextView) view.getRootView().findViewById(R.id.registerLastSyncTime);
            populateLastSync(textView);
        }
    }

    public void postEvent(BaseEvent event) {
        Utils.postEvent(event);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void processView() {

        String fullName = getOpenSRPContext().allSharedPreferences().getANMPreferredName(
                getOpenSRPContext().allSharedPreferences().fetchRegisteredANM());

        refreshButton = findViewById(R.id.refreshSyncButton); //assign RefreshButton

        //set user initials
        if (fullName != null && !fullName.toString().isEmpty()) {
            TextView textView = (TextView) toolbar.findViewById(R.id.custom_toolbar_logo_text);
            textView.setText(Utils.getShortInitials(fullName));
        }

        //Set last sync time
        TextView lastSyncTimeTextView = (TextView) findViewById(R.id.registerLastSyncTime);
        if (lastSyncTimeTextView != null) {
            String defaultLastSyncTime = Utils.formatDate(Calendar.getInstance().getTime(), "MMM dd HH:mm");
            lastSyncTimeTextView.setText("Last sync: " + Utils.readPrefString(this, LAST_SYNC_TIME_STRING, defaultLastSyncTime));
        }

        //Set App Name
        MainConfig config = TbrApplication.getJsonSpecHelper().getMainConfiguration();
        TextView title = (TextView) toolbar.findViewById(R.id.custom_toolbar_title);
        if (config != null && config.getApplicationName() != null) {
            title.setText(config.getApplicationName());
        } else {
            title.setText(R.string.app_title);
        }
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.registers_container, new HomeFragment()).commit();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void refreshViewFromConfigurationChange(ViewConfigurationSyncCompleteEvent syncCompleteEvent) {
        if (syncCompleteEvent != null && refreshButton != null) {
            refreshButton.clearAnimation();
            processView();

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void refreshViewFromConfigurationChange(EnketoFormSaveCompleteEvent enketoFormSaveCompleteEvent) {
        if (enketoFormSaveCompleteEvent != null) {
            processView();

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void refreshViewFromLanguageChange(LanguageConfigurationEvent languageConfigurationEvent) {
        if (languageConfigurationEvent != null) {
            processView();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void refreshView(SyncEvent syncEvent) {
        if (syncEvent != null) {
            processView();

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void triggerSyncSpinner(SyncEvent syncEvent) {
        if (syncEvent != null && syncEvent.getFetchStatus().equals(FetchStatus.fetchStarted) && refreshButton != null) {
            refreshButton.startAnimation(Utils.getRotateAnimation());
        }

    }


}
