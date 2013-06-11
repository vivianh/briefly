package com.venmo.scrum_timer;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;


public class GroupActivity extends ExpandableListActivity implements
	OnChildClickListener {

	private final static int ADD_GROUP_RESULT = 2; 
	private final static int EDIT_GROUP_RESULT = 1;
	private static GroupDatabase groupDB;
	private static PeopleDatabase peopleDB;
	
	public final static String GROUPNAME = "GROUPNAME";
	public final static String TIMELIMIT = "TIMELIMIT";
	public final static String CHARGEAMT = "CHARGEAMT";
	public final static String GROUPID = "GROUPID";
	public final static String ALL_NAMES = "NAMES";
	public final static String ALL_NUMBERS = "NUMBERS";
	
	ArrayList<Group> allGroups = new ArrayList<Group>();
	ArrayList<Object> allChildren = new ArrayList<Object>();
	ArrayList<Person> allPeople = new ArrayList<Person>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		groupDB = new GroupDatabase(this);
		peopleDB = new PeopleDatabase(this);
		updateGroups();
		updatePeople();
		setChildData();
		
		ExpandableListView exp = getExpandableListView();
		exp.setDividerHeight(2);
		exp.setGroupIndicator(null);
		exp.setClickable(true);
		
		NewAdapter mNewAdapter = new NewAdapter(allGroups, allChildren);
		mNewAdapter.setInflater((LayoutInflater)
					getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		getExpandableListView().setAdapter(mNewAdapter);
		exp.setOnChildClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.group, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_create_group:
				Intent createGroupIntent = new Intent(getApplicationContext(), EditGroupActivity.class);
				createGroupIntent.putExtra(GROUPID, groupDB.max());
				Log.v("PLZ", "group ID on creation " + groupDB.max());
				startActivityForResult(createGroupIntent, ADD_GROUP_RESULT);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String _groupname = data.getStringExtra(GROUPNAME);
			String _timelimit = data.getStringExtra(TIMELIMIT);
			String _chargeamt = data.getStringExtra(CHARGEAMT);
			int _groupid = data.getIntExtra(GROUPID, -1);
			
			switch(requestCode) {
			case ADD_GROUP_RESULT:								
				groupDB.addGroup(_groupname, _timelimit, _chargeamt);
				break;
			case EDIT_GROUP_RESULT:
				Group g = new Group(_groupid, _groupname, _timelimit, _chargeamt);
				groupDB.updateGroup(g);
				break;
			}
			
			ArrayList<String> newNames = data.getStringArrayListExtra(EditGroupActivity.NEW_NAMES);
			ArrayList<String> newNumbers = data.getStringArrayListExtra(EditGroupActivity.NEW_NUMBERS);
			
			// do some error checking here if ^ are not generated
			
			for (int i = 0 ; i < newNames.size(); i++) {
				String name = newNames.get(i);
				String number = newNumbers.get(i);
				peopleDB.addPersonToDB(name, number, _groupid);
			}
		}
		updateGroups();
		updatePeople();
		setChildData();
	}

	public void updateGroups() {
		allGroups = groupDB.getAllGroups();
	}
	
	public void updatePeople() {
		allPeople = peopleDB.getAllPeople();
	}
	
	// I need the save button to go back bc I need it to return bc the db is there
	public void editGroup(Group group) {
		Intent editGroupIntent = new Intent(getApplicationContext(), EditGroupActivity.class);

		ArrayList<String> inGroupNames = new ArrayList<String>();
		ArrayList<String> inGroupNumbers = new ArrayList<String>();
		int group_id = group._id;
		for (int i = 0; i < allPeople.size(); i++) {
			if (allPeople.get(i)._group_id == group_id) {
				inGroupNames.add(allPeople.get(i)._name);
				inGroupNumbers.add(allPeople.get(i)._phone);
			}
		}
		
		editGroupIntent.putExtra("EDIT_GROUP", true);
		editGroupIntent.putExtra(GROUPNAME, group._name);
		editGroupIntent.putExtra(TIMELIMIT, group._time);
		editGroupIntent.putExtra(CHARGEAMT, group._amt);
		editGroupIntent.putExtra(GROUPID, group._id);
		editGroupIntent.putStringArrayListExtra(ALL_NAMES, inGroupNames);
		editGroupIntent.putStringArrayListExtra(ALL_NUMBERS, inGroupNumbers);
		
		startActivityForResult(editGroupIntent, EDIT_GROUP_RESULT);
	}
	
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
		
		private int count = 1;
		// why did this break in onCreate
		
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
			count++;
			Log.v("PLZ", "is this not updating? " + count);
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
	
		public int max() {
			return count;
		}
	}
	
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
	}

	public void setChildData() {
		ArrayList<Person> child;
		int group_id = -1;
		
		ArrayList<Person> allPeople = peopleDB.getAllPeople();
		
		for (int i = 0; i < allGroups.size(); i++) {
			child = new ArrayList<Person>();			
			group_id = allGroups.get(i)._id;
			Log.v("PLZ", "trying to replace group id " + allGroups.get(i)._id);
			
			for (int j = 0; j < allPeople.size(); j++) {
				Log.v("PLZ", "id of person " + allPeople.get(j)._group_id);
				if (allPeople.get(j)._group_id == group_id) {
					child.add(allPeople.get(j));
				}
			}
			Log.v("PLZ", "size of " + child.size());
			allChildren.add(child);
			for (int j = 0; j < allChildren.size(); j++) {
				Log.v("PLZ", "kids " + j);
				for (int k = 0; k < child.size(); k++) {
					Log.v("PLZ", "kids and then " + child.get(k)._name);
				}
			}
		}
	}
	
	// toggles y/n for person there or not
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// Toast.makeText(this, "Clicked on Child", Toast.LENGTH_SHORT).show();
		return true;
	}

	public class NewAdapter extends BaseExpandableListAdapter {
		public ArrayList<Group> groupItem;
		public ArrayList<Person> tempChild;
		public ArrayList<Object> childItem = new ArrayList<Object>();
		public LayoutInflater mInflater;
		public Activity activity;
		
		public NewAdapter(ArrayList<Group> grList, ArrayList<Object> childList) {
			groupItem = grList;
			childItem = childList;
		}
		
		public void setInflater(LayoutInflater inflater, Activity act) {
			mInflater = inflater;
			activity = act;
		}
		
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			tempChild = (ArrayList<Person>) childItem.get(groupPosition);
			
			TextView textName = null;
			TextView textNum = null;
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.childrow, null);
			}
			
			textName = (TextView) convertView.findViewById(R.id.textView1);
			textName.setText(tempChild.get(childPosition)._name);
			
			textNum = (TextView) convertView.findViewById(R.id.textView2);
			textNum.setText(tempChild.get(childPosition)._phone);
			
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(activity, tempChild.get(childPosition)._name,
							Toast.LENGTH_SHORT).show();
				}
			});
			
			return convertView;
		}

		@SuppressWarnings("unchecked")
		@Override
		public int getChildrenCount(int groupPosition) {
			return ((ArrayList<String>)childItem.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public int getGroupCount() {
			return groupItem.size();
		}
		
		@Override
		public void onGroupCollapsed(int groupPosition) {
			super.onGroupCollapsed(groupPosition);
		}
		
		@Override
		public void onGroupExpanded(int groupPosition) {
			super.onGroupExpanded(groupPosition);
		}
		
		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.grouprow, null);				
			}
			
			View titleGroup = ((ViewGroup) convertView).getChildAt(1);
			View title = ((ViewGroup) titleGroup).getChildAt(0);			
			View infoGroup = ((ViewGroup) titleGroup).getChildAt(1);			
			View charge = ((ViewGroup) infoGroup).getChildAt(0);
			View time   = ((ViewGroup) infoGroup).getChildAt(1);
			
			((CheckedTextView) title).setText(groupItem.get(groupPosition)._name);
			((CheckedTextView) title).setChecked(isExpanded);
			
			String _charge = "$" + groupItem.get(groupPosition)._amt + ".00";
			String _time = groupItem.get(groupPosition)._time + " secs";
			
			((TextView) charge).setText(_charge);
			((TextView) time).setText(_time);
			
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
		
	}
}
