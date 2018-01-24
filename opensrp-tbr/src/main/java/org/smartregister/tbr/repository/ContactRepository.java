package org.smartregister.tbr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.tbr.model.Contact;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ContactRepository extends BaseRepository {

    private static final String TAG = ContactRepository.class.getCanonicalName();

    public static final String TABLE_NAME = "ec_contacts";
    public static final String ID = "_id";
    public static final String CONTACT_ID = "contact_id";//contact base entity id
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String STAGE = "stage";
    public static final String INDEX_RELATIONSHIP = "index_relationship";
    public static final String BASE_ENTITY_ID = "base_entity_id";//index / parent Base Entity ID
    public static final String AGE = "age";
    public static final String GENDER = "gender";
    public static final String IS_NEGATIVE = "is_negative";
    public static final String FORM_SUBMISSION_ID = "form_submission_id";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            CONTACT_ID + "  VARCHAR  NULL, " +
            FIRST_NAME + "  VARCHAR NULL," +
            LAST_NAME + "  VARCHAR NOT NULL, " +
            BASE_ENTITY_ID + "  VARCHAR NOT NULL," +
            INDEX_RELATIONSHIP + "  VARCHAR NOT NULL, " +
            AGE + "  VARCHAR NOT NULL, " +
            GENDER + "  VARCHAR NOT NULL, " +
            STAGE + "  VARCHAR NOT NULL, " +
            FORM_SUBMISSION_ID + "  VARCHAR NOT NULL, " +
            IS_NEGATIVE + "  BOOLEAN TRUE, " +
            CREATED_AT + " VARCHAR NOT NULL, " +
            UPDATED_AT + " INTEGER NOT NULL, " +
            "UNIQUE(" + CONTACT_ID + ", " + FORM_SUBMISSION_ID + ") ON CONFLICT IGNORE )";

    private static final String INDEX_CONTACT_ID = "CREATE INDEX " + TABLE_NAME + "_" + CONTACT_ID +
            "_index ON " + TABLE_NAME + "(" + CONTACT_ID + " COLLATE NOCASE);";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + TABLE_NAME + "_" + BASE_ENTITY_ID +
            "_index ON " + TABLE_NAME + "(" + BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_FORM_SUBMISSION_ID = "CREATE INDEX " + TABLE_NAME + "_" + FORM_SUBMISSION_ID +
            "_index ON " + TABLE_NAME + "(" + FORM_SUBMISSION_ID + " COLLATE NOCASE);";

    public ContactRepository(Repository repository) {
        super(repository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_CONTACT_ID);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_FORM_SUBMISSION_ID);
    }

    public void saveContact(Contact contact) {
        if (contact == null)
            return;
        else if (contact.getUpdatedAt() == null) {
            contact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
        }
        if (contact.getId() == null) {
            Long existingId = getExistingContactId(contact);
            if (existingId != null) {
                contact.setId(existingId);
                update(contact);
            } else {
                getWritableDatabase().insert(TABLE_NAME, null, createValuesFor(contact));
            }
        } else {
            update(contact);
        }
    }

    private void update(Contact result) {
        ContentValues contentValues = createValuesFor(result);
        getWritableDatabase().update(TABLE_NAME, contentValues, ID + " = ?", new String[]{result.getId().toString()});
    }

    private ContentValues createValuesFor(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(CONTACT_ID, contact.getContactId());
        values.put(FIRST_NAME, contact.getFirstName());
        values.put(LAST_NAME, contact.getLastName());
        values.put(BASE_ENTITY_ID, contact.getBaseEntityId());
        values.put(AGE, contact.getAge());
        values.put(INDEX_RELATIONSHIP, contact.getIndexRelationship());
        values.put(GENDER, contact.getGender());
        values.put(STAGE, contact.getStage());
        values.put(IS_NEGATIVE, contact.isNegative());
        values.put(FORM_SUBMISSION_ID, contact.getFormSubmissionId());
        values.put(CREATED_AT, contact.getCreatedAt());
        values.put(UPDATED_AT, contact.getUpdatedAt());
        return values;
    }

    private Long getExistingContactId(Contact result) {
        String selection = null;
        String[] selectionArgs = null;
        Long id = null;
        if (StringUtils.isNotBlank(result.getFormSubmissionId()) && StringUtils.isNotBlank(result.getContactId())) {
            selection = FORM_SUBMISSION_ID + " = ? " + COLLATE_NOCASE + " OR " + CONTACT_ID + " = ? " + COLLATE_NOCASE;
            selectionArgs = new String[]{result.getFormSubmissionId(), result.getContactId()};
        } else if (StringUtils.isNotBlank(result.getContactId())) {
            selection = CONTACT_ID + " = ? " + COLLATE_NOCASE;
            selectionArgs = new String[]{result.getContactId()};
        } else if (StringUtils.isNotBlank(result.getFormSubmissionId())) {
            selection = FORM_SUBMISSION_ID + " = ? " + COLLATE_NOCASE;
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

    private List<Contact> getContactsByContactID(String contactBaseEntityId) {
        Cursor cursor = null;
        List<Contact> contacts = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query =
                    "SELECT * FROM " + ContactRepository.TABLE_NAME + " WHERE " + ContactRepository.CONTACT_ID + " = '" + contactBaseEntityId + "'";
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                Contact contact;
                do {
                    contact = new Contact();
                    contact.setId(cursor.getLong(cursor.getColumnIndex(ContactRepository.ID)));
                    contact.setAge(cursor.getString(cursor.getColumnIndex(ContactRepository.AGE)));
                    contact.setContactId(cursor.getString(cursor.getColumnIndex(ContactRepository.CONTACT_ID)));
                    contact.setFirstName(cursor.getString(cursor.getColumnIndex(ContactRepository.FIRST_NAME)));
                    contact.setLastName(cursor.getString(cursor.getColumnIndex(ContactRepository.LAST_NAME)));
                    contact.setGender(cursor.getString(cursor.getColumnIndex(ContactRepository.GENDER)));
                    contact.setFormSubmissionId(cursor.getString(cursor.getColumnIndex(ContactRepository.FORM_SUBMISSION_ID)));
                    contact.setBaseEntityId(cursor.getString(cursor.getColumnIndex(ContactRepository.BASE_ENTITY_ID)));
                    contact.setIndexRelationship(cursor.getString(cursor.getColumnIndex(ContactRepository.INDEX_RELATIONSHIP)));
                    contact.setCreatedAt(cursor.getString(cursor.getColumnIndex(ContactRepository.CREATED_AT)));
                    contact.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(ContactRepository.UPDATED_AT)));
                    contact.setStage(cursor.getString(cursor.getColumnIndex(ContactRepository.STAGE)));
                    contact.setNegative(cursor.getInt(cursor.getColumnIndex(ContactRepository.IS_NEGATIVE)) > 0);

                    contacts.add(contact);
                } while (cursor.moveToNext());
            }
            return contacts;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return contacts;

    }

}
