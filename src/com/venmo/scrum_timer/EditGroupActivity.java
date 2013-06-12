package com.venmo.scrum_timer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class EditGroupActivity extends Activity {

	private final static int ADD_PERSON_RESULT = 100;
	
	private static String _name;
	private static String _time;
	private static String _amt;
	private static int group_id;
	ArrayList<String> namesInGroup;
	ArrayList<String> numbersInGroup;
	ArrayList<String> newNames;
	ArrayList<String> newNumbers;
	ArrayList<Person> yayPeople;
	public static final String NEW_NAMES = "NEWNAMES";
	public static final String NEW_NUMBERS = "NEWNUMBERS";
	
	private ListView mainListView;
	private ArrayAdapter<Person> listAdapter;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);
		
		namesInGroup = new ArrayList<String>();
		numbersInGroup = new ArrayList<String>();
		newNames = new ArrayList<String>();
		newNumbers = new ArrayList<String>();
		yayPeople = new ArrayList<Person>();
		Intent intent = getIntent();
		
		// intent is from edit bc it has group object
		boolean toEdit = intent.getBooleanExtra("EDIT_GROUP", false);
		if (toEdit) {
			group_id = intent.getIntExtra(GroupActivity.GROUPID, -1);
			_name    = intent.getStringExtra(GroupActivity.GROUPNAME);
			_time    = intent.getStringExtra(GroupActivity.TIMELIMIT);
			_amt     = intent.getStringExtra(GroupActivity.CHARGEAMT);
			namesInGroup = intent.getStringArrayListExtra(GroupActivity.ALL_NAMES);
			numbersInGroup = intent.getStringArrayListExtra(GroupActivity.ALL_NUMBERS);
			yayPeople = intent.getParcelableArrayListExtra(GroupActivity.PEOPLE);
			setInfo();
		} else {
			group_id = intent.getIntExtra(GroupActivity.GROUPID, -1);
		}
		mainListView = (ListView) findViewById(R.id.listView);
		listAdapter = new CustomAdapter(this, R.id.listView, yayPeople);
		mainListView.setAdapter(listAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_group, menu);		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_person:
			Intent addPersonIntent = new Intent(this, AddPersonActivity.class);
			startActivityForResult(addPersonIntent, ADD_PERSON_RESULT);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ADD_PERSON_RESULT:
				// does data already have intent or do I need getIntent()
				//data = getIntent();
				String name = data.getStringExtra(AddPersonActivity.NAME);
				String number = data.getStringExtra(AddPersonActivity.NUMBER);
				namesInGroup.add(name);
				numbersInGroup.add(number);
				newNames.add(name);
				newNumbers.add(number);
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
	
	public void saveGroup(View view) {
		getInfo();
		Intent returnGroupIntent = new Intent(this, GroupActivity.class);
		returnGroupIntent.putExtra(GroupActivity.GROUPNAME, _name);
		returnGroupIntent.putExtra(GroupActivity.TIMELIMIT, _time);
		returnGroupIntent.putExtra(GroupActivity.CHARGEAMT, _amt);
		returnGroupIntent.putExtra(GroupActivity.GROUPID, group_id);
		returnGroupIntent.putStringArrayListExtra(NEW_NAMES, newNames);
		returnGroupIntent.putStringArrayListExtra(NEW_NUMBERS, newNumbers);
		setResult(RESULT_OK, returnGroupIntent);
		finish();
	}
	
	public void cancel(View view) {
		Intent backIntent = new Intent(this, GroupActivity.class);
		backIntent.putExtra("CANCEL", true);
		setResult(RESULT_OK, backIntent);
		finish();
	}
	
	public void startTimer(View view) {
		getInfo();
		Intent setTimerIntent = new Intent(this, TimerActivity.class);
		if (_time.length() == 0) {
			((EditText) findViewById(R.id.edit_time_limit)).setError("Enter time in seconds");
			return;
		}
		setTimerIntent.putExtra(GroupActivity.GROUPNAME, _name);
		setTimerIntent.putExtra(GroupActivity.TIMELIMIT, _time);
		setTimerIntent.putExtra(GroupActivity.CHARGEAMT, _amt);
		setTimerIntent.putStringArrayListExtra(GroupActivity.ALL_NUMBERS, namesInGroup);
		setTimerIntent.putStringArrayListExtra(GroupActivity.ALL_NAMES, numbersInGroup);
		startActivity(setTimerIntent);
	}
	
	public static class ViewHolder {
		public ImageView icon;
		public TextView name;
		public TextView phone;
		public ImageView trash;
	}

	public class CustomAdapter extends ArrayAdapter<Person> {
		private ArrayList<Person> entries;
		private Activity activity;
		
		public CustomAdapter(Activity a, int textViewResourceId, ArrayList<Person> entries) {
			super(a, textViewResourceId, entries);
			this.entries = entries;
			this.activity = a;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder holder;
//			if (v == null) {
				LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.childrow, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) v.findViewById(R.id.person_icon);
				holder.name = (TextView) v.findViewById(R.id.person_name);
				holder.phone = (TextView) v.findViewById(R.id.person_number);
				holder.trash = (ImageView) v.findViewById(R.id.trash1);
//			} else {
//				holder = (ViewHolder) v.getTag();
//			}
			
			final Person person = entries.get(position);
			if (person != null) {
				// holder.icon.setImageResource(R.drawable.ic_blue_person);
				holder.name.setText(person._name);
				holder.phone.setText(person._phone);
			}
			return v;
		}
	}
}
