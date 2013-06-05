package com.venmo.scrum_timer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
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
	private static ArrayList<String> names;
	private static ArrayList<String> numbers;
	
	private static String _name;
	private static String _time;
	private static String _amt;
	// ??? WHAT AM I DOING but really
	public final static String GROUPNAME = "GROUPNAME";
	public final static String TIMELIMIT = "TIMELIMIT";
	public final static String CHARGEAMT = "CHARGEAMT";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);
		
		names = new ArrayList<String>();
		numbers = new ArrayList<String>();
		_name = ""; _time = ""; _amt = "";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_group, menu);
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
				names.add(name);
				numbers.add(number);
				updateMembers();
			}
		}
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
		Intent returnGroupIntent = new Intent();
		returnGroupIntent.putExtra(GROUPNAME, _name);
		returnGroupIntent.putExtra(TIMELIMIT, _time);
		returnGroupIntent.putExtra(CHARGEAMT, _amt);
		//returnGroupIntent.putStringArrayListExtra(name, value);
		setResult(RESULT_OK, returnGroupIntent);
		finish();
	}
	
	// could definitely make this more efficient instead of redoing every time... 
	private void updateMembers() {
		int num = 0;
		final LinearLayout layout = (LinearLayout) findViewById(R.id.edit_people_layout);
		
		for (int i = 0; i < names.size(); i++) {
			Log.v("UGH", "??" + names.get(i));
			LinearLayout uLayout = new LinearLayout(this);
			uLayout.setLayoutDirection(0);
			uLayout.setId(num);
			
			TextView textView = new TextView(this);
			textView.setText(names.get(i));
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
				}
			});
			
			uLayout.addView(textView);
			uLayout.addView(uButton);
			layout.addView(uLayout);
			num++;
		}
	}
}
