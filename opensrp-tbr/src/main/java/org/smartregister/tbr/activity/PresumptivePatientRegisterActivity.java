package org.smartregister.tbr.activity;

import org.smartregister.provider.SmartRegisterClientsProvider;

/**
 * Created by samuelgithengi on 10/30/17.
 */

public class PresumptivePatientRegisterActivity extends BaseRegisterActivity {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
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
    public void startRegistration() {

    }
}
