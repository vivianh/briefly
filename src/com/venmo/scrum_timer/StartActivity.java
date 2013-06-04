package com.venmo.scrum_timer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity {

	public final static String TIME_INPUT = "000";
	public final static String USERNAMES = "usernames lol";
	private static int num;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		num = 0;
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
		
		if(editText.getText().toString().length() == 0 ) {
			editText.setError( "Enter time in seconds" );
			return;
		}
		
		// is this the only way to pass strings with intents? ask
		setTimerIntent.putExtra(TIME_INPUT, time_input);

		ArrayList<String> usernames = getUsernames();
		if (usernames == null) {
			return;
		}
		setTimerIntent.putStringArrayListExtra(USERNAMES, usernames);
		
		startActivity(setTimerIntent);
	}
	
	// passing in view here!?!?!? for onclick methods uhhh
	public void addUsername(View view) {
		final LinearLayout layout = (LinearLayout)findViewById(R.id.usernameLayout);
		LinearLayout uLayout = new LinearLayout(this);
		uLayout.setLayoutDirection(0);
		
		uLayout.setId(num);
		EditText editText = new EditText(this);
		editText.setHint("Venmo username");
		//InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
		editText.setInputType(InputType.TYPE_CLASS_PHONE);
		editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		editText.requestFocus();
		editText.setLayoutParams(new TableLayout.LayoutParams(
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
		
		uLayout.addView(editText);
		uLayout.addView(uButton);
		layout.addView(uLayout);
		num++;
	}
	
	private ArrayList<String> getUsernames() {
		final LinearLayout layout = (LinearLayout)findViewById(R.id.usernameLayout);
		ArrayList<String> users = new ArrayList<String>();
	
		int c = layout.getChildCount();
		for (int i = 0; i < c; i++) {
			View v = layout.getChildAt(i);
			EditText e = (EditText)((ViewGroup) v).getChildAt(0);
			String u = e.getText().toString();
			if (u.length() > 0) {
				users.add(e.getText().toString());
			}
		}
		
		if (c == 0) {
			Context context = getApplicationContext();
			CharSequence text = "add someone!!!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return null;
		}
		
		if (users.size() == 0) {
			((TextView) ((ViewGroup) layout.getChildAt(0)).getChildAt(0)).setError("gotta charge someone");
			return null;
		}
		return users;
	}
	
}
