package com.venmo.scrum_timer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class GroupActivity extends Activity {

	private final static int ADD_GROUP_RESULT = 2; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group, menu);
		return true;
	}

	public void createGroup(View view) {
		Intent createGroupIntent = new Intent(this, EditGroupActivity.class);
		//startActivity(createGroupIntent);
		startActivityForResult(createGroupIntent, ADD_GROUP_RESULT);
	}
	
	public class GroupOpenHelper extends SQLiteOpenHelper {
		
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
		
		GroupOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(GROUPS_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		public void addGroup(String name, String time, String amount) {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(GroupOpenHelper.COLUMN_GROUP_NAME, name);
			values.put(GroupOpenHelper.COLUMN_TIME, time);
			values.put(GroupOpenHelper.COLUMN_AMOUNT, amount);
			
			db.insert(GroupOpenHelper.TABLE_GROUPS, null, values);
			db.close();
		}
		
		public List<Group> getAllGroups() {
			List<Group> allGroups = new ArrayList<Group>();
			String selectQuery = "SELECT * FROM " + TABLE_GROUPS;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					Group group = new Group(cursor.getString(0),
						cursor.getString(1), cursor.getString(2));
					allGroups.add(group);
				} while (cursor.moveToNext());
			}			
			return allGroups;
		}
	}
	
	public class PeopleOpenHelper extends SQLiteOpenHelper {

		public PeopleOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
