package org.smartregister.tbr.jsonspec;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.BaseConfiguration;
import org.smartregister.tbr.jsonspec.model.LoginConfiguration;
import org.smartregister.tbr.jsonspec.model.MainConfig;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.util.Constants;

import java.io.InputStreamReader;
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
    private static String BASE_PATH = "synced";
    private static final String TAG = JsonSpecHelper.class.getCanonicalName();

    private static final Type MAIN_CONFIG_TYPE = new TypeToken<MainConfig>() {
    }.getType();

    private static final Type LANG_TYPE = new TypeToken<Map<String, String>>() {
    }.getType();


    private static final Type VIEW_CONFIG_TYPE = new TypeToken<ViewConfiguration>() {
    }.getType();


    private Context context = null;

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

    public List<String> getAvailableLanguages() {
        try {
            String[] langFiles = context.getResources().getAssets().list(BASE_PATH + "/lang");
            List<String> languages = new ArrayList<>();
            for (int i = 0; i < langFiles.length; i++) {
                String language = langFiles[i].substring(0, langFiles[i].indexOf('.'));
                Locale locale = new Locale(language);
                languages.add(locale.getDisplayLanguage());

            }

            return languages;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public Map<String, String> getAvailableLanguagesMap() {
        try {
            String[] langFiles = context.getResources().getAssets().list(BASE_PATH + "/lang");
            Map<String, String> languages = new LinkedHashMap<>();
            for (int i = 0; i < langFiles.length; i++) {
                String language = langFiles[i].substring(0, langFiles[i].indexOf('.'));
                Locale locale = new Locale(language);
                languages.put(language, locale.getDisplayLanguage());

            }

            return languages;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public Map<String, String> getLanguageFile(String language) {
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new InputStreamReader(context.getResources().getAssets().open(BASE_PATH + "/lang/" + language + ".json")));
            return gson.fromJson(reader, LANG_TYPE);
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
                    .registerSubtype(MainConfig.class, "Main");
            final Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();
            return gson.fromJson(jsonString, VIEW_CONFIG_TYPE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

}
