package org.smartregister.tbr.helper.view;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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

public class RenderServiceHistoryCardHelper extends BaseRenderHelper implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String UNION_TABLE_FLAG = "union_table_flag";
    private static final Uri CONTENT_URI = Uri.parse("content://com.smartregister.tbr.service.history.provider");
    private static final int SERVICE_HISTORY_LOADER_ID = 0;
    private static String TAG = RenderServiceHistoryCardHelper.class.getCanonicalName();
    private String BASE_ENTITY_ID = "";

    private LoaderManager.LoaderCallbacks<Cursor> loaderManagerCallbackInterface;
    private ListView listView;

    public RenderServiceHistoryCardHelper(Context context, ResultDetailsRepository detailsRepository) {
        super(context, detailsRepository);

    }

    @Override
    public void renderView(final View view, final Map<String, String> metadata) {
        BASE_ENTITY_ID = metadata.get(Constants.KEY._ID);
        loaderManagerCallbackInterface = this;
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                listView = (ListView) view.findViewById(R.id.serviceHistoryListView);
                if (listView != null) {
                    listView.setTag(TB_REACH_ID, metadata.get(Constants.KEY.TBREACH_ID));
                    ((Activity) context).getLoaderManager().initLoader(SERVICE_HISTORY_LOADER_ID, null, loaderManagerCallbackInterface);

                }

            }


        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] mProjection = {
                "1 || " + ResultsRepository.ID + " _id", //union query hence unique identifier
                ResultsRepository.TYPE,
                ResultsRepository.FORMSUBMISSION_ID,
                ResultsRepository.DATE,
                ResultsRepository.BASE_ENTITY_ID,
                "1 " + RenderServiceHistoryCardHelper.UNION_TABLE_FLAG,
                ResultsRepository.CREATED_AT
        };

        String[] mProjection2 = {
                "0 || " + ECClientRepository.ID + " _id", //union query hence unique identifier
                "\"Screening\"",
                ECClientRepository.ID,
                ECClientRepository.FIRST_ENCOUNTER,
                ResultsRepository.BASE_ENTITY_ID,
                "0 " + RenderServiceHistoryCardHelper.UNION_TABLE_FLAG,
                ECClientRepository.BASELINE + " " + ResultsRepository.CREATED_AT
        };

        return new CursorLoader(context, CONTENT_URI,
                mProjection, " WHERE " + ResultsRepository.BASE_ENTITY_ID + "='" + BASE_ENTITY_ID + "'", mProjection2,
                RenderServiceHistoryCardHelper.UNION_TABLE_FLAG + " DESC, " + ResultsRepository.DATE + " DESC, " + ResultsRepository.CREATED_AT + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        try {
            cursor.moveToFirst();
            ServiceHistoryAdapter adapter = new ServiceHistoryAdapter(context, cursor, 0);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Overridden
    }

    public class ECClientRepository {
        public static final String ID = "id";
        public static final String TABLE_NAME = "ec_patient";
        public static final String FIRST_ENCOUNTER = Constants.KEY.FIRST_ENCOUNTER;
        public static final String BASELINE = "baseline";
    }

}
