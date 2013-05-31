package com.venmo.scrum_timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class TimerActivity extends Activity {

	private Handler mHandler = new Handler();
	private final int REFRESH_RATE = 1000; // in milliseconds
	private long elapsedTime;
	private long startTime;
	private int initialTime;
	private long secs;
	private String seconds;
	
	boolean shouldTeamGetCharged;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);
		
		Intent intent = getIntent();
		String time_input = intent.getStringExtra(StartActivity.TIME_INPUT);
		
		TextView initialTimer = (TextView)findViewById(R.id.timer);
		initialTimer.setText(time_input);
		
		initialTime = Integer.parseInt(time_input);
		startClick(findViewById(R.id.timer));
		
		shouldTeamGetCharged = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timer, menu);
		return true;
	}
	
	public void startClick(View view) {
		startTime = System.currentTimeMillis();
		// check handler functions ughh
		mHandler.removeCallbacks(startTimer);
		mHandler.postDelayed(startTimer, 0);
	}

	public void stopClick(View view) {
		mHandler.removeCallbacks(startTimer);
	}
	
	public void restartClick(View view) {
		startTime = System.currentTimeMillis() - elapsedTime;
		mHandler.removeCallbacks(startTimer);
		mHandler.postDelayed(startTimer, 0);
	}
	
	private Runnable startTimer = new Runnable() {
		public void run() {
			elapsedTime = System.currentTimeMillis() - startTime;
			updateTimer(elapsedTime);
			mHandler.postDelayed(this, REFRESH_RATE);
		}
	};	

	private void updateTimer(float time) {
		secs = (long)(time/1000);
		secs = initialTime - secs;
		seconds=String.valueOf(secs);
    	((TextView)findViewById(R.id.timer)).setText(seconds);
	}
	
}
