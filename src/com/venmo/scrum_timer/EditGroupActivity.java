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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class EditGroupActivity extends Activity {

	private final static int ADD_PERSON_RESULT = 100;
	
	private static String _name;
	private static String _time;
	private static String _amt;
	private static int group_id;
	
	// ??? WHAT AM I DOING but really
	public final static String GROUPNAME = "GROUPNAME";
	public final static String TIMELIMIT = "TIMELIMIT";
	public final static String CHARGEAMT = "CHARGEAMT";
	
	private static PeopleDatabase db;
	ArrayList<Person> inGroup;
	ArrayList<String> inGroupNames;
	ArrayList<String> inGroupNumbers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);
		
		_name = ""; _time = ""; _amt = "";
		db = new PeopleDatabase(this);
		
		Intent intent = getIntent();
		
		// intent is from edit bc it has group object
		boolean toEdit = intent.getBooleanExtra("EDIT_GROUP", false);
		if (toEdit) {
			group_id = intent.getIntExtra("GROUP_ID", -1);
			_name    = intent.getStringExtra("GROUP_NAME");
			_time    = intent.getStringExtra("GROUP_TIME");
			_amt     = intent.getStringExtra("GROUP_AMT");

			Log.v("UGH", "check this out " + group_id);
			setInfo();
		} else {
			group_id = intent.getIntExtra("GROUP_ID", -1);
		}
		
		updateMembers(group_id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.edit_group, menu);
		return true;
	}

	public void addPerson(View view) {
		Intent addPersonIntent = new Intent(this, AddPersonActivity.class);
		startActivityForResult(addPersonIntent, ADD_PERSON_RESULT);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ADD_PERSON_RESULT:
				// does data already have intent or do I need getIntent()
				//data = getIntent();
				String name = data.getStringExtra(AddPersonActivity.NAME);
				String number = data.getStringExtra(AddPersonActivity.NUMBER);

				db.addPersonToDB(name, number, group_id);
				updateMembers(group_id);
			}
		}
	}
	
	private void setInfo() {
		EditText editName = (EditText) findViewById(R.id.edit_group_name);
		EditText editTime = (EditText) findViewById(R.id.edit_time_limit);
		EditText editAmt = (EditText) findViewById(R.id.edit_charge_amt);

		editName.setText(_name);
		editTime.setText(_time);
		editAmt.setText(_amt);
	}
	
	private void getInfo() {
		EditText editName = (EditText) findViewById(R.id.edit_group_name);
		EditText editTime = (EditText) findViewById(R.id.edit_time_limit);
		EditText editAmt = (EditText) findViewById(R.id.edit_charge_amt);
		
		_name = editName.getText().toString();
		_time = editTime.getText().toString();
		_amt = editAmt.getText().toString();
	}
	
	// this shouldn't return back to activity
	public void saveGroup(View view) {
		getInfo();
		Intent returnGroupIntent = new Intent();
		returnGroupIntent.putExtra(GROUPNAME, _name);
		returnGroupIntent.putExtra(TIMELIMIT, _time);
		returnGroupIntent.putExtra(CHARGEAMT, _amt);
		returnGroupIntent.putExtra("GROUP_ID", group_id);
		setResult(RESULT_OK, returnGroupIntent);
		finish();
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
			// TODO Auto-generated method stub
			
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
	
	// could definitely make this more efficient instead of redoing every time... 
	private void updateMembers(int group_id) {
		int num = 0;
		final LinearLayout layout = (LinearLayout) findViewById(R.id.edit_people_layout);
		
		// remove all members
		layout.removeAllViews();
		
		ArrayList<Person> people = db.getAllPeople();
		inGroup = new ArrayList<Person>();
		inGroupNames = new ArrayList<String>();
		inGroupNumbers = new ArrayList<String>();
		
		for (int i = 0; i < people.size(); i++) {
			Log.v("UGH", "group id" + people.get(i).getGroupId());
			Log.v("UGH", "set to 0" + group_id);
			if (people.get(i).getGroupId() == group_id) {
				inGroup.add(people.get(i));
				inGroupNames.add(people.get(i).getName());
				inGroupNumbers.add(people.get(i).getPhone());
			}
		}
		
		for (int i = 0; i < inGroup.size(); i++) {
			// Log.v("UGH", "??" + names.get(i));
			Person current = inGroup.get(i);
			final Person ugh = current;
			
			LinearLayout uLayout = new LinearLayout(this);
			uLayout.setLayoutDirection(0);
			uLayout.setId(num);
			
			TextView textView = new TextView(this);
			textView.setText(current.getName() + " " +
							 current.getPhone());
			textView.setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT,
					1.0f));
			
			Button uButton = new Button(this);
			uButton.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			uButton.setText("-");
			uButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					View view = findViewById(((View) v.getParent()).getId());
					layout.removeView(view);
					db.removePerson(ugh.getId());
				}
			});
			
			uLayout.addView(textView);
			uLayout.addView(uButton);
			layout.addView(uLayout);
			num++;
		}
	}
	
	public void startTimer(View view) {
		getInfo();
		Intent setTimerIntent = new Intent(this, TimerActivity.class);
		if (_time.length() == 0) {
			((EditText) findViewById(R.id.edit_time_limit)).setError("Enter time in seconds");
			return;
		}
		setTimerIntent.putExtra("TIME_INPUT", _time);
		setTimerIntent.putExtra("CHARGE_AMT", _amt);
		setTimerIntent.putStringArrayListExtra("PHONE_NUMBERS", inGroupNumbers);
		setTimerIntent.putStringArrayListExtra("NAMES", inGroupNames);
		startActivity(setTimerIntent);
	}
}
