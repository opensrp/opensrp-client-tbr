package org.smartregister.configurableviews;


import org.smartregister.Context;
import org.smartregister.configurableviews.helper.ConfigurableViewsHelper;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.repository.Repository;

/**
 * Created by ndegwamartin on 16/02/2018.
 */

public class ConfigurableViewsLibrary {

    public static ConfigurableViewsLibrary instance;
    public static Context context;
    private String password;

    private Repository repository;
    private ConfigurableViewsRepository configurableViewsRepository;
    public static JsonSpecHelper jsonSpecHelper;
    private ConfigurableViewsHelper configurableViewsHelper;

    public static void init(Context context_, Repository repository) {
        if (instance == null) {
            ConfigurableViewsLibrary.context = context_;
            ConfigurableViewsLibrary.instance = new ConfigurableViewsLibrary(repository);
            ConfigurableViewsLibrary.jsonSpecHelper = new JsonSpecHelper(context.applicationContext());
        }
    }

    public static ConfigurableViewsLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException(" Instance does not exist!!! Call " + ConfigurableViewsLibrary.class.getName() + ".init method in the onCreate method of your Application class ");
        }
        return instance;
    }

    private ConfigurableViewsLibrary(Repository repository) {
        this.repository = repository;
    }

    public ConfigurableViewsRepository getConfigurableViewsRepository() {
        if (configurableViewsRepository == null) {
            configurableViewsRepository = new ConfigurableViewsRepository(getRepository());
        }
        return configurableViewsRepository;
    }

    public Repository getRepository() {
        return repository;
    }

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
    }

    public static Context getContext() {
        return context;
    }

    public String getPassword() {
        if (password == null) {
            String username = getContext().userService().getAllSharedPreferences().fetchRegisteredANM();
            password = getContext().userService().getGroupId(username);
        }
        return password;
    }

    public ConfigurableViewsHelper getConfigurableViewsHelper() {
        if (configurableViewsHelper == null) {
            configurableViewsHelper = new ConfigurableViewsHelper(getConfigurableViewsRepository(),
                    getJsonSpecHelper(), context.applicationContext());
        }
        return configurableViewsHelper;
    }
}