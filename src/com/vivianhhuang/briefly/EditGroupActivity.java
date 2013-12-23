package com.vivianhhuang.briefly;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

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
	ArrayList<Person> allPeople;
	public static final String NEW_NAMES = "NEWNAMES";
	public static final String NEW_NUMBERS = "NEWNUMBERS";
	
	private ListView mainListView;
	private ArrayAdapter<Person> listAdapter;

    private EditText editName;
    private EditText editTime;
    private EditText editAmt;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);

        editName = (EditText) findViewById(R.id.edit_group_name);
        editTime = (EditText) findViewById(R.id.edit_time_limit);
        editAmt = (EditText) findViewById(R.id.edit_charge_amt);

		namesInGroup = new ArrayList<String>();
		numbersInGroup = new ArrayList<String>();
		newNames = new ArrayList<String>();
		newNumbers = new ArrayList<String>();
		allPeople = new ArrayList<Person>();
		Intent intent = getIntent();
		
		// intent is from edit bc it has group object
		boolean toEdit = intent.getBooleanExtra("EDIT_GROUP", false);
		if (toEdit) {
            Group group = intent.getParcelableExtra(GroupActivity.GROUPNAME);
            _name = group.getName();
            _time = group.getTime();
            _amt = group.getAmt();
            group_id = group.getId();

			namesInGroup = intent.getStringArrayListExtra(GroupActivity.ALL_NAMES);
			numbersInGroup = intent.getStringArrayListExtra(GroupActivity.ALL_NUMBERS);
			allPeople = intent.getParcelableArrayListExtra(GroupActivity.PEOPLE);
			setInfo();
		} else {
			group_id = intent.getIntExtra(GroupActivity.GROUPID, -1);
		}
		mainListView = (ListView) findViewById(R.id.listView);
		listAdapter = new CustomAdapter(this, R.id.listView, allPeople);
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

            if (data.getBooleanExtra("CANCEL", false) == true) {
                return;
            }

			switch (requestCode) {
			case ADD_PERSON_RESULT:
				String name = data.getStringExtra(AddPersonActivity.NAME);
				String number = data.getStringExtra(AddPersonActivity.NUMBER);
				namesInGroup.add(name);
				numbersInGroup.add(number);
				newNames.add(name);
				newNumbers.add(number);
			}
		}
		
		if (newNames != null) {
			for (int i = 0; i < newNames.size(); i++) {
				Person p = new Person(newNames.get(i), newNumbers.get(i));
				allPeople.add(p);
			}
		}

		listAdapter = new CustomAdapter(this, R.id.listView, allPeople);
		mainListView.setAdapter(listAdapter);
	}
	
	private void setInfo() {
		editName.setText(_name);
		editTime.setText(_time);
		editAmt.setText(_amt);
	}
	
	private void getInfo() {
		_name = editName.getText().toString();
		_time = editTime.getText().toString();
		_amt = editAmt.getText().toString();
        if (_amt.contains(".00")) {
            int index = _amt.indexOf(".");
            _amt = _amt.substring(0, index);
        }
	}
	
	public void saveGroup(View view) {
		getInfo();
        if (!validForm()) return;
		Intent returnGroupIntent = new Intent(this, GroupActivity.class);
        Group group = new Group(group_id, _name, _time, _amt);
        returnGroupIntent.putExtra(GroupActivity.GROUPNAME, group);
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
	
	public void deletePerson(View view) {
		View parent = (View) view.getParent();
		GroupActivity.peopleDB.removePerson((Integer)parent.getTag());
		
		for (int i = 0; i < allPeople.size(); i++) {
			if (allPeople.get(i)._id == ((Integer) parent.getTag())) {
				allPeople.remove(i);
			}
		}
		
		listAdapter = new CustomAdapter(this, R.id.listView, allPeople);
		mainListView.setAdapter(listAdapter);
	}
	
	public void startTimer(View view) {
		getInfo();
		Intent setTimerIntent = new Intent(this, TimerActivity.class);
        if (!validForm()) return;
        if (numbersInGroup.size() == 0) {
            Context context = getApplicationContext();
            CharSequence text = "No members currently";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        setTimerIntent.putExtra(GroupActivity.GROUPNAME, new Group(-1, _name, _time, _amt));
		setTimerIntent.putStringArrayListExtra(GroupActivity.ALL_NUMBERS, namesInGroup);
		setTimerIntent.putStringArrayListExtra(GroupActivity.ALL_NAMES, numbersInGroup);
		startActivity(setTimerIntent);
	}

    private boolean validForm() {
        if (_name.length() == 0) {
            editName.setError("Enter group name");
            return false;
        }

        if (_time.length() == 0) {
            editTime.setError("Enter time in seconds");
            return false;
        }

        if (_amt.length() == 0) {
            editAmt.setError("Enter charge amount");
            return false;
        }

        return true;
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
		
		public CustomAdapter(Activity activity, int textViewResourceId, ArrayList<Person> entries) {
			super(activity, textViewResourceId, entries);
			this.entries = entries;
			this.activity = activity;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.childrow, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) v.findViewById(R.id.person_icon);
            holder.name = (TextView) v.findViewById(R.id.person_name);
            holder.phone = (TextView) v.findViewById(R.id.person_number);
            holder.trash = (ImageView) v.findViewById(R.id.trash1);
			
			final Person person = entries.get(position);
			if (person != null) {
				holder.name.setText(person._name);
				holder.phone.setText(person._phone);
				v.setTag(person._id);
			}
			return v;
		}
	}
}
