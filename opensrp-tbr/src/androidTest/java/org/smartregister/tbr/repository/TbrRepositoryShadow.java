package org.smartregister.tbr.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by samuelgithengi on 1/18/18.
 */

public class TbrRepositoryShadow extends TbrRepository {

    private String password = "Sample PASS";

    public TbrRepositoryShadow(Context context, org.smartregister.Context openSRPContext) {
        super(context, openSRPContext);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(password);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(password);
    }
}
