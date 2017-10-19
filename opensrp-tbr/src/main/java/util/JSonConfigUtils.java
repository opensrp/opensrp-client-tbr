package util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by SGithengi on 10/11/17.
 */

public class JSonConfigUtils {

    private static String TAG="JSonConfigUtils";

    /**
     * Helper function to load file from assets
     */
    public static String readJsonFile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line;
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            Log.e(TAG,"Error reading json file  "+fileName);
        } finally {
            try {
                if (isr != null) isr.close();
                if (fIn != null) fIn.close();
                if (input != null) input.close();
            } catch (Exception e2) {
                Log.e(TAG,"Error during cleanup");
            }
        }
        return returnString.toString();
    }
}
