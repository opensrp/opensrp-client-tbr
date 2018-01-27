package org.smartregister.tbr.helper.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteQueryBuilder;

import org.smartregister.tbr.R;
import org.smartregister.tbr.adapter.ServiceHistoryAdapter;
import org.smartregister.tbr.repository.ResultDetailsRepository;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.tbr.util.Constants;

import java.util.Map;

import static org.smartregister.tbr.R.id.TB_REACH_ID;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderServiceHistoryCardHelper extends BaseRenderHelper {

    public static final String UNION_TABLE_FLAG = "union_table_flag";

    public RenderServiceHistoryCardHelper(Context context, ResultDetailsRepository detailsRepository) {
        super(context, detailsRepository);

    }

    @Override
    public void renderView(final View view, final Map<String, String> metadata) {
        new Handler().post(new Runnable() {
            final String baseEntityId = metadata.get(Constants.KEY._ID);

            @Override
            public void run() {

                ListView listView = (ListView) view.findViewById(R.id.serviceHistoryListView);
                if (listView != null) {
                    listView.setTag(TB_REACH_ID, metadata.get(Constants.KEY.PARTICIPANT_ID));

                    String[] mProjection = {
                            ResultsRepository.ID,
                            ResultsRepository.TYPE,
                            ResultsRepository.FORMSUBMISSION_ID,
                            ResultsRepository.DATE,
                            ResultsRepository.BASE_ENTITY_ID,
                            "1 " + RenderServiceHistoryCardHelper.UNION_TABLE_FLAG
                    };

                    String[] mProjection2 = {
                            ECClientRepository.ID,
                            "\"Registration\"",
                            ECClientRepository.ID,
                            ECClientRepository.FIRST_ENCOUNTER,
                            ResultsRepository.BASE_ENTITY_ID,
                            "0 " + RenderServiceHistoryCardHelper.UNION_TABLE_FLAG
                    };

                    SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                    builder.setTables(ResultsRepository.TABLE_NAME);
                    String projectionStr = getProjectionString(mProjection);
                    String projectionStrTwo = getProjectionString(mProjection2);

                    String[] subQueries = new String[]{
                            "SELECT " + projectionStr + " FROM " + ResultsRepository.TABLE_NAME + " WHERE " + ResultsRepository.BASE_ENTITY_ID + "='" + baseEntityId + "'",
                            "SELECT " + projectionStrTwo + " FROM " + ECClientRepository.TABLE_NAME + " WHERE " + ResultsRepository.BASE_ENTITY_ID + "='" + baseEntityId + "'"};
                    String sql = builder.buildUnionQuery(subQueries, RenderServiceHistoryCardHelper.UNION_TABLE_FLAG + " DESC, " + ResultsRepository.DATE + " DESC", null);


                    Cursor mCursor = repository.getReadableDatabase().rawQuery(sql, null);
                    ((Activity) context).startManagingCursor(mCursor);
                    ServiceHistoryAdapter adapter = new ServiceHistoryAdapter(context, mCursor, 0);
                    listView.setAdapter(adapter);
                }
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

    private class ECClientRepository {
        public static final String ID = "id";
        public static final String TABLE_NAME = "ec_patient";
        public static final String FIRST_ENCOUNTER = Constants.KEY.FIRST_ENCOUNTER;
    }


}
