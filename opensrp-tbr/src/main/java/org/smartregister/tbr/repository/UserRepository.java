package org.smartregister.tbr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.model.User;
import org.smartregister.tbr.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserRepository extends BaseRepository {

    private static final String TAG = UserRepository.class.getCanonicalName();

    public static final String TABLE_NAME = "ec_users";
    public static final String ID = "_id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DATE = "date";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            USERNAME + "  VARCHAR NOT NULL, " +
            PASSWORD + "  VARCHAR NOT NULL, " +
            DATE + "  DATETIME NOT NULL)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + TABLE_NAME + "_" + USERNAME +
            "_index ON " + TABLE_NAME + "(" + USERNAME + " COLLATE NOCASE);";

    public UserRepository(Repository repository) {
        super(repository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    private void saveRecord(User user) {
        if (user == null)
            return;

        if (user.getId() == null) {
            getWritableDatabase().insert(TABLE_NAME, null, createValuesFor(user));
        } else {
            update(user);
        }
    }

    private void update(User user) {
        ContentValues contentValues = createValuesFor(user);
        getWritableDatabase().update(TABLE_NAME, contentValues, ID + " = ?", new String[]{user.getId().toString()});
    }

    private ContentValues createValuesFor(User user) {
        ContentValues values = new ContentValues();
        values.put(USERNAME, user.getUsername());
        values.put(PASSWORD, user.getPassword());
        values.put(DATE, user.getDate().getTime());
        return values;
    }

    public User getUserByUsername(String username) {
        String query =
                "SELECT * FROM " + UserRepository.TABLE_NAME + " WHERE " + UserRepository.USERNAME + " = '" + username + "'";
        List<User> users = getUsersCore(query);
        if(users.size() == 0)
            return null;

        return users.get(0);

    }

    public List<User> getLatestUsers() {
        String query =
                "SELECT * FROM " + UserRepository.TABLE_NAME + " ORDER BY " + UserRepository.DATE + " DESC limit 4";
        return getUsersCore(query);

    }

    public List<User> getUser(String username, String password) {
        String query =
                "SELECT * FROM " + UserRepository.TABLE_NAME + " WHERE " + USERNAME + " = '"+username+"' AND " +
                        PASSWORD + " = '"+ password + "'";
        return getUsersCore(query);

    }

    private List<User> getUsersCore(String sqlQuery) {
        Cursor cursor = null;
        List<User> users = new ArrayList<>();
        try {
            SQLiteDatabase db = TbrApplication.getInstance().getRepository().getReadableDatabase("Admin123");

            cursor = db.rawQuery(sqlQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                User user;
                do {
                    user = new User();
                    user.setId(cursor.getLong(cursor.getColumnIndex(UserRepository.ID)));
                    user.setUsername(cursor.getString(cursor.getColumnIndex(UserRepository.USERNAME)));
                    user.setPassword(cursor.getString(cursor.getColumnIndex(UserRepository.PASSWORD)));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Constants.KEY.DATE)));
                    user.setDate(calendar.getTime());
                    users.add(user);
                } while (cursor.moveToNext());
            }
            return users;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return users;


    }

    public void saveUser(String username, String password) {

        User user = getUserByUsername(username);

        User newUser = new User();
        if(user == null) {
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setDate(new Date());
        } else {
            newUser = user;
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setDate(new Date());
        }

        saveRecord(newUser);

    }

}