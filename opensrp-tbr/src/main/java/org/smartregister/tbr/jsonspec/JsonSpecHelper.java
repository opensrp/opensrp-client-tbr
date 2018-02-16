package org.smartregister.tbr.jsonspec;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.BaseConfiguration;
import org.smartregister.tbr.jsonspec.model.LoginConfiguration;
import org.smartregister.tbr.jsonspec.model.MainConfig;
import org.smartregister.tbr.jsonspec.model.RegisterConfiguration;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.util.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import util.RuntimeTypeAdapterFactory;

/**
 * Created by ndegwamartin on 12/10/2017.
 */

public class JsonSpecHelper {

    private static final String TAG = JsonSpecHelper.class.getCanonicalName();

    private static final Type VIEW_CONFIG_TYPE = new TypeToken<ViewConfiguration>() {
    }.getType();


    private Context context = null;

    private static final int LANG_SUBSTRING_OFFSET = 5;

    public JsonSpecHelper() {
        if (context == null) {
            throw new IllegalStateException("This class requires Context param. Instantiate using the parameterized constructor");
        }
    }

    public JsonSpecHelper(Context context) {
        this.context = context;
    }

    public MainConfig getMainConfiguration() {
        try {
            String jsonString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(Constants.CONFIGURATION.MAIN);
            return (MainConfig) getConfigurableView(jsonString).getMetadata();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public boolean isEnableJsonViews() {
        return getMainConfiguration() != null && getMainConfiguration().isEnableJsonViews();
    }

    public List<String> getAvailableLanguages() {
        List<String> languages = new ArrayList<>();
        try {
            List<String> langFiles = TbrApplication.getInstance().getConfigurableViewsRepository().getAvailableLanguagesJson();
            if (langFiles != null) {
                for (int i = 0; i < langFiles.size(); i++) {
                    String language = langFiles.get(i).substring(LANG_SUBSTRING_OFFSET);
                    Locale locale = new Locale(language);
                    languages.add(locale.getDisplayLanguage());
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return languages;
    }

    public Map<String, String> getAvailableLanguagesMap() {
        try {
            List<String> langFiles = TbrApplication.getInstance().getConfigurableViewsRepository().getAvailableLanguagesJson();
            Map<String, String> languages = new LinkedHashMap<>();
            for (int i = 0; i < langFiles.size(); i++) {
                String language = langFiles.get(i).substring(LANG_SUBSTRING_OFFSET);
                Locale locale = new Locale(language);
                languages.put(language, locale.getDisplayLanguage());

            }

            return languages;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public ViewConfiguration getConfigurableView(String jsonString) {
        try {
            final RuntimeTypeAdapterFactory<BaseConfiguration> typeFactory = RuntimeTypeAdapterFactory
                    .of(BaseConfiguration.class, "type")
                    .registerSubtype(LoginConfiguration.class, "Login")
                    .registerSubtype(MainConfig.class, "Main")
                    .registerSubtype(RegisterConfiguration.class, "Register");
            final Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();
            return gson.fromJson(jsonString, VIEW_CONFIG_TYPE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public ViewConfiguration getLanguage(String language) {
        try {
            String jsonString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableLanguageJson(Constants.CONFIGURATION.LANG + "_" + language);
            return getConfigurableView(jsonString);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
