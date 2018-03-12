package org.smartregister.tbr.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.BaseEvent;
import org.smartregister.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.abs;

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

    public static String formatDateFromLong(long datetimeInMilliseconds, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datetimeInMilliseconds);
        return formatDate(calendar.getTime(), pattern);
    }

    public static String getInitials(String fullname) {
        try {
            StringBuilder initials = new StringBuilder();
            for (String s : fullname.split(Constants.CHAR.SPACE)) {
                initials.append(s.charAt(0));
            }
            return initials.toString().toUpperCase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public static String getShortInitials(String fullname) {
        String initials = getInitials(fullname);
        return initials.length() > 2 ? initials.substring(0, 2) : initials;
    }


    public static void saveLanguage(String language) {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(TbrApplication.getInstance().getApplicationContext()));
        allSharedPreferences.saveLanguagePreference(language);
        setLocale(new Locale(language));


    }


    public static String getLanguage() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(TbrApplication.getInstance().getApplicationContext()));
        return allSharedPreferences.fetchLanguagePreference();
    }

    public static void setLocale(Locale locale) {
        Resources resources = TbrApplication.getInstance().getApplicationContext().getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
            TbrApplication.getInstance().getApplicationContext().createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, displayMetrics);
        }
    }

    public static void postEvent(BaseEvent event) {
        EventBus.getDefault().post(event);
    }


    public static String getFormattedAgeString(String dobString) {
        String formattedAge = "";
        if (!TextUtils.isEmpty(dobString)) {
            DateTime dateTime = new DateTime(dobString);
            Date dob = dateTime.toDate();
            long timeDiff = Calendar.getInstance().getTimeInMillis() - dob.getTime();

            if (timeDiff >= 0) {
                formattedAge = DateUtil.getDuration(timeDiff);
            }
        }
        return formattedAge.contains("y") ? formattedAge.substring(0, formattedAge.indexOf('y')) : formattedAge;
    }

    public static String formatIdentifier(String identifier) {
        if (identifier != null && !identifier.isEmpty()) {
            String cleanIdentifier = identifier.contains(Constants.CHAR.HASH) ? identifier.replaceAll(Constants.CHAR.HASH, Constants.CHAR.NO_CHAR) : identifier;
            return Constants.CHAR.HASH + cleanIdentifier;
        } else return Constants.CHAR.NO_CHAR;
    }

    public static int getTokenStringResourceId(Context context, String token) {
        return context.getResources().getIdentifier(token, "string", context.getPackageName());
    }

    public static int getLayoutIdentifierResourceId(Context context, String token) {
        return context.getResources().getIdentifier(token, "id", context.getPackageName());
    }

    public static String readPrefString(Context context, final String key, String defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, defaultValue);
    }

    public static void writePrefString(Context context, final String key, final String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static Animation getRotateAnimation() {

        Animation rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(50);
        rotate.setRepeatCount(Animation.INFINITE);

        return rotate;
    }

    public static String getTBTypeByCode(String tbCode) {
        if (tbCode.equals(Constants.PTB)) {
            return Constants.PULMONARY;
        } else if (tbCode.equals(Constants.EPTB)) {
            return Constants.EXTRA_PULMONARY;
        } else {
            return "";
        }
    }

    public static Integer getMonthCountFromDate(Date date, Date date2) {
        String dateFormat = "yyyy-MM-dd";
        LocalDate start = LocalDate.parse(new SimpleDateFormat(dateFormat).format(date));
        LocalDate end = LocalDate.parse(new SimpleDateFormat(dateFormat).format(date2));
        start = start.withDayOfMonth(1);
        end = end.withDayOfMonth(1);
        return abs(Months.monthsBetween(start, end).getMonths()) + 1;
    }

    public static String getTimeAgo(String date) {
        DateTime duration;
        if (StringUtils.isNotBlank(date)) {
            try {
                duration = new DateTime(date);
                return DateUtil.getDuration(duration) + " ago";
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
        return "";
    }

    public static Integer getIntegerValue(Object object) {
        int val = 0;
        if (object != null) {
            try {
                val = Integer.parseInt(object.toString());
            } catch (NumberFormatException e) {
                val = 0;
            }
        }
        return val;
    }
}
