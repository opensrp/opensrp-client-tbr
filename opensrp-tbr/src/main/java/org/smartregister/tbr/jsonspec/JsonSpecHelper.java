package org.smartregister.tbr.jsonspec;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.smartregister.tbr.jsonspec.model.Language;
import org.smartregister.tbr.jsonspec.model.MainConfig;
import org.smartregister.tbr.jsonspec.model.Register;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ndegwamartin on 12/10/2017.
 */

public class JsonSpecHelper {
    public static String BASE_PATH = "synced";
    public static final String TAG = JsonSpecHelper.class.getCanonicalName();
    private static final Type REGISTER_TYPE = new TypeToken<Register>() {
    }.getType();
    private static final Type MAIN_CONFIG_TYPE = new TypeToken<MainConfig>() {
    }.getType();

    private static final Type LANG_TYPE = new TypeToken<Map<String, String>>() {
    }.getType();


    private static Context context;

    public JsonSpecHelper() {
        if (context == null) {
            throw new IllegalStateException("This class requires Context param. Instantiate using the parameterized constructor");
        }
    }

    public JsonSpecHelper(Context context) {
        this.context = context;
    }

    public static MainConfig getMainConfigFile() {
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new InputStreamReader(context.getResources().getAssets().open(BASE_PATH + "/configs/views/main_config.json")));
            return gson.fromJson(reader, MAIN_CONFIG_TYPE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public static List<String> getAvailableLanguages() {
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

    public static Map<String, String> getAvailableLanguagesMap() {
        try {
            String[] langFiles = context.getResources().getAssets().list(BASE_PATH + "/lang");
            Map<String, String> languages = new HashMap<>();
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

    public static Map<String, String> getLanguageFile(String language) {
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new InputStreamReader(context.getResources().getAssets().open(BASE_PATH + "/lang/" + language + ".json")));
            return gson.fromJson(reader, LANG_TYPE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }


}
