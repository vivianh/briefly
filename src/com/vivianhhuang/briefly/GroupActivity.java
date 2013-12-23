package com.vivianhhuang.briefly;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GroupActivity extends ExpandableListActivity implements
	OnChildClickListener {

	private final static int ADD_GROUP_RESULT = 2; 
	private final static int EDIT_GROUP_RESULT = 1;
	private final static int DELETE_GROUP_RESULT = 3;

    private static GroupDatabase groupDB;
	static PeopleDatabase peopleDB;
	private static HashMap<Integer, ArrayList<Person>> global;
	
	public final static String GROUPNAME = "GROUPNAME";
	public final static String GROUPID = "GROUPID";
	public final static String ALL_NAMES = "NAMES";
	public final static String ALL_NUMBERS = "NUMBERS";
	public final static String PEOPLE = "PEOPLE";
	
	ArrayList<Group> allGroups = new ArrayList<Group>();
	ArrayList<Object> allChildren = new ArrayList<Object>();
	ArrayList<Person> allPeople = new ArrayList<Person>();
	
	ExpandableListView exp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		groupDB = new GroupDatabase(this);
		peopleDB = new PeopleDatabase(this);
		global = new HashMap<Integer, ArrayList<Person>>();
		updatePeople();
		updateGroups();
		cache();
		setChildData();
		
		exp = getExpandableListView();
		exp.setDividerHeight(2);
		exp.setGroupIndicator(null);
		exp.setClickable(true);
		
		NewAdapter mNewAdapter = new NewAdapter(allGroups, allChildren);
		mNewAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		exp.setAdapter(mNewAdapter);
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
				// Log.v("PLZ", "group ID on creation " + groupDB.max());
				startActivityForResult(createGroupIntent, ADD_GROUP_RESULT);
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

            Group group = data.getParcelableExtra(GROUPNAME);
            int gId = group.getId();
            String gName = group.getName();
            String gTime = group.getTime();
            String gAmt = group.getAmt();

			if (data.getBooleanExtra(DeleteGroupActivity.DELETE, false) == true) {
                groupDB.removeGroup(gId);
			}

			// only if this is not cancel...
			switch(requestCode) {
			case ADD_GROUP_RESULT:
				groupDB.addGroup(gName, gTime, gAmt);
				gId = groupDB.max();
				break;
			case EDIT_GROUP_RESULT:
				// _chargeamt = _chargeamt.substring(0, _chargeamt.length()-3);
                groupDB.updateGroup(group);
				break;
			}
			
			ArrayList<String> newNames = data.getStringArrayListExtra(EditGroupActivity.NEW_NAMES);
			ArrayList<String> newNumbers = data.getStringArrayListExtra(EditGroupActivity.NEW_NUMBERS);
			
			// do some error checking here if ^ are not generated
			
			if (newNames != null && newNumbers != null) {
				for (int i = 0 ; i < newNames.size(); i++) {
					String name = newNames.get(i);
					String number = newNumbers.get(i);
					peopleDB.addPersonToDB(name, number, gId);
				}
			}
			
		}

		updatePeople();
		updateGroups();
		setChildData();
		
		NewAdapter mNewAdapter = new NewAdapter(allGroups, allChildren);
		mNewAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		exp.setAdapter(mNewAdapter);
		exp.setOnChildClickListener(this);
	}

    // not clear what this does...
	public void updateGroups() {
		allGroups = groupDB.getAllGroups();
		for (int i = 0; i < allGroups.size(); i++) {
			Group g = allGroups.get(i);
			for (int j = 0; j < allPeople.size(); j++) {
				Person p = allPeople.get(j);
			}
		}
	}
	
	public void updatePeople() {
		allPeople = peopleDB.getAllPeople();
	}
	
	public void startTimer(View view) {
		View parent = (View) view.getParent();
        ArrayList<Person> please = global.get(parent.getTag());
        if (please.size() == 0) {
            Context context = getApplicationContext();
            CharSequence text = "No members currently; edit the meeting group to add members.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }

		ArrayList<String> numbers = new ArrayList<String>();
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < please.size(); i++) {
			names.add(please.get(i)._name);
			numbers.add(please.get(i)._phone);
		}
		
		Intent startTimerIntent = new Intent(this, TimerActivity.class);
		Group g = groupDB.getGroup((Integer) parent.getTag());
        startTimerIntent.putExtra(GROUPNAME, g);
		startTimerIntent.putStringArrayListExtra(ALL_NUMBERS, numbers);
		startTimerIntent.putStringArrayListExtra(ALL_NAMES, names);
		startActivity(startTimerIntent);
	}
	
	public void cache() {
		for (int i = 0; i < allGroups.size(); i++) {
			Group g = allGroups.get(i);
			ArrayList<Person> p = new ArrayList<Person>();
			for (int j = 0; j < allPeople.size(); j++) {
				if (allPeople.get(j).getGroupId() == g._id) {
					p.add(allPeople.get(j));
				}
			}
			global.put(g._id, p);
		}
	}
	
	// I need the save button to go back bc I need it to return bc the db is there
	public void editGroup(View view) {
		View parent = (View) view.getParent();
		
		String name = ((CheckedTextView) parent.findViewById(R.id.group_name)).getText().toString();
		String time = ((TextView) parent.findViewById(R.id.time_limit)).getText().toString();
		String amt = ((TextView) parent.findViewById(R.id.charge_amt)).getText().toString();
		// int group_id = groupDB.getGroupId(name);
		int group_id = (Integer) parent.getTag();
		time = time.split(" ")[0];
		amt = amt.substring(1);
		
		Intent editGroupIntent = new Intent(getApplicationContext(), EditGroupActivity.class);

		ArrayList<String> inGroupNames = new ArrayList<String>();
		ArrayList<String> inGroupNumbers = new ArrayList<String>();
		ArrayList<Person> yayPeople = new ArrayList<Person>();
		
		
		for (int i = 0; i < allPeople.size(); i++) {
			if (allPeople.get(i)._group_id == group_id) {
				yayPeople.add(allPeople.get(i));
				inGroupNames.add(allPeople.get(i)._name);
				inGroupNumbers.add(allPeople.get(i)._phone);
			}
		}
		
		editGroupIntent.putExtra("EDIT_GROUP", true);
        editGroupIntent.putExtra(GROUPNAME, new Group(group_id, name, time, amt));
		editGroupIntent.putStringArrayListExtra(ALL_NAMES, inGroupNames);
		editGroupIntent.putStringArrayListExtra(ALL_NUMBERS, inGroupNumbers);
		editGroupIntent.putExtra(PEOPLE, yayPeople);
		startActivityForResult(editGroupIntent, EDIT_GROUP_RESULT);
	}
	
	public void deletePerson(View view) {
		View parent = (View) view.getParent();
		peopleDB.removePerson((Integer)parent.getTag());
		updatePeople();
		updateGroups();
		setChildData();
		
		NewAdapter mNewAdapter = new NewAdapter(allGroups, allChildren);
		mNewAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		exp.setAdapter(mNewAdapter);
		exp.setOnChildClickListener(this);
	}
	
	public void deleteGroup(View view) {
		Intent deleteGroupIntent = new Intent(this, DeleteGroupActivity.class);
		View parent = (View) view.getParent();
		deleteGroupIntent.putExtra(GROUPID, (Integer)parent.getTag());
		startActivityForResult(deleteGroupIntent, DELETE_GROUP_RESULT);
	}

	public void setChildData() {
		allChildren.clear();
		
		for (int i = 0; i < allGroups.size(); i++) {
            ArrayList<Person> child = new ArrayList<Person>();
			int group_id = allGroups.get(i)._id;
			
			for (int j = 0; j < allPeople.size(); j++) {
				if (allPeople.get(j)._group_id == group_id) {
					child.add(allPeople.get(j));
				}
			}
			allChildren.add(child);
		}
	}
	
	// toggles selection for person attendance
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
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

		@SuppressWarnings("unchecked")
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			tempChild = (ArrayList<Person>) childItem.get(groupPosition);
			return tempChild.get(childPosition)._id;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			
			tempChild = (ArrayList<Person>) childItem.get(groupPosition);

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.childrow, null);
			}
			
			convertView.setTag((int)getChildId(groupPosition, childPosition));
			
			TextView textName = (TextView) convertView.findViewById(R.id.person_name);
			textName.setText(tempChild.get(childPosition)._name);
			
			TextView textNum = (TextView) convertView.findViewById(R.id.person_number);
			textNum.setText(tempChild.get(childPosition)._phone);
			
			final View view = convertView;
			
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String name = ((TextView) v.findViewById(R.id.person_name)).getText().toString();
					String number = ((TextView) v.findViewById(R.id.person_number)).getText().toString();

					int _id = (Integer) view.getTag();
					int group_id = peopleDB.getGroupID(_id);
					ArrayList<Person> people = global.get(group_id);
					
					ImageView icon = (ImageView) v.findViewById(R.id.person_icon);
					TextView textName = (TextView) v.findViewById(R.id.person_name);
					TextView textNum = (TextView) v.findViewById(R.id.person_number);

                    if (!icon.getTag().equals("selected")) {
                        icon.setImageResource(R.drawable.person_blue);
                        icon.setTag("selected");
                        textName.setTextColor(Color.parseColor("#000000"));
                        textNum.setTextColor(Color.parseColor("#A9A9A9"));
                        people.add(new Person(_id, name, number, group_id));
                        global.put(group_id, people);
                    } else {
                        icon.setImageResource(R.drawable.person_gray);
                        icon.setTag("unselected");
                        textName.setTextColor(Color.parseColor("#A9A9A9"));
                        textNum.setTextColor(Color.parseColor("#A9A9A9"));
                        for (int i = 0; i < people.size(); i++) {
                            if (people.get(i)._group_id == group_id && people.get(i)._name.equals(name)) {
                                // Log.v("PLZ", "removed " + people.get(i)._name);
                                people.remove(i);
                                global.put(group_id, people);
                            }
                        }
                    }
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
			return groupItem.get(groupPosition)._id;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			notifyDataSetChanged();
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.grouprow, null);				
			}

			convertView.setTag((int)getGroupId(groupPosition));
			
			CheckedTextView title = (CheckedTextView) (convertView.findViewById(R.id.group_name));
			TextView charge = (TextView) (convertView.findViewById(R.id.charge_amt));
			TextView time = (TextView) (convertView.findViewById(R.id.time_limit));
			
			title.setText(groupItem.get(groupPosition)._name);
			title.setChecked(isExpanded);
            String _charge = groupItem.get(groupPosition)._amt;
            String _chargeText = _charge;

            if (_chargeText.contains(".")) {
                // if amt is .x
                if (_chargeText.substring(_chargeText.indexOf(".") + 1).length() == 1) {
                    _chargeText += "0";
                }
            } else {
                // should be just an integer
                _chargeText += ".00";
            }

			_chargeText = "$" + _chargeText;
			String _time = groupItem.get(groupPosition)._time + " secs";
			charge.setText(_chargeText);
			time.setText(_time);
			
			ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);
			if (isExpanded) {				
				arrow.setImageResource(R.drawable.up);
			} else {
				arrow.setImageResource(R.drawable.down);
			}
			
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