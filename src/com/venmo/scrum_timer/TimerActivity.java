package com.venmo.scrum_timer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends Activity {

	private Handler mHandler = new Handler();
	private final int REFRESH_RATE = 1000; // in milliseconds
	private long elapsedTime;
	private long startTime;
	private int initialTime;
	private long secs;
	private String seconds;
	private ArrayList<String> usernames;
	
	boolean shouldTeamGetCharged;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);
		
		Intent intent = getIntent();
		String time_input = intent.getStringExtra(StartActivity.TIME_INPUT);
		
		TextView initialTimer = (TextView)findViewById(R.id.timer);
		initialTimer.setText(time_input);
		
		//
		usernames = intent.getStringArrayListExtra(StartActivity.USERNAMES);
		TextView usernames_view = (TextView)findViewById(R.id.usernames);
		
		String s = "";
		for (int i = 0; i < usernames.size(); i++) {
			s += " " + usernames.get(i);
		}
		
		usernames_view.setText(s);
		//
		
		initialTime = Integer.parseInt(time_input);
		startClick(findViewById(R.id.timer));
		
		shouldTeamGetCharged = false;
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
		
		if (secs < 0) {
			shouldTeamGetCharged = true;
			Context context = getApplicationContext();
			CharSequence text = "over time";
			int duration = Toast.LENGTH_SHORT;
			mHandler.removeCallbacksAndMessages(startTimer);
			mHandler.removeCallbacks(startTimer);
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		else {
			seconds=String.valueOf(secs);
			((TextView)findViewById(R.id.timer)).setText(seconds);
		}
	}
	
}
