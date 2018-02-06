package org.smartregister.tbr.application;

import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.tbr.activity.LoginActivity;
import org.smartregister.tbr.event.LanguageConfigurationEvent;
import org.smartregister.tbr.event.TriggerSyncEvent;
import org.smartregister.tbr.jsonspec.ConfigurableViewsHelper;
import org.smartregister.tbr.jsonspec.JsonSpecHelper;
import org.smartregister.tbr.jsonspec.model.MainConfig;
import org.smartregister.tbr.receiver.TbrSyncBroadcastReceiver;
import org.smartregister.tbr.repository.BMIRepository;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;
import org.smartregister.tbr.repository.ResultDetailsRepository;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.tbr.repository.TbrRepository;
import org.smartregister.tbr.service.PullConfigurableViewsIntentService;
import org.smartregister.tbr.service.SyncService;
import org.smartregister.tbr.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import util.ServiceTools;
import util.TbrConstants;
import util.TbrConstants.KEY;

import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

/**
 * Created by keyman on 23/08/2017.
 */
public class TbrApplication extends DrishtiApplication {

    private static JsonSpecHelper jsonSpecHelper;

    private ConfigurableViewsRepository configurableViewsRepository;
    private EventClientRepository eventClientRepository;
    private ResultsRepository resultsRepository;
    private static CommonFtsObject commonFtsObject;
    private ResultDetailsRepository resultDetailsRepository;
    private ConfigurableViewsHelper configurableViewsHelper;
    private BMIRepository bmiRepository;

    private static final String TAG = TbrApplication.class.getCanonicalName();
    private String password;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context);

        DrishtiSyncScheduler.setReceiverClass(TbrSyncBroadcastReceiver.class);

        startPullConfigurableViewsIntentService(getApplicationContext());
        try {
            Utils.saveLanguage("en");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        //Initialize JsonSpec Helper
        this.jsonSpecHelper = new JsonSpecHelper(this);

        //setUpEventHandling();

    }

    public static synchronized TbrApplication getInstance() {
        return (TbrApplication) mInstance;
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new TbrRepository(getInstance().getApplicationContext(), context);
                getConfigurableViewsRepository();
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }

    public String getPassword() {
        if (password == null) {
            String username = getContext().userService().getAllSharedPreferences().fetchRegisteredANM();
            password = getContext().userService().getGroupId(username);
        }
        return password;
    }

    @Override
    public void logoutCurrentUser() {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
    }

    public Context getContext() {
        return context;
    }

    protected void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

    @Override
    public void onTerminate() {
        logInfo("Application is terminating. Stopping Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        TimeChangedBroadcastReceiver.destroy(this);
        super.onTerminate();
    }

    public void startPullConfigurableViewsIntentService(android.content.Context context) {
        Intent intent = new Intent(context, PullConfigurableViewsIntentService.class);
        context.startService(intent);
    }

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields());
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields());
            }
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{TbrConstants.PATIENT_TABLE_NAME};
    }

    private static String[] getFtsSearchFields() {
        return new String[]{KEY.PARTICIPANT_ID, KEY.PROGRAM_ID, KEY.FIRST_NAME, KEY.LAST_NAME};

    }

    private static String[] getFtsSortFields() {
        return new String[]{KEY.PARTICIPANT_ID, KEY.PROGRAM_ID, KEY.FIRST_NAME,
                KEY.LAST_INTERACTED_WITH, KEY.PRESUMPTIVE, KEY.CONFIRMED_TB, KEY.FIRST_ENCOUNTER,
                KEY.DIAGNOSIS_DATE, KEY.TREATMENT_INITIATION_DATE, KEY.DATE_REMOVED};
    }


    public ConfigurableViewsRepository getConfigurableViewsRepository() {
        if (configurableViewsRepository == null)
            configurableViewsRepository = new ConfigurableViewsRepository(getRepository());
        return configurableViewsRepository;
    }

    public EventClientRepository getEventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository(getRepository());
        }
        return eventClientRepository;
    }

    public ResultsRepository getResultsRepository() {
        if (resultsRepository == null) {
            resultsRepository = new ResultsRepository(getRepository());
        }
        return resultsRepository;
    }

    public ResultDetailsRepository getResultDetailsRepository() {
        if (resultDetailsRepository == null) {
            resultDetailsRepository = new ResultDetailsRepository(getRepository());
        }
        return resultDetailsRepository;
    }

    public ConfigurableViewsHelper getConfigurableViewsHelper() {
        if (configurableViewsHelper == null) {
            configurableViewsHelper = new ConfigurableViewsHelper(getConfigurableViewsRepository(),
                    getJsonSpecHelper(), getApplicationContext());
        }
        return configurableViewsHelper;
    }

    public BMIRepository getBmiRepository() {
        if (bmiRepository == null) {
            bmiRepository = new BMIRepository(getRepository());
        }
        return bmiRepository;
    }

    private void setUpEventHandling() {
        EventBus.builder().addIndex(new org.smartregister.tbr.TBREventBusIndex()).installDefaultEventBus();
        EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void triggerSync(TriggerSyncEvent event) {
        if (event != null) {
            startPullConfigurableViewsIntentService(this);
            ServiceTools.startService(getApplicationContext(), SyncService.class);
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void setServerLanguage(LanguageConfigurationEvent event) {
        //Set Language
        MainConfig config = TbrApplication.getJsonSpecHelper().getMainConfiguration();
        if (config != null && config.getLanguage() != null && event.isFromServer()) {
            Utils.saveLanguage(config.getLanguage());
        }
    }
}
