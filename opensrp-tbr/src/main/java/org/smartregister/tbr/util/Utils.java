package org.smartregister.tbr.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ndegwamartin on 10/10/2017.
 */

public class Utils {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }
}
