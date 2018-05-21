package org.smartregister.configurableviews.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ndegwamartin on 21/02/2018.
 */

public class Utils {

    public static void writePrefString(Context context, final String key, final String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String formatDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static List<JSONObject> convertJsonArraytoList(final JSONArray jsonArray) {
        final int size = jsonArray.length();
        final ArrayList<JSONObject> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final JSONObject jsonObject = jsonArray.optJSONObject(i);
            if (jsonObject != null) {
                result.add(jsonObject);
            }
        }
        return result;
    }

    public static JSONArray removeJsonObjectByIndex(final int index, final JSONArray jsonArray) {
        final List<JSONObject> jsonObjects = convertJsonArraytoList(jsonArray);
        jsonObjects.remove(index);

        final JSONArray newJsonArray = new JSONArray();
        for (final JSONObject obj : jsonObjects) {
            newJsonArray.put(obj);
        }

        return newJsonArray;
    }

}
