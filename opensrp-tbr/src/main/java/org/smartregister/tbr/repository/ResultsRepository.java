package org.smartregister.tbr.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.tbr.model.Result;

import java.util.Calendar;

public class ResultsRepository extends BaseRepository {

    public static final String TABLE_NAME = "results";
    private static final String ID = "_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String TYPE = "type";
    public static final String RESULT1 = "result1";
    public static final String VALUE1 = "value1";
    public static final String RESULT2 = "result2";
    public static final String VALUE2 = "value2";
    public static final String FORMSUBMISSION_ID = "formSubmissionId";
    public static final String EVENT_ID = "event_id";
    public static final String DATE = "date";
    public static final String UPDATED_AT_COLUMN = "updated_at";
    public static final String ANMID = "anmid";
    public static final String LOCATIONID = "location_id";
    public static final String SYNC_STATUS = "sync_status";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            BASE_ENTITY_ID + "  VARCHAR NOT NULL, " +
            TYPE + "  VARCHAR NOT NULL, " +
            RESULT1 + "  VARCHAR NOT NULL, " +
            VALUE1 + "  VARCHAR NOT NULL," +
            RESULT2 + "  VARCHAR  NULL, " +
            VALUE2 + "  VARCHAR NULL," +
            FORMSUBMISSION_ID + "  VARCHAR NOT NULL, " +
            EVENT_ID + "  VARCHAR  NULL, " +
            DATE + "  DATETIME NOT NULL, " +
            ANMID + "  VARCHAR NOT NULL, " +
            LOCATIONID + "  VARCHAR NOT NULL, " +
            SYNC_STATUS + "  VARCHAR NOT NULL, " +
            UPDATED_AT_COLUMN + " INTEGER NULL, " +
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
        getWritableDatabase().update(TABLE_NAME, createValuesFor(result), ID + " = ?", new String[]{result.getId().toString()});
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
        values.put(FORMSUBMISSION_ID, result.getFormSubmissionId());
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

}
