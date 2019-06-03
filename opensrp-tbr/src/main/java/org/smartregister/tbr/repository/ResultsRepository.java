package org.smartregister.tbr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.tbr.model.Result;
import org.smartregister.tbr.util.Constants;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResultsRepository extends BaseRepository {

    private static final String TAG = "ResultsRepository";

    public static final String TABLE_NAME = "results";
    public static final String ID = "_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String TYPE = "type";
    public static final String RESULT1 = "result1";
    public static final String VALUE1 = "value1";
    public static final String RESULT2 = "result2";
    public static final String VALUE2 = "value2";
    public static final String FORMSUBMISSION_ID = "formSubmissionId";
    public static final String EVENT_ID = "event_id";
    public static final String DATE = "date";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT_COLUMN = "updated_at";
    public static final String ANMID = "anmid";
    public static final String LOCATIONID = "location_id";
    public static final String SYNC_STATUS = "sync_status";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String HAEMOGLOBIN = "haemoglobin";
    public static final String NEXT_VISIT_DATE = "next_visit_date";
    public static final String NEXT_GROWTH_MONITORING_DATE = "next_growth_monitoring_date";
    public static final String DIARREA = "diarrea";
    public static final String MALARIA = "malaria";
    public static final String COLD = "cold";
    public static final String PNEUMONIA = "pneumonia";
    public static final String TUBERCULOSIS = "tuberculosis";
    public static final String DENGUE = "dengue";
    public static final String HEIGHT_AGE_STATUS = "height_age_status";
    public static final String WEIGHT_HEIGHT_STATUS = "weight_height_status";
    public static final String DEWORMING = "deworming";
    public static final String DEWORMING_DATE = "deworming_date";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            BASE_ENTITY_ID + "  VARCHAR NOT NULL, " +
            WEIGHT + " REAL NOT NULL, " +
            HEIGHT + " REAL NOT NULL, " +
            HEIGHT_AGE_STATUS + " VARCHAR NOT NULL, " +
            WEIGHT_HEIGHT_STATUS + " VARCHAR NOT NULL, " +
            HAEMOGLOBIN + " REAL NOT NULL, " +
            NEXT_VISIT_DATE + " VARCHAR NOT NULL, " +
            NEXT_GROWTH_MONITORING_DATE + " VARCHAR NOT NULL, " +
            DEWORMING + " VARCHAR NOT NULL, " +
            DEWORMING_DATE + " VARCHAR NULL, " +
            DIARREA + " VARCHAR NULL, " +
            MALARIA + " VARCHAR NULL, " +
            COLD + " VARCHAR NULL, " +
            PNEUMONIA + " VARCHAR NULL, " +
            TUBERCULOSIS + " VARCHAR NULL, " +
            DENGUE + " VARCHAR NULL, " +
            TYPE + "  VARCHAR NOT NULL, " +
            RESULT1 + "  VARCHAR NULL, " +
            VALUE1 + "  VARCHAR NULL," +
            RESULT2 + "  VARCHAR  NULL, " +
            VALUE2 + "  VARCHAR NULL," +
            FORMSUBMISSION_ID + "  VARCHAR NOT NULL, " +
            EVENT_ID + "  VARCHAR  NULL, " +
            DATE + "  DATETIME NOT NULL, " +
            ANMID + "  VARCHAR NOT NULL, " +
            LOCATIONID + "  VARCHAR NOT NULL, " +
            SYNC_STATUS + "  VARCHAR NOT NULL, " +
            CREATED_AT + " VARCHAR NOT NULL, " +
            UPDATED_AT_COLUMN + " INTEGER NOT NULL, " +
            "UNIQUE(base_entity_id, formSubmissionId) ON CONFLICT IGNORE )";

    private static final String INDEX_ID = "CREATE INDEX " + TABLE_NAME + "_" + ID +
            "_index ON " + TABLE_NAME + "(" + ID + " COLLATE NOCASE);";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + TABLE_NAME + "_" + BASE_ENTITY_ID +
            "_index ON " + TABLE_NAME + "(" + BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_RESULT_TYPE = "CREATE INDEX " + TABLE_NAME + "_" + TYPE +
            "_index ON " + TABLE_NAME + "(" + TYPE + " COLLATE NOCASE);";

    public ResultsRepository(Repository repository) {
        super(repository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_ID);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_RESULT_TYPE);
    }

    public void saveResult(Result result) {
        if (result == null)
            return;
        else if (result.getUpdatedAt() == null) {
            result.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
        }
        if (result.getId() == null) {
            Long existingId = getExistingResultId(result);
            if (existingId != null) {
                result.setId(existingId);
                update(result);
            } else {
                getWritableDatabase().insert(TABLE_NAME, null, createValuesFor(result));
            }
        } else {
            result.setSyncStatus(TYPE_Unsynced);
            update(result);
        }
    }

    private void update(Result result) {
        ContentValues contentValues = createValuesFor(result);
        getWritableDatabase().update(TABLE_NAME, contentValues, ID + " = ?", new String[]{result.getId().toString()});
    }

    private ContentValues createValuesFor(Result result) {
        ContentValues values = new ContentValues();
        values.put(BASE_ENTITY_ID, result.getBaseEntityId());
        values.put(TYPE, result.getType());
        values.put(RESULT1, result.getResult1());
        values.put(VALUE1, result.getValue1());
        values.put(RESULT2, result.getResult2());
        values.put(VALUE2, result.getValue2());
        values.put(DATE, result.getDate().getTime());
        values.put(ANMID, result.getAnmId());
        values.put(LOCATIONID, result.getLocationId());
        values.put(SYNC_STATUS, result.getSyncStatus());
        values.put(WEIGHT, result.getWeight());
        values.put(HEIGHT, result.getHeight());
        values.put(HEIGHT_AGE_STATUS, result.getHeightAgeStatus());
        values.put(WEIGHT_HEIGHT_STATUS, result.getWeightHeightStatus());
        values.put(DEWORMING, result.getDeworming());
        values.put(DEWORMING_DATE, result.getDewormingDate());
        values.put(NEXT_VISIT_DATE, result.getNextVisitDate());
        values.put(NEXT_GROWTH_MONITORING_DATE, result.getNextGrowthMonitoringDate());
        values.put(DIARREA, result.getDiarrea());
        values.put(MALARIA, result.getMalaria());
        values.put(COLD, result.getCold());
        values.put(PNEUMONIA, result.getPneumonia());
        values.put(TUBERCULOSIS, result.getTuberculosis());
        values.put(DENGUE, result.getDengue());
        values.put(HAEMOGLOBIN, result.getHaemoglobin());
        values.put(FORMSUBMISSION_ID, result.getFormSubmissionId());
        values.put(CREATED_AT, result.getCreatedAt());
        values.put(UPDATED_AT_COLUMN, result.getUpdatedAt());
        return values;
    }

    private Long getExistingResultId(Result result) {
        String selection = null;
        String[] selectionArgs = null;
        Long id = null;
        if (StringUtils.isNotBlank(result.getFormSubmissionId()) && StringUtils.isNotBlank(result.getEventId())) {
            selection = FORMSUBMISSION_ID + " = ? " + COLLATE_NOCASE + " OR " + EVENT_ID + " = ? " + COLLATE_NOCASE;
            selectionArgs = new String[]{result.getFormSubmissionId(), result.getEventId()};
        } else if (StringUtils.isNotBlank(result.getEventId())) {
            selection = EVENT_ID + " = ? " + COLLATE_NOCASE;
            selectionArgs = new String[]{result.getEventId()};
        } else if (StringUtils.isNotBlank(result.getFormSubmissionId())) {
            selection = FORMSUBMISSION_ID + " = ? " + COLLATE_NOCASE;
            selectionArgs = new String[]{result.getFormSubmissionId()};
        }
        Cursor cursor = getReadableDatabase().query(TABLE_NAME, new String[]{ID}, selection, selectionArgs, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }

    public Map<String, String> getLatestResults(String baseEntityId) {
        return getLatestResults(baseEntityId, false, null);
    }

    public Map<String, String> getLatestResults(String baseEntityId, boolean singleResult, Long baseline) {
        Cursor cursor = null;
        Map<String, String> clientDetails = new LinkedHashMap<>();
        try {
            cursor = getLatestResultsCursor(baseEntityId, baseline, singleResult, false, false);

            if (cursor != null && cursor.moveToFirst()) {
                do {
/*                    String key = cursor.getString(cursor.getColumnIndex(RESULT1));
                    String value = cursor.getString(cursor.getColumnIndex(VALUE1));
                    clientDetails.put(key, value);
                    clientDetails.put(DATE, cursor.getString(cursor.getColumnIndex(DATE)));
                    String key2 = cursor.getString(cursor.getColumnIndex(RESULT2));
                    if (key2 != null && !key2.isEmpty()) {
                        value = cursor.getString(cursor.getColumnIndex(VALUE2));
                        clientDetails.put(key2, value);
                    }*/

                    String key = WEIGHT;
                    String value = cursor.getString(cursor.getColumnIndex(WEIGHT));
                    clientDetails.put(key, value);

                    key = HEIGHT;
                    value = cursor.getString(cursor.getColumnIndex(HEIGHT));
                    clientDetails.put(key, value);

                    key = HEIGHT_AGE_STATUS;
                    value = cursor.getString(cursor.getColumnIndex(HEIGHT_AGE_STATUS));
                    clientDetails.put(key, value);

                    key = WEIGHT_HEIGHT_STATUS;
                    value = cursor.getString(cursor.getColumnIndex(WEIGHT_HEIGHT_STATUS));
                    clientDetails.put(key, value);

                    key = HAEMOGLOBIN;
                    value = cursor.getString(cursor.getColumnIndex(HAEMOGLOBIN));
                    clientDetails.put(key, value);

                    key = NEXT_VISIT_DATE;
                    value = cursor.getString(cursor.getColumnIndex(NEXT_VISIT_DATE));
                    clientDetails.put(key, value);

                    key = NEXT_GROWTH_MONITORING_DATE;
                    value = cursor.getString(cursor.getColumnIndex(NEXT_GROWTH_MONITORING_DATE));
                    clientDetails.put(key, value);

                    key = DIARREA;
                    value = cursor.getString(cursor.getColumnIndex(DIARREA));
                    clientDetails.put(key, value);

                    key = MALARIA;
                    value = cursor.getString(cursor.getColumnIndex(MALARIA));
                    clientDetails.put(key, value);

                    key = COLD;
                    value = cursor.getString(cursor.getColumnIndex(COLD));
                    clientDetails.put(key, value);

                    key = PNEUMONIA;
                    value = cursor.getString(cursor.getColumnIndex(PNEUMONIA));
                    clientDetails.put(key, value);

                    key = TUBERCULOSIS;
                    value = cursor.getString(cursor.getColumnIndex(TUBERCULOSIS));
                    clientDetails.put(key, value);

                    key = DENGUE;
                    value = cursor.getString(cursor.getColumnIndex(DENGUE));
                    clientDetails.put(key, value);

                    clientDetails.put(DATE, cursor.getString(cursor.getColumnIndex(DATE)));

                } while (cursor.moveToNext());
            }
            return clientDetails;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return clientDetails;
    }

    public Map<String, String> getLatestResult(String baseEntityId) {
        return getLatestResults(baseEntityId, true, null);
    }

    private Cursor getLatestResultsCursor(String baseEntityId, Long baseline, boolean singleResult, boolean orderResults, boolean afterBaseline) {
        Cursor cursor;
        SQLiteDatabase db = getReadableDatabase();
        String baselineFilter = "";
        String orderByClause = "";
        String groupByClause = "";
        if (baseline != null) {
            if (afterBaseline) {
                baselineFilter = "AND " + CREATED_AT + ">" + baseline + "";
            } else {
                baselineFilter = "AND " + CREATED_AT + "<=" + baseline + "";
            }
        }
        if (!singleResult) {
            groupByClause = " GROUP BY " + TYPE;
        }
        if (orderResults) {
            orderByClause = " ORDER BY " + DATE + " DESC";
        }
        String query =
                "SELECT max(" + DATE + "||" + CREATED_AT + ")," + DATE + "," + TYPE + "," + RESULT1 + "," + VALUE1 + "," + RESULT2 + "," + VALUE2 + ","
                        + WEIGHT + "," + HEIGHT + "," + HAEMOGLOBIN + "," + HEIGHT_AGE_STATUS + "," + WEIGHT_HEIGHT_STATUS
                        +"," + NEXT_VISIT_DATE + "," + NEXT_GROWTH_MONITORING_DATE + "," + DIARREA + "," + MALARIA
                        + "," + COLD + "," + PNEUMONIA + "," + TUBERCULOSIS + "," + DENGUE
                        + "," + DEWORMING + "," + DEWORMING_DATE
                        + " FROM " + TABLE_NAME + " WHERE " + BASE_ENTITY_ID + "  = '" + baseEntityId + "' "
                        + baselineFilter
                        + groupByClause + orderByClause;

        cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Map<String, Result> getLatestResultsAll(String baseEntityId, Long baseline, boolean afterBaseline) {
        Cursor cursor = null;
        Result result;
        Map<String, Result> clientDetails = new LinkedHashMap<>();
        try {
            cursor = getLatestResultsCursor(baseEntityId, baseline, false, true, afterBaseline);
            if (cursor != null && cursor.moveToFirst()) {
                do {


                    String key = cursor.getString(cursor.getColumnIndex(RESULT1));
                    String key2 = cursor.getString(cursor.getColumnIndex(RESULT2));

                    result = new Result();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Constants.KEY.DATE)));
                    result.setDate(calendar.getTime());
                    result.setResult1(key);
                    result.setValue1(cursor.getString(cursor.getColumnIndex(VALUE1)));

                    if (key2 != null && !key2.isEmpty() && key2.equals(Constants.RESULT.ERROR_CODE)) {

                        result.setResult2(key2);
                        result.setValue2(cursor.getString(cursor.getColumnIndex(VALUE2)));
                    }

                    clientDetails.put(key, result);

                    if (key2 != null && !key2.isEmpty() && !key2.equals(Constants.RESULT.ERROR_CODE)) {

                        result = new Result();
                        result.setDate(calendar.getTime());
                        result.setResult1(key2);
                        result.setValue1(cursor.getString(cursor.getColumnIndex(VALUE2)));
                        clientDetails.put(key2, result);

                    }

                } while (cursor.moveToNext());
            }
            return clientDetails;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return clientDetails;
    }
}
