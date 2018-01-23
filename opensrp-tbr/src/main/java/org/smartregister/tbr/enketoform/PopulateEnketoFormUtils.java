package org.smartregister.tbr.enketoform;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.tbr.application.TbrApplication;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static org.smartregister.repository.EventClientRepository.Table.event;
import static org.smartregister.repository.EventClientRepository.event_column;

/**
 * Created by samuelgithengi on 1/19/18.
 */

public class PopulateEnketoFormUtils {

    private static String TAG = PopulateEnketoFormUtils.class.getCanonicalName();

    private static PopulateEnketoFormUtils instance;

    private String assetsPath = "www/form/";

    private Context context;

    public static PopulateEnketoFormUtils getInstance(Context context) {
        if (instance == null) {
            instance = new PopulateEnketoFormUtils(context);
        }
        return instance;
    }

    private PopulateEnketoFormUtils(Context context) {
        this.context = context;
    }

    public FieldOverrides populateFormOverrides(String baseEntityId, String formSubmissionId, String enketoForm) {
        Map fields = new HashMap();
        String model = readFileAssets(assetsPath + enketoForm + "/model.xml");
        ModelXMLHandler modelXMLHandler = new ModelXMLHandler();
        List<Model> tags = parseXML(modelXMLHandler, model);
        JSONObject event;
        if (formSubmissionId != null) {
            event = TbrApplication.getInstance().getEventClientRepository().getEventsByFormSubmissionId(formSubmissionId);
        } else {
            event = getEventsByBaseEntityIdAndEventType(baseEntityId, modelXMLHandler.getEventType());
        }
        if (event == null)
            return new FieldOverrides(new JSONObject().toString());
        Type listType = new TypeToken<List<Obs>>() {
        }.getType();
        try {
            List<Obs> obs = new Gson().fromJson(event.getJSONArray("obs").toString(), listType);
            Map<String, Obs> formObservations = new HashMap<>();
            for (Obs observation : obs)
                formObservations.put(observation.getFormSubmissionField(), observation);
            for (Model tag : tags) {
                Obs observation = formObservations.get(tag.getTag());
                if (observation != null) {
                    if (!observation.getHumanReadableValues().isEmpty())
                        fields.put(tag.getTag(), TextUtils.join(",", observation.getHumanReadableValues()));
                    else
                        fields.put(tag.getTag(), observation.getValue());
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "populateFormOverrides: Error Parsing Json ", e);
        }

        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    private List<Model> parseXML(ModelXMLHandler modelXMLHandler, String xmlInput) {
        List<Model> modelTags = new ArrayList<>();
        try {
            Log.w(TAG, "Start Parsing ModelXML");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            xr.setContentHandler(modelXMLHandler);
            InputSource inStream = new InputSource();

            inStream.setCharacterStream(new StringReader(xmlInput));

            xr.parse(inStream);

            modelTags = modelXMLHandler.getTags();
        } catch (SAXTerminationException e) {
            modelTags = modelXMLHandler.getTags();
            Log.i(TAG, "Finished Parsing the Model");
        } catch (Exception e) {
            Log.w(e.getMessage(), e);
        }
        for (Model model : modelTags) {
            Log.i(TAG, "getTag: " + model.getTag() + " : " + model.getOpenMRSEntity() + " : " + model.getOpenMRSEntityId());
        }
        return modelTags;
    }

    private String readFileAssets(String fileName) {
        String fileContents;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.e(TAG, ex.toString(), ex);
            return null;
        }
        return fileContents;
    }


    private JSONObject getEventsByBaseEntityIdAndEventType(String baseEntityId, String eventType) {
        if (StringUtils.isBlank(baseEntityId)) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT json FROM "
                    + event.name()
                    + " WHERE "
                    + event_column.baseEntityId.name()
                    + "= ? AND " + event_column.eventType.name() + "= ? ", new String[]{baseEntityId, eventType});
            if (cursor.moveToNext()) {
                String jsonEventStr = cursor.getString(0);

                jsonEventStr = jsonEventStr.replaceAll("'", "");

                return new JSONObject(jsonEventStr);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private SQLiteDatabase getReadableDatabase() {
        return TbrApplication.getInstance().getRepository().getReadableDatabase();
    }
}
