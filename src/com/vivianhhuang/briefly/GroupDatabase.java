package com.vivianhhuang.briefly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class GroupDatabase extends SQLiteOpenHelper {
    public static final String TABLE_GROUPS = "groups";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_GROUP_NAME = "group_name";
    public static final String COLUMN_TIME = "time_limit";
    public static final String COLUMN_AMOUNT = "charge_amt";

    private static final String DATABASE_NAME = "groups.db";
    private static final int DATABASE_VERSION = 1;

    private static final String GROUPS_TABLE_CREATE =
            "CREATE TABLE " + TABLE_GROUPS + " (" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_GROUP_NAME + " TEXT, " +
                    COLUMN_TIME + " TEXT, " +
                    COLUMN_AMOUNT + " TEXT);";

    GroupDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GROUPS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }

    public void addGroup(String name, String time, String amount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, name);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_AMOUNT, amount);

        db.insert(TABLE_GROUPS, null, values);
        db.close();
    }

    public void updateGroup(Group group) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, group.getName());
        values.put(COLUMN_TIME, group.getTime());
        values.put(COLUMN_AMOUNT, group.getAmt());

        db.update(TABLE_GROUPS, values, COLUMN_ID + " = ?",
                new String[] {String.valueOf(group.getId())});
    }

    public void removeGroup(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUPS, COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    public ArrayList<Group> getAllGroups() {
        ArrayList<Group> allGroups = new ArrayList<Group>();
        String selectQuery = "SELECT * FROM " + TABLE_GROUPS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Group group = new Group(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3));
                allGroups.add(group);
            } while (cursor.moveToNext());
        }
        return allGroups;
    }

    public Group getGroup(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_GROUPS + " WHERE "
                + COLUMN_ID + " = " + id;
        Group gr = new Group();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            gr = new Group(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
        }
        return gr;
    }

    public int max() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(_id) AS _id FROM " + TABLE_GROUPS;
        Cursor cursor = db.rawQuery(query, null);

        int id = 0;
        if (cursor.moveToFirst())
        {
            do
            {
                id = cursor.getInt(0);
            } while(cursor.moveToNext());
        }
        return id;
    }
}