package org.smartregister.tbr.application;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.repository.Repository;
import org.smartregister.tbr.repository.TbrRepository;
import org.smartregister.view.activity.DrishtiApplication;

import static org.smartregister.util.Log.logError;

/**
 * Created by keyman on 23/08/2017.
 */
public class TbrApplication extends DrishtiApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());

        //Initialize Modules
        CoreLibrary.init(context);
    }

    public static synchronized TbrApplication getInstance() {
        return (TbrApplication) mInstance;
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new TbrRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }

    @Override
    public void logoutCurrentUser() {

    }
}
