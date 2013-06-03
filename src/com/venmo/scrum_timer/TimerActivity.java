package com.venmo.scrum_timer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
	private boolean killMe;
	
	
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
		
		killMe = false;
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
		
		Log.v("ScrumTimer", "updateTimer() was called. time is: " + time);
		Log.v("ScrumTimer", "updateTimer() was called. secs is: " + secs);
		
		if (killMe) {
			return;
		}
		if (secs < 0) {
			Context context = getApplicationContext();
			CharSequence text = "over time";
			int duration = Toast.LENGTH_SHORT;
			mHandler.removeCallbacks(startTimer);
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			// augafldsja;lkfj;dlskajf;lkajs;lkj
			new CreateChargeTask().execute();
			killMe = true;
		}
		else {
			seconds=String.valueOf(secs);
			((TextView)findViewById(R.id.timer)).setText(seconds);
		}
	}
	
	private class timerTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private class CreateChargeTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... voids) {
//			String[] user_names = new String[] {
//				"vivian"	
//			};
			String uname = "";
//			for (int i = 0; i < user_names.length; i++) {
//				uname = user_names[i];
//				doTheCharge(uname);
//			}
			for (int i = 0; i < usernames.size(); i++) {
				uname = usernames.get(i);
				doTheCharge(uname);
			}
			return null;
		}
		
		protected void doTheCharge(String uname) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			try {
				HttpPost thepost = new HttpPost("https://api.venmo.com/payments");
				// does this 2 do anything? capacity?
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("access_token", "WsQJPyg6MRpCbbVdGyDHHpHqZYfs5eEP"));
				nameValuePairs.add(new BasicNameValuePair("phone", uname));
				nameValuePairs.add(new BasicNameValuePair("amount", "-.01"));
				nameValuePairs.add(new BasicNameValuePair("note", "test welp"));
				nameValuePairs.add(new BasicNameValuePair("audience", "private"));
				
				thepost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				response = httpclient.execute(thepost);
				
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					Log.v("P2P", "this is good. made charge");
				} else {
					response.getEntity().getContent().close();
					Log.v("P2P", "this is bad. didn't make charge");
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				
			} catch (IOException e) {
				
			}
		}
	}
}
