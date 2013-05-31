package com.venmo.scrum_timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class TimerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);
		
		Intent intent = getIntent();
		String time_input = intent.getStringExtra(StartActivity.TIME_INPUT);
		
		TextView initialTimer = (TextView)findViewById(R.id.timer);
		initialTimer.setText(time_input);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timer, menu);
		return true;
	}
	
}
