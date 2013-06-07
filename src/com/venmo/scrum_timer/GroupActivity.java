package com.venmo.scrum_timer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.venmo.scrum_timer.DeleteGroupDialog.DeleteGroupDialogListener;


// is this ok1?!!?!??!?!?!?!
public class GroupActivity extends FragmentActivity implements DeleteGroupDialogListener{

	private final static int ADD_GROUP_RESULT = 2; 
	private final static int EDIT_GROUP_RESULT = 1;
	private static GroupDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
		
		db = new GroupDatabase(this);
		updateGroups();
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
		updateGroups();
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
		
//		public int getMax() {
//			String countQuery = "SELECT * FROM " + TABLE_GROUPS;
//			SQLiteDatabase db = this.getReadableDatabase();
//			Cursor cursor = db.rawQuery(countQuery, null);
//			
//			return cursor.getCount();
//		}
	}

	private void updateGroups() {
		int num = 0;
		final LinearLayout layout = (LinearLayout) findViewById(R.id.groups_layout);
		
		// remove groups
		layout.removeAllViews();
		
		ArrayList<Group> groups = db.getAllGroups();
		
		for (int i = 0; i < groups.size(); i++) {
			//Log.v("UGH", "??" + groups.get(i));
			Group current = groups.get(i);
			final Group ugh = current;
			
			LinearLayout uLayout = new LinearLayout(this);
			uLayout.setLayoutDirection(0);
			uLayout.setId(num);
			
			TextView textView = new TextView(this);
			textView.setText(current.getName() + " " + 
							 current.getTime() + " seconds $" +
							 current.getAmt());
			textView.setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT,
					1.0f));
			
			Button editButton = new Button(this);
			editButton.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			editButton.setText(">");
			editButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent editGroupIntent = new Intent(GroupActivity.this, EditGroupActivity.class);
					editGroupIntent.putExtra("EDIT_GROUP", true);
					editGroupIntent.putExtra("GROUP_ID", ugh.getId());
					editGroupIntent.putExtra("GROUP_NAME", ugh.getName());
					editGroupIntent.putExtra("GROUP_TIME", ugh.getTime());
					editGroupIntent.putExtra("GROUP_AMT", ugh.getAmt());
					startActivityForResult(editGroupIntent, EDIT_GROUP_RESULT);
				}
			});
			
			Button uButton = new Button(this);
			uButton.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			uButton.setText("-");
			uButton.setOnClickListener(new View.OnClickListener() {		
				public void onClick(View v) {
					int view_id = ((View) v.getParent()).getId();
//					View view = findViewById(id);
//					layout.removeView(view);
					// int group_id = db.removeGroup(ugh.getId());
					int group_id = ugh.getId();
					showDeleteDialog(view_id, group_id);
					//Log.v("UGH", "IS THIS CHANGING " + ugh.getId());
				}
			});
			
			uLayout.addView(textView);
			uLayout.addView(editButton);
			uLayout.addView(uButton);
			layout.addView(uLayout);
			num++;
		}
	}

	
	private void showDeleteDialog(int view_id, int group_id) {
		FragmentManager fm = getSupportFragmentManager();
		
		Bundle bundle = new Bundle();
		bundle.putInt("VIEW_ID", view_id);
		bundle.putInt("GROUP_ID", group_id);
        DeleteGroupDialog deleteGroupDialog = new DeleteGroupDialog();
        deleteGroupDialog.setArguments(bundle);
        deleteGroupDialog.show(fm, "activity_delete_dialog");
	}
	
	@Override
	public void onFinishDeleteDialog(int signal, int view_id, int group_id) {
		// Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();
		if (signal == 0) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.groups_layout);
			View view = findViewById(view_id);
			layout.removeView(view);
			db.removeGroup(group_id);
		}
	}
}
