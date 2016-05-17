package com.example.regi.zass.ContentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.example.regi.zass.Utils.Constants;

public class NoteDatabase extends SQLiteOpenHelper {

    private static NoteDatabase instance;

    private static final String DATABASE_NAME = "NotesDatabase.db";
    private static final String TABLE_NAME = "telepromtnotes";
    private static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME +
            " (_id INTEGER PRIMARY KEY, " +
            Constants.PROVIDER_TAGS + " TEXT , " +
            Constants.PROVIDER_READINGTEXT + " TEXT , " +
            Constants.PROVIDER_READINGTITLE + " TEXT , " +
            Constants.PROVIDER_DATECREATED + " TEXT , " +
            Constants.PROVIDER_KEY + " TEXT , " +
            Constants.PROVIDER_SPEED + " INTEGER , " +
            Constants.PROVIDER_SIZE + " INTEGER , " +
            Constants.PROVIDER_COUNTDOWN + " INTEGER , " +
            Constants.PROVIDER_MARGIN + " INTEGER , " +
            Constants.PROVIDER_SHOWMARKER + " INTEGER , " +
            Constants.PROVIDER_SHOWTIMER + " INTEGER , " +
            Constants.PROVIDER_SHOULDLOOP + " INTEGER , " +
            Constants.PROVIDER_LIGHT + " INTEGER )";


    private static final String SQL_DROP = "DROP TABLE IS EXISTS " + TABLE_NAME;

    private NoteDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static NoteDatabase getInstance(Context context){
        if (instance == null){
            instance = new NoteDatabase(context.getApplicationContext());
        }
        return  instance;
    }

    public void dropAll(){
        getWritableDatabase().delete(TABLE_NAME,null,null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        onCreate(db);
    }

    public Cursor getImages(String id, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(TABLE_NAME);

        if (id != null) {
            sqliteQueryBuilder.appendWhere("_id" + " = " + id);
        }

        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return cursor;
    }

    public long addNewNote(ContentValues values) throws SQLException {
        long id = getWritableDatabase().insert(TABLE_NAME, "", values);
        if (id <= 0) {
            throw new SQLException("Failed to add an image");
        }

        return id;
    }

    public int deleteNotes(String id) {
        if (id == null) {
            return getWritableDatabase().delete(TABLE_NAME, null, null);
        } else {
            return getWritableDatabase().delete(TABLE_NAME, "_id=?", new String[]{id});
        }
    }

    public int updateNotes(String id, ContentValues values) {
        if (id == null) {
            return getWritableDatabase().update(TABLE_NAME, values, null, null);
        } else {
            return getWritableDatabase().update(TABLE_NAME, values, "_id=?", new String[]{id});
        }
    }
}