package com.vivianhhuang.briefly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class PeopleDatabase extends SQLiteOpenHelper {

    public static final String TABLE_PEOPLE = "people";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "user_name";
    public static final String COLUMN_PHONE = "phone_num";
    public static final String COLUMN_GROUP_ID = "group_id";

    private static final String DATABASE_NAME = "people.db";
    private static final int DATABASE_VERSION = 1;

    private static final String PEOPLE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_PEOPLE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_GROUP_ID + " TEXT);";

    public PeopleDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PEOPLE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPersonToDB(String name, String phone, int group_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_GROUP_ID, group_id);

        db.insert(TABLE_PEOPLE, null, values);
        db.close();
    }

    public void removePerson(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PEOPLE, COLUMN_ID + " = ?",
                new String[] {String.valueOf(id)} );
        db.close();
    }

    public ArrayList<Person> getAllPeople() {
        ArrayList<Person> allPeople = new ArrayList<Person>();
        String selectQuery = "SELECT * FROM " + TABLE_PEOPLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Person person = new Person(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3));
                allPeople.add(person);
            } while (cursor.moveToNext());
        }
        return allPeople;
    }

    public int getGroupID(int _id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_GROUP_ID + " AS " +
                COLUMN_GROUP_ID + " FROM " + TABLE_PEOPLE + " WHERE " +
                COLUMN_ID + " = '" + _id + "'";
        Cursor cursor = db.rawQuery(query, null);

        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        return id;
    }
}
