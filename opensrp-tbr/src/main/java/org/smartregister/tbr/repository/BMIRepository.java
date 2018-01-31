package org.smartregister.tbr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.tbr.model.BMIRecord;
import org.smartregister.tbr.model.BMIRecordWrapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BMIRepository extends BaseRepository {

    private static final String TAG = BMIRepository.class.getCanonicalName();

    public static final String TABLE_NAME = "ec_patient_bmi";
    public static final String ID = "_id";
    public static final String HEIGHT = "height";
    public static final String WEIGHT = "weight";
    public static final String BMI = "bmi";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            HEIGHT + "  VARCHAR NULL, " +
            WEIGHT + "  VARCHAR NOT NULL," +
            BASE_ENTITY_ID + "  VARCHAR NOT NULL," +
            BMI + "  VARCHAR NULL, " +
            CREATED_AT + " INTEGER NOT NULL, " +
            UPDATED_AT + " INTEGER NULL )";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + TABLE_NAME + "_" + BASE_ENTITY_ID +
            "_index ON " + TABLE_NAME + "(" + BASE_ENTITY_ID + " COLLATE NOCASE);";

    public BMIRepository(Repository repository) {
        super(repository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    private void saveRecord(BMIRecord bMIRecord) {
        if (bMIRecord == null)
            return;
        else if (bMIRecord.getUpdatedAt() == null) {
            bMIRecord.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
        }
        if (bMIRecord.getId() == null) {
            Long existingId = getExistingBMIRecordId(bMIRecord);
            if (existingId != null) {
                bMIRecord.setId(existingId);
                bMIRecord.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                update(bMIRecord);
            } else {
                getWritableDatabase().insert(TABLE_NAME, null, createValuesFor(bMIRecord));
            }
        } else {
            update(bMIRecord);
        }
    }

    private void update(BMIRecord bmiRecord) {
        ContentValues contentValues = createValuesFor(bmiRecord);
        getWritableDatabase().update(TABLE_NAME, contentValues, ID + " = ?", new String[]{bmiRecord.getId().toString()});
    }

    private ContentValues createValuesFor(BMIRecord bMIRecord) {
        ContentValues values = new ContentValues();
        values.put(HEIGHT, bMIRecord.getHeight());
        values.put(WEIGHT, bMIRecord.getWeight());
        values.put(BMI, bMIRecord.getBmi());
        values.put(BASE_ENTITY_ID, bMIRecord.getBaseEntityId());
        values.put(CREATED_AT, bMIRecord.getCreatedAt());
        values.put(UPDATED_AT, bMIRecord.getUpdatedAt());
        return values;
    }

    private Long getExistingBMIRecordId(BMIRecord bmiRecord) {
        String selection = null;
        String[] selectionArgs = {bmiRecord.getBaseEntityId(), bmiRecord.getCreatedAt()};
        Long id = null;
        if (StringUtils.isNotBlank(bmiRecord.getBaseEntityId()) && StringUtils.isNotBlank(bmiRecord.getBaseEntityId())) {
            selection = BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE + " AND CREATED_AT = ? " + COLLATE_NOCASE;
        }
        Cursor cursor = getReadableDatabase().query(TABLE_NAME, new String[]{ID}, selection, selectionArgs, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }

    private List<BMIRecord> getBMIRecordsByBMIRecordID(String bMIRecordBaseEntityId) {
        Cursor cursor = null;
        List<BMIRecord> bMIRecords = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query =
                    "SELECT * FROM " + BMIRepository.TABLE_NAME + " WHERE " + BMIRepository.BASE_ENTITY_ID + " = '" + bMIRecordBaseEntityId + "' AND " + BMIRepository.WEIGHT + " > 0";
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                BMIRecord bMIRecord;
                do {
                    bMIRecord = new BMIRecord();
                    bMIRecord.setId(cursor.getLong(cursor.getColumnIndex(BMIRepository.ID)));
                    bMIRecord.setHeight(cursor.getFloat(cursor.getColumnIndex(BMIRepository.HEIGHT)));
                    bMIRecord.setBmi(cursor.getFloat(cursor.getColumnIndex(BMIRepository.BMI)));
                    bMIRecord.setWeight(cursor.getFloat(cursor.getColumnIndex(BMIRepository.WEIGHT)));
                    bMIRecord.setBaseEntityId(cursor.getString(cursor.getColumnIndex(BMIRepository.BASE_ENTITY_ID)));
                    bMIRecord.setCreatedAt(cursor.getString(cursor.getColumnIndex(BMIRepository.CREATED_AT)));
                    bMIRecord.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(BMIRepository.UPDATED_AT)));

                    bMIRecords.add(bMIRecord);
                } while (cursor.moveToNext());
            }
            return bMIRecords;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bMIRecords;

    }

    public Float fetchBMIHeightByBaseEntityId(String baseEntityId) {

        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query =
                    "SELECT height FROM " + BMIRepository.TABLE_NAME + " WHERE " + BMIRepository.BASE_ENTITY_ID + " = '" + baseEntityId + "' AND height IS NOT NULL AND height > 0 ORDER BY " + BMIRepository.CREATED_AT + " DESC";
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getFloat(cursor.getColumnIndex(BMIRepository.HEIGHT));
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

    public void saveBMIRecord(String baseEntityId, Float weight, Float height, Float bmi, String createdAt) {

        Float workingHeight = height != null && height > 0 ? height : fetchBMIHeightByBaseEntityId(baseEntityId);
        Float workingBmi = bmi;
        BMIRecord bmiRecord = new BMIRecord();
        bmiRecord.setBaseEntityId(baseEntityId);
        bmiRecord.setHeight(height);
        bmiRecord.setWeight(weight);
        bmiRecord.setCreatedAt(createdAt);
        if (workingHeight != null) {
            if (bmi == null) {
                workingBmi = calculateBMI(weight, workingHeight);
            }

            bmiRecord.setBmi(workingBmi);
        }

        saveRecord(bmiRecord);
    }

    public Float calculateBMI(Float weight, Float height) {
        Float bmi = null;
        if (height != null && height > 0 && weight != null && weight > 0) {
            float heightValue = height;
            float weightValue = weight;

            bmi = weightValue / (heightValue * heightValue);
        }
        return bmi;
    }

    public BMIRecordWrapper getBMIRecords(String baseEntityId) {
        Float height = fetchBMIHeightByBaseEntityId(baseEntityId);
        String type = height != null ? BMIRecordWrapper.BMIRecordsTYPE.BMIS : BMIRecordWrapper.BMIRecordsTYPE.WEIGHTS;
        return new BMIRecordWrapper(type, getBMIRecordsByBMIRecordID(baseEntityId));

    }
}