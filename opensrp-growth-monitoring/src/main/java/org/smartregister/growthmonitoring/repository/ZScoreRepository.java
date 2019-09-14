package org.smartregister.growthmonitoring.repository;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.opensrp.api.constants.Gender;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores child z-scores obtained from:
 * - http://www.who.int/childgrowth/standards/wfa_boys_0_5_zscores.txt
 * - http://www.who.int/childgrowth/standards/wfa_girls_0_5_zscores.txt
 * <p/>
 * Created by Jason Rogena - jrogena@ona.io on 29/05/2017.
 */

public class ZScoreRepository extends BaseRepository {
    private static final String TAG = ZScoreRepository.class.getName();
    public static final String TABLE_NAME_WEIGHT_FOR_AGE = "z_scores_weight_for_age";
    public static final String TABLE_NAME_HEIGHT_FOR_AGE = "z_scores_height_for_age";
    public static final String TABLE_NAME_WEIGHT_FOR_HEIGHT = "z_scores_weight_for_height";

    public static final String COLUMN_SEX = "sex";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_L = "l";
    public static final String COLUMN_M = "m";
    public static final String COLUMN_S = "s";
    public static final String COLUMN_SD3NEG = "sd3neg";
    public static final String COLUMN_SD2NEG = "sd2neg";
    public static final String COLUMN_SD1NEG = "sd1neg";
    public static final String COLUMN_SD0 = "sd0";
    public static final String COLUMN_SD1 = "sd1";
    public static final String COLUMN_SD2 = "sd2";
    public static final String COLUMN_SD3 = "sd3";

    private static final Map<String, String> CSV_HEADING_SQL_COLUMN_MAP;

