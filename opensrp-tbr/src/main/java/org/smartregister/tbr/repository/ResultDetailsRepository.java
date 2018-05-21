package org.smartregister.tbr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.helper.DBQueryHelper;
import org.smartregister.configurableviews.model.TestResultsConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.tbr.model.Register;
import org.smartregister.tbr.model.RegisterCount;
import org.smartregister.tbr.util.Constants;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResultDetailsRepository extends BaseRepository {
    private static final String TAG = ResultDetailsRepository.class.getCanonicalName();

    public static final String TABLE_NAME = "result_details";
    public static final String FORMSUBMISSION_ID = "formSubmissionId";
    private static final String KEY_COLUMN = "key";
    private static final String VALUE_COLUMN = "value";
    private static final String RESULT_DATE_COLUMN = "event_date";

    private static final String CREATE_TABLE_SQL = "CREATE VIRTUAL TABLE " + TABLE_NAME + " USING FTS4 (" +
            FORMSUBMISSION_ID + " VARCHAR  NULL, " +
            KEY_COLUMN + "  VARCHAR  NULL, " +
            VALUE_COLUMN + "  VARCHAR  NULL, " +
            RESULT_DATE_COLUMN + "  VARCHAR  NULL )";


    public ResultDetailsRepository(Repository repository) {
        super(repository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
    }

    public void add(String formSubmissionId, String key, String value, Long timestamp) {
        SQLiteDatabase database = getWritableDatabase();
        Boolean exists = getIdForDetailsIfExists(formSubmissionId, key, value);
        if (exists == null) { // Value has not changed, no need to update
            return;
        }

        ContentValues values = new ContentValues();
        values.put(FORMSUBMISSION_ID, formSubmissionId);
        values.put(KEY_COLUMN, key);
        values.put(VALUE_COLUMN, value);
        values.put(RESULT_DATE_COLUMN, timestamp);

        if (exists) {
            database.update(TABLE_NAME, values,
                    FORMSUBMISSION_ID + " = ? AND " + KEY_COLUMN + " MATCH ? ",
                    new String[]{formSubmissionId, key});
        } else {
            database.insert(TABLE_NAME, null, values);
        }
    }

    private Boolean getIdForDetailsIfExists(String formSubmissionId, String key, String value) {
        Cursor mCursor = null;
        try {
            SQLiteDatabase db = getWritableDatabase();
            String query = "SELECT " + VALUE_COLUMN + " FROM " + TABLE_NAME + " WHERE "
                    + FORMSUBMISSION_ID + " = '" + formSubmissionId + "' AND " + KEY_COLUMN + " "
                    + "MATCH '" + key + "' ";
            mCursor = db.rawQuery(query, null);
            if (mCursor != null && mCursor.moveToFirst()) {
                if (value != null) {
                    String currentValue = mCursor.getString(mCursor.getColumnIndex(VALUE_COLUMN));
                    if (value.equals(currentValue)) { // Value has not changed, no need to update
                        return null;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return false;
    }

    public void saveClientDetails(String formSubmissionId, Map<String, String> values, Long timestamp) {
        for (String key : values.keySet()) {
            String value = values.get(key);
            add(formSubmissionId, key, value, timestamp);
        }
    }

    public Map<String, String> getFormResultDetails(String formSubmissionId) {
        Cursor cursor = null;
        Map<String, String> clientDetails = new LinkedHashMap<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query =
                    "SELECT * FROM " + ResultDetailsRepository.TABLE_NAME + " WHERE " + ResultsRepository.FORMSUBMISSION_ID + "= '" + formSubmissionId + "'";
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String key = cursor.getString(cursor.getColumnIndex(KEY_COLUMN));
                    String value = cursor.getString(cursor.getColumnIndex(VALUE_COLUMN));
                    clientDetails.put(key, value);
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

    public RegisterCount getRegisterCountByType(String type) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String suffix = "";
            if (type.equals(Register.PRESUMPTIVE_PATIENTS)) {
                suffix = DBQueryHelper.getPresumptivePatientRegisterCondition();
            } else if (type.equals(Register.POSITIVE_PATIENTS)) {
                suffix = DBQueryHelper.getPositivePatientRegisterCondition();

            } else if (type.equals(Register.IN_TREATMENT_PATIENTS)) {

                suffix = DBQueryHelper.getIntreatmentPatientRegisterCondition();
            }

            String jsonString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(Constants.CONFIGURATION.TEST_RESULTS);
            ViewConfiguration testResultsConfig = jsonString == null ? null : TbrApplication.getJsonSpecHelper().getConfigurableView(jsonString);
            Integer days = ((TestResultsConfiguration)testResultsConfig.getMetadata()).getFollowupOverduePeriod();

            String query = "SELECT " +
                    "    sum(case when " + suffix + " then 1 else 0 end) " + RegisterCount.REGISTER_COUNT + "," +
                    "    sum(case when " + suffix + " and date(next_visit_date, '+"+ days +" day'"+") < date('now') then 1 else 0 end) " + RegisterCount.OVERDUE_COUNT +
                    " FROM ec_patient";
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                return new RegisterCount(cursor.getInt(cursor.getColumnIndex(RegisterCount.REGISTER_COUNT)), cursor.getInt(cursor.getColumnIndex(RegisterCount.OVERDUE_COUNT)));

            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
