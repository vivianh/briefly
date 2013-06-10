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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;


// is this ok1?!!?!??!?!?!?!
public class GroupActivity extends ExpandableListActivity implements
	OnChildClickListener {

	private final static int ADD_GROUP_RESULT = 2; 
	private final static int EDIT_GROUP_RESULT = 1;
	private static GroupDatabase db;
	
	ArrayList<Group> allGroups;
	ArrayList<Object> allChildren = new ArrayList<Object>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_group);
		
		db = new GroupDatabase(this);
		Log.v("UGH", "" + db.getAllGroups().size());
		
		allGroups = db.getAllGroups();
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
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.group, menu);
		return true;
	}

	public void createGroup(View view) {
		Intent createGroupIntent = new Intent(this, EditGroupActivity.class);
		//startActivity(createGroupIntent);
		//createGroupIntent.putExtra("GROUP_ID", db.getSize());
		startActivityForResult(createGroupIntent, ADD_GROUP_RESULT);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String _groupname = data.getStringExtra(EditGroupActivity.GROUPNAME);
			String _timelimit = data.getStringExtra(EditGroupActivity.TIMELIMIT);
			String _chargeamt = data.getStringExtra(EditGroupActivity.CHARGEAMT);
			
			switch(requestCode) {
			case ADD_GROUP_RESULT:								
				db.addGroup(_groupname, _timelimit, _chargeamt);
				break;
			case EDIT_GROUP_RESULT:
				int _gid = data.getIntExtra("GROUP_ID", -1);
				Group g = new Group(_gid, _groupname, _timelimit, _chargeamt);
				db.updateGroup(g);
			}
		}
		//updateGroups();
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
		
		GroupDatabase(Context context) {
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

	}
	
	public void setChildData() {
		
		for (int i = 0; i < allGroups.size(); i++) {
			ArrayList<Person> child = new ArrayList<Person>();
			
			Person p1 = new Person(0, "Vivian", "7132487562", i);
			Person p2 = new Person(0, "Robert", "7132487562", i);
			
			child.add(p1);
			child.add(p2);
			
			allChildren.add(child);
		}
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Toast.makeText(this, "Clicked on Child", Toast.LENGTH_SHORT).show();
		return true;
	}

	public class NewAdapter extends BaseExpandableListAdapter {

		public ArrayList<Group> groupItem;
		public ArrayList<Person> tempChild;
		public ArrayList<Object> childItem = new ArrayList<Object>();
		// ???
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
			
			TextView text = null;
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.childrow, null);
			}
			
			text = (TextView) convertView.findViewById(R.id.textView1);
			text.setText(tempChild.get(childPosition)._name);
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
			
			((CheckedTextView) convertView).setText(groupItem.get(groupPosition)._name);
			((CheckedTextView) convertView).setChecked(isExpanded);
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