    static {
        CSV_HEADING_SQL_COLUMN_MAP = new HashMap<>();
        CSV_HEADING_SQL_COLUMN_MAP.put("Month", ZScoreRepository.COLUMN_MONTH);
        CSV_HEADING_SQL_COLUMN_MAP.put("L", ZScoreRepository.COLUMN_L);
        CSV_HEADING_SQL_COLUMN_MAP.put("M", ZScoreRepository.COLUMN_M);
        CSV_HEADING_SQL_COLUMN_MAP.put("S", ZScoreRepository.COLUMN_S);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD3neg", ZScoreRepository.COLUMN_SD3NEG);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD2neg", ZScoreRepository.COLUMN_SD2NEG);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD1neg", ZScoreRepository.COLUMN_SD1NEG);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD0", ZScoreRepository.COLUMN_SD0);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD1", ZScoreRepository.COLUMN_SD1);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD2", ZScoreRepository.COLUMN_SD2);
        CSV_HEADING_SQL_COLUMN_MAP.put("SD3", ZScoreRepository.COLUMN_SD3);
    }

    private static final Map<String, String> CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT;

    static {
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT = new HashMap<>();
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.put("Height", ZScoreRepository.COLUMN_HEIGHT);
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.put("L", ZScoreRepository.COLUMN_L);
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.put("M", ZScoreRepository.COLUMN_M);
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.put("S", ZScoreRepository.COLUMN_S);
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.put("SD3neg", ZScoreRepository.COLUMN_SD3NEG);
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.put("SD2neg", ZScoreRepository.COLUMN_SD2NEG);
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.put("SD1neg", ZScoreRepository.COLUMN_SD1NEG);
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.put("SD0", ZScoreRepository.COLUMN_SD0);
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.put("SD1", ZScoreRepository.COLUMN_SD1);
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.put("SD2", ZScoreRepository.COLUMN_SD2);
        CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.put("SD3", ZScoreRepository.COLUMN_SD3);
    }

    private static final String CREATE_TABLE_QUERY_WEIGHT_FOR_AGE = "CREATE TABLE " + TABLE_NAME_WEIGHT_FOR_AGE +
            " (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SEX + " VARCHAR NOT NULL, " +
            COLUMN_MONTH + " INTEGER NOT NULL, " +
            COLUMN_L + " REAL NOT NULL, " +
            COLUMN_M + " REAL NOT NULL, " +
            COLUMN_S + " REAL NOT NULL, " +
            COLUMN_SD3NEG + " REAL NOT NULL, " +
            COLUMN_SD2NEG + " REAL NOT NULL, " +
            COLUMN_SD1NEG + " REAL NOT NULL, " +
            COLUMN_SD0 + " REAL NOT NULL, " +
            COLUMN_SD1 + " REAL NOT NULL, " +
            COLUMN_SD2 + " REAL NOT NULL, " +
            COLUMN_SD3 + " REAL NOT NULL, " +
            "UNIQUE(" + COLUMN_SEX + ", " + COLUMN_MONTH + ") ON CONFLICT REPLACE)";

    private static final String CREATE_INDEX_SEX_QUERY_WEIGHT_FOR_AGE = "CREATE INDEX " + COLUMN_SEX + "weight_age_index ON " + TABLE_NAME_WEIGHT_FOR_AGE + "(" + COLUMN_SEX + " COLLATE NOCASE);";
    private static final String CREATE_INDEX_MONTH_QUERY_WEIGHT_FOR_AGE = "CREATE INDEX " + COLUMN_MONTH + "weight_age_index ON " + TABLE_NAME_WEIGHT_FOR_AGE + "(" + COLUMN_MONTH + " COLLATE NOCASE);";

    private static final String CREATE_TABLE_QUERY_HEIGHT_FOR_AGE = "CREATE TABLE " + TABLE_NAME_HEIGHT_FOR_AGE +
            " (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SEX + " VARCHAR NOT NULL, " +
            COLUMN_MONTH + " INTEGER NOT NULL, " +
            COLUMN_L + " REAL NOT NULL, " +
            COLUMN_M + " REAL NOT NULL, " +
            COLUMN_S + " REAL NOT NULL, " +
            COLUMN_SD3NEG + " REAL NOT NULL, " +
            COLUMN_SD2NEG + " REAL NOT NULL, " +
            COLUMN_SD1NEG + " REAL NOT NULL, " +
            COLUMN_SD0 + " REAL NOT NULL, " +
            COLUMN_SD1 + " REAL NOT NULL, " +
            COLUMN_SD2 + " REAL NOT NULL, " +
            COLUMN_SD3 + " REAL NOT NULL, " +
            "UNIQUE(" + COLUMN_SEX + ", " + COLUMN_MONTH + ") ON CONFLICT REPLACE)";

    private static final String CREATE_INDEX_SEX_QUERY_HEIGHT_FOR_AGE = "CREATE INDEX " + COLUMN_SEX + "height_age_index ON " + TABLE_NAME_HEIGHT_FOR_AGE + "(" + COLUMN_SEX + " COLLATE NOCASE);";
    private static final String CREATE_INDEX_MONTH_QUERY_HEIGHT_FOR_AGE = "CREATE INDEX " + COLUMN_MONTH + "height_age_index ON " + TABLE_NAME_HEIGHT_FOR_AGE + "(" + COLUMN_MONTH + " COLLATE NOCASE);";

    private static final String CREATE_TABLE_QUERY_WEIGHT_FOR_HEIGHT = "CREATE TABLE " + TABLE_NAME_WEIGHT_FOR_HEIGHT +
            " (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SEX + " VARCHAR NOT NULL, " +
            COLUMN_HEIGHT + " REAL NOT NULL, " +
            COLUMN_L + " REAL NOT NULL, " +
            COLUMN_M + " REAL NOT NULL, " +
            COLUMN_S + " REAL NOT NULL, " +
            COLUMN_SD3NEG + " REAL NOT NULL, " +
            COLUMN_SD2NEG + " REAL NOT NULL, " +
            COLUMN_SD1NEG + " REAL NOT NULL, " +
            COLUMN_SD0 + " REAL NOT NULL, " +
            COLUMN_SD1 + " REAL NOT NULL, " +
            COLUMN_SD2 + " REAL NOT NULL, " +
            COLUMN_SD3 + " REAL NOT NULL, " +
            "UNIQUE(" + COLUMN_SEX + ", " + COLUMN_HEIGHT + ") ON CONFLICT REPLACE)";

    private static final String CREATE_INDEX_SEX_QUERY_WEIGHT_FOR_HEIGHT = "CREATE INDEX " + COLUMN_SEX + "weight_height_index ON " + TABLE_NAME_WEIGHT_FOR_HEIGHT + "(" + COLUMN_SEX + " COLLATE NOCASE);";
    private static final String CREATE_INDEX_MONTH_QUERY_WEIGHT_FOR_HEIGHT = "CREATE INDEX " + COLUMN_HEIGHT + "weight_height_index ON " + TABLE_NAME_WEIGHT_FOR_HEIGHT + "(" + COLUMN_HEIGHT + " COLLATE NOCASE);";


    public ZScoreRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY_WEIGHT_FOR_AGE);
        database.execSQL(CREATE_INDEX_SEX_QUERY_WEIGHT_FOR_AGE);
        database.execSQL(CREATE_INDEX_MONTH_QUERY_WEIGHT_FOR_AGE);

        database.execSQL(CREATE_TABLE_QUERY_HEIGHT_FOR_AGE);
        database.execSQL(CREATE_INDEX_SEX_QUERY_HEIGHT_FOR_AGE);
        database.execSQL(CREATE_INDEX_MONTH_QUERY_HEIGHT_FOR_AGE);

        database.execSQL(CREATE_TABLE_QUERY_WEIGHT_FOR_HEIGHT);
        database.execSQL(CREATE_INDEX_SEX_QUERY_WEIGHT_FOR_HEIGHT);
        database.execSQL(CREATE_INDEX_MONTH_QUERY_WEIGHT_FOR_HEIGHT);
    }

    /**
     * @param query
     * @return
     */
    public boolean runRawQuery(String query) {
        try {
            getRepository().getWritableDatabase().execSQL(query);
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return false;
    }

    public List<ZScore> findWeightForAgeByGender(Gender gender) {
        List<ZScore> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase database = getRepository().getReadableDatabase();
            cursor = database.query(TABLE_NAME_WEIGHT_FOR_AGE,
                    null,
                    COLUMN_SEX + " = ? " + COLLATE_NOCASE,
                    new String[]{gender.name()}, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result.add(new ZScore(gender,
                            cursor.getInt(cursor.getColumnIndex(COLUMN_MONTH)),
                            0.0,
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_L)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_M)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_S)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD3NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD2NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD1NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD0)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD1)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD2)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD3))));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) cursor.close();
        }

        return result;
    }

    public List<ZScore> findHeightForAgeByGender(Gender gender) {
        List<ZScore> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase database = getRepository().getReadableDatabase();
            cursor = database.query(TABLE_NAME_HEIGHT_FOR_AGE,
                    null,
                    COLUMN_SEX + " = ? " + COLLATE_NOCASE,
                    new String[]{gender.name()}, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result.add(new ZScore(gender,
                            cursor.getInt(cursor.getColumnIndex(COLUMN_MONTH)),
                            0.0,
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_L)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_M)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_S)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD3NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD2NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD1NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD0)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD1)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD2)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD3))));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) cursor.close();
        }

        return result;
    }

    public List<ZScore> findWeightForHeightByGender(Gender gender) {
        List<ZScore> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase database = getRepository().getReadableDatabase();
            cursor = database.query(TABLE_NAME_WEIGHT_FOR_HEIGHT,
                    null,
                    COLUMN_SEX + " = ? " + COLLATE_NOCASE,
                    new String[]{gender.name()}, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result.add(new ZScore(gender,
                            0,
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_HEIGHT)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_L)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_M)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_S)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD3NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD2NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD1NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD0)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD1)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD2)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD3))));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) cursor.close();
        }

        return result;
    }


    public void dumpCsvHeightForAge(SQLiteDatabase database, Gender gender, boolean force, Context context) {
        try {
            /*List<ZScore> existingScores = GrowthMonitoringLibrary.getInstance().zScoreRepository().findByGender(gender);
            if (force
                    || existingScores.size() == 0) {*/
            String filename = null;
            if (gender.equals(Gender.FEMALE)) {
                filename = GrowthMonitoringLibrary.getInstance().getConfig().getFemaleHeightForAgeZScoreFile();
            } else if (gender.equals(Gender.MALE)) {
                filename = GrowthMonitoringLibrary.getInstance().getConfig().getMaleHeightForAgeZScoreFile();
            }

            if (filename != null) {
                CSVParser csvParser = CSVParser.parse(Utils.readAssetContents(context, filename),
                        CSVFormat.newFormat('\t'));

                HashMap<Integer, Boolean> columnStatus = new HashMap<>();
                String query = "INSERT INTO `" + ZScoreRepository.TABLE_NAME_HEIGHT_FOR_AGE + "` ( `" + ZScoreRepository.COLUMN_SEX + "`";
                for (CSVRecord record : csvParser) {
                    if (csvParser.getCurrentLineNumber() == 2) {// The second line
                        query = query + ")\n VALUES (\"" + gender.name() + "\"";
                    } else if (csvParser.getCurrentLineNumber() > 2) {
                        query = query + "),\n (\"" + gender.name() + "\"";
                    }

                    for (int columnIndex = 0; columnIndex < record.size(); columnIndex++) {
                        String curColumn = record.get(columnIndex);
                        if (csvParser.getCurrentLineNumber() == 1) {
                            if (CSV_HEADING_SQL_COLUMN_MAP.containsKey(curColumn)) {
                                columnStatus.put(columnIndex, true);
                                query = query + ", `" + CSV_HEADING_SQL_COLUMN_MAP.get(curColumn) + "`";
                            } else {
                                columnStatus.put(columnIndex, false);
                            }
                        } else {
                            if (columnStatus.get(columnIndex)) {
                                query = query + ", \"" + curColumn + "\"";
                            }
                        }
                    }
                }
                query = query + ");";

                database.rawExecSQL(query);
            }
            // }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public void dumpCsvWeightForAge(SQLiteDatabase database, Gender gender, boolean force, Context context) {
        try {
            /*List<ZScore> existingScores = GrowthMonitoringLibrary.getInstance().zScoreRepository().findByGender(gender);
            if (force
                    || existingScores.size() == 0) {*/
                String filename = null;
                if (gender.equals(Gender.FEMALE)) {
                    filename = GrowthMonitoringLibrary.getInstance().getConfig().getFemaleWeightForAgeZScoreFile();
                } else if (gender.equals(Gender.MALE)) {
                    filename = GrowthMonitoringLibrary.getInstance().getConfig().getMaleWeightForAgeZScoreFile();
                }

                if (filename != null) {
                    CSVParser csvParser = CSVParser.parse(Utils.readAssetContents(context, filename),
                            CSVFormat.newFormat('\t'));

                    HashMap<Integer, Boolean> columnStatus = new HashMap<>();
                    String query = "INSERT INTO `" + ZScoreRepository.TABLE_NAME_WEIGHT_FOR_AGE + "` ( `" + ZScoreRepository.COLUMN_SEX + "`";
                    for (CSVRecord record : csvParser) {
                        if (csvParser.getCurrentLineNumber() == 2) {// The second line
                            query = query + ")\n VALUES (\"" + gender.name() + "\"";
                        } else if (csvParser.getCurrentLineNumber() > 2) {
                            query = query + "),\n (\"" + gender.name() + "\"";
                        }

                        for (int columnIndex = 0; columnIndex < record.size(); columnIndex++) {
                            String curColumn = record.get(columnIndex);
                            if (csvParser.getCurrentLineNumber() == 1) {
                                if (CSV_HEADING_SQL_COLUMN_MAP.containsKey(curColumn)) {
                                    columnStatus.put(columnIndex, true);
                                    query = query + ", `" + CSV_HEADING_SQL_COLUMN_MAP.get(curColumn) + "`";
                                } else {
                                    columnStatus.put(columnIndex, false);
                                }
                            } else {
                                if (columnStatus.get(columnIndex)) {
                                    query = query + ", \"" + curColumn + "\"";
                                }
                            }
                        }
                    }
                    query = query + ");";

                    database.rawExecSQL(query);
                }
           // }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public void dumpCsvWeightForHeight(SQLiteDatabase database, Gender gender, boolean force, Context context) {
        try {
            /*List<ZScore> existingScores = GrowthMonitoringLibrary.getInstance().zScoreRepository().findByGender(gender);
            if (force
                    || existingScores.size() == 0) {*/
            String filename = null;
            if (gender.equals(Gender.FEMALE)) {
                filename = GrowthMonitoringLibrary.getInstance().getConfig().getFemaleWeightForHeightZScoreFile();
            } else if (gender.equals(Gender.MALE)) {
                filename = GrowthMonitoringLibrary.getInstance().getConfig().getMaleWeightForHeightZScoreFile();
            }

            if (filename != null) {
                CSVParser csvParser = CSVParser.parse(Utils.readAssetContents(context, filename),
                        CSVFormat.newFormat('\t'));

                HashMap<Integer, Boolean> columnStatus = new HashMap<>();
                String query = "INSERT INTO `" + ZScoreRepository.TABLE_NAME_WEIGHT_FOR_HEIGHT + "` ( `" + ZScoreRepository.COLUMN_SEX + "`";
                for (CSVRecord record : csvParser) {
                    if (csvParser.getCurrentLineNumber() == 2) {// The second line
                        query = query + ")\n VALUES (\"" + gender.name() + "\"";
                    } else if (csvParser.getCurrentLineNumber() > 2) {
                        query = query + "),\n (\"" + gender.name() + "\"";
                    }

                    for (int columnIndex = 0; columnIndex < record.size(); columnIndex++) {
                        String curColumn = record.get(columnIndex);
                        if (csvParser.getCurrentLineNumber() == 1) {
                            if (CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.containsKey(curColumn)) {
                                columnStatus.put(columnIndex, true);
                                query = query + ", `" + CSV_HEADING_SQL_COLUMN_MAP_WEIGHT_HEIGHT.get(curColumn) + "`";
                            } else {
                                columnStatus.put(columnIndex, false);
                            }
                        } else {
                            if (columnStatus.get(columnIndex)) {
                                query = query + ", \"" + curColumn + "\"";
                            }
                        }
                    }
                }
                query = query + ");";

                database.rawExecSQL(query);
            }
            // }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
