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

	private final int ADD_PERSON_RESULT = 100;
    public static final String NEW_NAMES = "com.vivianhhuang.briefly.extras.NEW_NAMES";
    public static final String NEW_NUMBERS = "com.vivianhhuang.briefly.extras.NEW_NUMBERS";

	private String mName, mTime, mAmt;
	private int mGroupId;
	private ArrayList<String> namesInGroup, numbersInGroup, newNames, newNumbers;
    private ArrayList<Person> allPeople;
	
	private ListView mListView;
	private ArrayAdapter<Person> mListAdapter;

    private EditText mEditName;
    private EditText mEditTime;
    private EditText mEditAmt;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);

        mEditName = (EditText) findViewById(R.id.edit_group_name);
        mEditTime = (EditText) findViewById(R.id.edit_time_limit);
        mEditAmt = (EditText) findViewById(R.id.edit_charge_amt);

		namesInGroup = new ArrayList<String>();
		numbersInGroup = new ArrayList<String>();
		newNames = new ArrayList<String>();
		newNumbers = new ArrayList<String>();
		allPeople = new ArrayList<Person>();
		Intent intent = getIntent();
		
		// intent is from edit bc it has group object
		boolean toEdit = intent.getBooleanExtra(GroupActivity.EDIT_GROUP, false);
		if (toEdit) {
            Group group = intent.getParcelableExtra(GroupActivity.GROUP);
            mName = group.getName();
            mTime = group.getTime();
            mAmt = group.getAmt();
            mGroupId = group.getId();

			namesInGroup = intent.getStringArrayListExtra(GroupActivity.ALL_NAMES);
			numbersInGroup = intent.getStringArrayListExtra(GroupActivity.ALL_NUMBERS);
			allPeople = intent.getParcelableArrayListExtra(GroupActivity.PEOPLE);
			setInfo();
		} else {
			mGroupId = intent.getIntExtra(GroupActivity.GROUP_ID, -1);
		}
		mListView = (ListView) findViewById(R.id.listView);
		mListAdapter = new CustomAdapter(this, R.id.listView, allPeople);
		mListView.setAdapter(mListAdapter);
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

            if (data.getBooleanExtra(GroupActivity.CANCEL, false)) {
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

		mListAdapter = new CustomAdapter(this, R.id.listView, allPeople);
		mListView.setAdapter(mListAdapter);
	}
	
	private void setInfo() {
		mEditName.setText(mName);
		mEditTime.setText(mTime);
		mEditAmt.setText(mAmt);
	}
	
	private void getInfo() {
		mName = mEditName.getText().toString();
		mTime = mEditTime.getText().toString();
		mAmt = mEditAmt.getText().toString();
        if (mAmt.contains(".00")) {
            int index = mAmt.indexOf(".");
            mAmt = mAmt.substring(0, index);
        }
	}
	
	public void saveGroup(View view) {
		getInfo();
        if (!validForm()) return;
		Intent returnGroupIntent = new Intent(this, GroupActivity.class);
        Group group = new Group(mGroupId, mName, mTime, mAmt);
        returnGroupIntent.putExtra(GroupActivity.GROUP, group);
		returnGroupIntent.putStringArrayListExtra(NEW_NAMES, newNames);
		returnGroupIntent.putStringArrayListExtra(NEW_NUMBERS, newNumbers);
		setResult(RESULT_OK, returnGroupIntent);
		finish();
	}
	
	public void cancel(View view) {
		Intent backIntent = new Intent(this, GroupActivity.class);
		backIntent.putExtra(GroupActivity.CANCEL, true);
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
		
		mListAdapter = new CustomAdapter(this, R.id.listView, allPeople);
		mListView.setAdapter(mListAdapter);
	}
	
	public void startTimer(View view) {
		getInfo();
		Intent setTimerIntent = new Intent(this, TimerActivity.class);
        if (!validForm()) return;
        if (numbersInGroup.size() == 0) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, R.string.no_members, duration);
            toast.show();
            return;
        }
        setTimerIntent.putExtra(GroupActivity.GROUP, new Group(-1, mName, mTime, mAmt));
		setTimerIntent.putStringArrayListExtra(GroupActivity.ALL_NUMBERS, namesInGroup);
		setTimerIntent.putStringArrayListExtra(GroupActivity.ALL_NAMES, numbersInGroup);
		startActivity(setTimerIntent);
	}

    private boolean validForm() {
        if (mName.length() == 0) {
            mEditName.setError("Enter group name");
            return false;
        }

        if (mTime.length() == 0) {
            mEditTime.setError("Enter time in seconds");
            return false;
        }

        if (mAmt.length() == 0) {
            mEditAmt.setError("Enter charge amount");
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
