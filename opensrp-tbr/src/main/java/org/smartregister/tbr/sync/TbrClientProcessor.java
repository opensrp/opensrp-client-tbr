package org.smartregister.tbr.sync;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.sync.ClientProcessor;

import java.util.List;

/**
 * Created by samuelgithengi on 11/13/17.
 */

public class TbrClientProcessor extends ClientProcessor {

    private static TbrClientProcessor instance;

    public TbrClientProcessor(Context context) {
        super(context);
    }

    public static TbrClientProcessor getInstance(Context context) {
        if (instance == null) {
            instance = new TbrClientProcessor(context);
        }

        return instance;
    }

    @Override
    public synchronized void processClient(List<JSONObject> events) throws Exception {
        super.processClient(events);
    }
}
