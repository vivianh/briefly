package com.venmo.scrum_timer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class StartActivity extends Activity {

	public final static String TIME_INPUT = "000";
	public final static String USERNAMES = "usernames lol";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	public void setTimer(View view) {
		Intent setTimerIntent = new Intent(this, TimerActivity.class);
		EditText editText = (EditText)findViewById(R.id.time_input);
		String time_input = editText.getText().toString();
		// is this the only way to pass strings with intents? ask
		setTimerIntent.putExtra(TIME_INPUT, time_input);

		//
		ArrayList<String> usernames = getUsernames();
		//setTimerIntent.putExtra(USERNAMES, usernames);
		setTimerIntent.putStringArrayListExtra(USERNAMES, usernames);
		//
		
		startActivity(setTimerIntent);
	}
	
	
	private ArrayList<String> getUsernames() {
		ArrayList<String> users = new ArrayList<String>();
		
		EditText username_1 = (EditText)findViewById(R.id.username_1);
		users.add(username_1.getText().toString());
		
		EditText username_2 = (EditText)findViewById(R.id.username_2);
		users.add(username_2.getText().toString());
		
		EditText username_3 = (EditText)findViewById(R.id.username_3);
		users.add(username_3.getText().toString());
		
		return users;
	}
	
}
