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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends Activity {

	private int initialTime;
	private ArrayList<String> usernames;
	private boolean charge;
	public CountDownTimer timer;
	private long secs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);
		
		Intent intent = getIntent();
		String time_input = intent.getStringExtra(StartActivity.TIME_INPUT);
		
		TextView initialTimer = (TextView)findViewById(R.id.timer);
		initialTimer.setText(time_input);
		
		usernames = intent.getStringArrayListExtra(StartActivity.USERNAMES);
		TextView usernames_view = (TextView)findViewById(R.id.usernames);
		
		String s = "";
		for (int i = 0; i < usernames.size(); i++) {
			s += " " + usernames.get(i);
		}
		
		usernames_view.setText(s);
		
		initialTime = Integer.parseInt(time_input);
		charge = true;
		//new TimerTask().execute(initialTime);
		
		timer = new MyTimer(initialTime*1000, 1000);
		timer.start();
	}

	public class MyTimer extends CountDownTimer {

		public MyTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onTick(long timeLeft) {
			Log.v("P2P", "anything???");
			((TextView)findViewById(R.id.timer)).setText(String.valueOf(timeLeft/1000));

		}
		
		@Override
		public void onFinish() {
			Log.v("P2P", "?!?!!");
			//new CreateChargeTask().execute();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timer, menu);
		return true;
	}
	
	public void stopClick(View view) {
		//charge = false;
		timer.cancel();
		Log.v("P2P", "tried to cancel timer...");
		
		Context context = getApplicationContext();
		CharSequence text = "no charge today!";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
//	private class TimerTask extends AsyncTask<Integer, Long, Void> {
//
//		private long startTime;
//		private long elapsedTime;
//		private long secs;
//		private String seconds;
//		private boolean timeOut;
//		
//		
//		@Override
//		protected void onPreExecute() {
//			 startTime = System.currentTimeMillis();
//			 timeOut = false;
//		}
//		
//		@Override
//		protected Void doInBackground(Integer... params) {
//			while (!timeOut) {
//				elapsedTime = (System.currentTimeMillis() - startTime)/1000;
//				publishProgress(elapsedTime);
//				if (elapsedTime >= initialTime) {
//					timeOut = true;
//				}
//			}
//			return null;
//		}
//		
//		@Override
//		protected void onProgressUpdate(Long... progress) {
//			secs = progress[0];
//			secs = initialTime - secs;
//			seconds=String.valueOf(secs);
//			((TextView)findViewById(R.id.timer)).setText(seconds);
//		}
//		
//		@Override
//		protected void onPostExecute(Void v) {
//			if (charge) {				
//				new CreateChargeTask().execute();
//				Log.v("P2P", "charge");
//			} else {
//				Log.v("P2P", "no charge");
//			}
//		}
//	}
	
	private class CreateChargeTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... voids) {
			String uname = "";
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
