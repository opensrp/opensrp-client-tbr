package org.smartregister.tbr.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.JsonSpecHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by ndegwamartin on 10/10/2017.
 */

public class Utils {

    public static final String TAG = Utils.class.getCanonicalName();

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }

    public static String formatDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String getInitials(String fullname) {
        try {
            StringBuilder initials = new StringBuilder();
            for (String s : fullname.split(Constants.CHAR.SPACE)) {
                initials.append(s.charAt(0));
            }
            return initials.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }


    public static void saveLanguage(String language) {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(TbrApplication.getInstance().getApplicationContext()));
        allSharedPreferences.saveLanguagePreference(language);
    }


    public static String getLanguage() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(TbrApplication.getInstance().getApplicationContext()));
        return allSharedPreferences.fetchLanguagePreference();
    }
}
