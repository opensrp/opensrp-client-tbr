package org.smartregister.tbr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.db.Column;
import org.smartregister.domain.db.ColumnAttribute;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfigurableViewsRepository extends BaseRepository {
    private static final String TAG = ConfigurableViewsRepository.class.getCanonicalName();
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String TABLE_NAME = "configurable_views";
    private static final String ID = "view_id";
    private static final String IDENTIFIER = "identifier";
    private static final String SERVER_VERSION = "serverVersion";
    private static final String JSON = "json";
    private static final String DATE_CREATED = "date_created";
    private static final String DATE_UPDATED = "date_updated";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            IDENTIFIER + "  VARCHAR NOT NULL,status VARCHAR NULL, " +
            SERVER_VERSION + "  Integer NULL," +
            JSON + "  VARCHAR NULL," +
            DATE_CREATED + "  DATETIME NULL," +
            DATE_UPDATED + "  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP )";

    private static final String INDEX_ID = "CREATE INDEX " + TABLE_NAME + "_" + ID +
            "_index ON " + TABLE_NAME + "(" + ID + " COLLATE NOCASE);";

    private static final String INDEX_IDENTIFIER = "CREATE INDEX " + TABLE_NAME + "_" + IDENTIFIER +
            "_index ON " + TABLE_NAME + "(" + IDENTIFIER + " COLLATE NOCASE);";

    private static final String INDEX_SERVER_VERSION = "CREATE INDEX " + TABLE_NAME + "_" + SERVER_VERSION +
            "_index ON " + TABLE_NAME + "(" + SERVER_VERSION + " COLLATE NOCASE);";


    private enum columns implements Column {
        id(ColumnAttribute.Type.longnum, true, true),
        identifier(ColumnAttribute.Type.text, false, true),
        json(ColumnAttribute.Type.text, false, false),
        serverVersion(ColumnAttribute.Type.longnum, false, true),
        dateCreated(ColumnAttribute.Type.date, false, false),
        dateUpdated(ColumnAttribute.Type.date, false, false);

        columns(ColumnAttribute.Type type, boolean pk, boolean index) {
            this.column = new ColumnAttribute(type, pk, index);
        }

        private ColumnAttribute column;

        public ColumnAttribute column() {
            return column;
        }
    }

    public ConfigurableViewsRepository(Repository repository) {
        super(repository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_ID);
        database.execSQL(INDEX_IDENTIFIER);
        database.execSQL(INDEX_SERVER_VERSION);
    }

    public void saveConfigurableViews(JSONArray jsonArray) {
        try {
            getWritableDatabase().beginTransaction();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String identifier = jsonObject.getString("identifier");

                ContentValues values = new ContentValues();
                values.put(columns.serverVersion.name(), jsonObject.getLong("serverVersion"));
                values.put(columns.json.name(), jsonObject.toString());
                if (configurableViewExists(identifier)) {
                    values.put(columns.dateUpdated.name(), dateFormat.format(new Date()));
                    getWritableDatabase().update(TABLE_NAME, values, IDENTIFIER + " = ?", new String[]{identifier});
                } else {
                    values.put(columns.identifier.name(), jsonObject.getString("identifier"));
                    getWritableDatabase().insert(TABLE_NAME, null, values);
                }
            }
            getWritableDatabase().setTransactionSuccessful();
        } catch (JSONException e) {
            Log.e(TAG, "error saving Configurable view");
        } finally {
            getWritableDatabase().endTransaction();
        }
    }

    private boolean configurableViewExists(String identifier) {
        Cursor c = getReadableDatabase().rawQuery("Select count(*) from " + TABLE_NAME + " Where " +
                        IDENTIFIER + " = ? ",
                new String[]{identifier});
        if (c.getCount() == 0) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }

    }

}
