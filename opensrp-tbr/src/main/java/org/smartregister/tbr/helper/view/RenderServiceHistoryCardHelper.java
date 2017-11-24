package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteQueryBuilder;

import org.json.JSONObject;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.tbr.R;
import org.smartregister.tbr.adapter.ServiceHistoryAdapter;
import org.smartregister.tbr.repository.ResultDetailsRepository;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.tbr.util.Constants;

import java.util.HashMap;
import java.util.Map;

import util.TbrConstants;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderServiceHistoryCardHelper extends BaseRenderHelper {

    private Cursor mCursor;

    public RenderServiceHistoryCardHelper(Context context, ResultDetailsRepository detailsRepository) {
        super(context, detailsRepository);

    }


    @Override
    public void renderView(final String baseEntityId, final View view) {

        new Handler().post(new Runnable() {

            @Override
            public void run() {

                ListView listView = (ListView) view.findViewById(R.id.serviceHistoryListView);

                String[] mProjection = {
                        ResultsRepository.ID,
                        ResultsRepository.TYPE,
                        ResultsRepository.FORMSUBMISSION_ID,
                        ResultsRepository.DATE,
                };

                String[] mProjection2 = {
                        ECClientRepository.ID,
                        "\"Registration\"",
                        ECClientRepository.ID,
                        ECClientRepository.FIRST_ENCOUNTER,
                };


                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ResultsRepository.TABLE_NAME);
                String projectionStr = getProjectionString(mProjection);
                String projectionStrTwo = getProjectionString(mProjection2);

                String[] subQueries = new String[]{
                        "SELECT " + projectionStr + " FROM " + ResultsRepository.TABLE_NAME,
                        "SELECT " + projectionStrTwo + " FROM " + ECClientRepository.TABLE_NAME};
                String sql = builder.buildUnionQuery(subQueries, ResultsRepository.DATE + " DESC", null);


                mCursor = repository.getReadableDatabase().rawQuery(sql, null);

                ServiceHistoryAdapter adapter = new ServiceHistoryAdapter(context, mCursor, 0);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Snackbar.make(view, view.getTag(R.id.FORM_NAME) + " Filled on : " + "\n" + view.getTag(R.id.FORM_SUBMITTED_DATE), Snackbar.LENGTH_LONG)
                                .setAction("No action", null).show();
                    }
                });

            }

        });

    }

    private String getProjectionString(String[] projection) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : projection)
            stringBuilder.append(s).append(",");

        String projectionStr = stringBuilder.toString();
        projectionStr = projectionStr.substring(0,
                projectionStr.length() - 1);
        return projectionStr;

    }

    @Override
    public void renderView(String baseEntityId, View view, final Map<String, String> extra) {
        //Inherited
    }

    private class ECClientRepository {
        public static final String ID = "id";
        public static final String TABLE_NAME = "ec_patient";
        public static final String FIRST_ENCOUNTER = Constants.KEY.LAST_INTERACTED_WITH;
    }


}
