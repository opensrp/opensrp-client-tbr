package org.smartregister.tbr.enketoform;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.form.FieldOverrides;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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

    public FieldOverrides populateForm(String formSubmissionId, String enketoForm) {
        Map fields = new HashMap();

        //JSONObject event = TbrApplication.getInstance().getEventClientRepository().getEventsByFormSubmissionId(formSubmissionId);

        String model = readFileAssets(assetsPath + enketoForm + "/model.xml");

        List<Model> tags = parseXML(model);
        try {
            JSONObject formDefinition = new JSONObject(readFileAssets(assetsPath + enketoForm + "/form_definition.json"));
            JSONArray formDefinitionFields = formDefinition.getJSONObject("form").getJSONArray("fields");
            for (int i = 0; i < formDefinitionFields.length(); i++) {
                JSONObject formDefinitionField = formDefinitionFields.getJSONObject(i);
                String formName = formDefinitionField.getString("name");
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error JsonParsing populateEnketoForm: ", e);
        }

        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    private List<Model> parseXML(String xmlInput) {

        ModelXMLHandler modelXMLHandler = new ModelXMLHandler();
        List<Model> modelTags = new ArrayList<>();
        try {

            Log.w(TAG, "Start Parsing ModelXML");
            /** Handling XML */
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
}
