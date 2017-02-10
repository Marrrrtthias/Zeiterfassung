package de.tastykatana.zeiterfassung;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by matthias on 2/10/17.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME_SESSIONS = "sessions";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";

    private static final String DATABASE_NAME = "sessions.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME_SESSIONS + "(" + COLUMN_START + " integer primary key, "
            + COLUMN_END + " integer);";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // not yet needed
    }

    public static void addWorkSession(WorkSession workSession, SQLiteDatabase database) {
        database.execSQL("insert into " + TABLE_NAME_SESSIONS + " values ("
                + workSession.getStart().getMillis() + ", " + workSession.getEnd().getMillis()
                + ");");
    }
}
