package com.venmo.scrum_timer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.text.InputType;
import android.util.Log;
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
	private final static int CONTACT_PICKER_RESULT = 1001;
	private static ArrayList<String> usernames;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		num = 0;
		usernames = new ArrayList<String>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
		setTimerIntent.putExtra(TIME_INPUT, time_input);

		usernames.addAll(getUsernames());
		if (usernames == null) {
			return;
		}
		
		setTimerIntent.putStringArrayListExtra(USERNAMES, usernames);		
		startActivity(setTimerIntent);
	}
	
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
	
	private void createField(String phone) {
		final LinearLayout layout = (LinearLayout)findViewById(R.id.usernameLayout);
		LinearLayout uLayout = new LinearLayout(this);
		uLayout.setLayoutDirection(0);
		uLayout.setId(num);
		EditText editText = new EditText(this);
		editText.setText(phone);
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
		
//		if (usernames.size() > 0) {
//			return users;
//		}
		
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
	
	
	public void pickContacts(View view) {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT:
				Cursor cursor = null;
				String phone = "";
				try {
					Uri result = data.getData();
					Log.v("CONTACT", "got a contact " + result.toString());
					Log.v("CONTACT", "last segment " + result.getLastPathSegment());
					
					String id = result.getLastPathSegment();
					cursor = getContentResolver().query(
										Phone.CONTENT_URI, null,
										Phone.CONTACT_ID + "=?",
										new String[]{id}, null);
					int phoneIdx = cursor.getColumnIndex(Phone.DATA);
					if (cursor.moveToFirst()) {
						phone = cursor.getString(phoneIdx);
						Log.v("CONTACT", "got # " + phone);
					} else {
						Log.v("CONTACT", "didn't get #");
					}
					
				} catch (Exception e) {
					Log.e("CONTACT", "failed to get email data", e);
				} finally {
					if (cursor != null) {
						cursor.close();
					}
					//EditText contactField = (EditText) findViewById(R.id.contact_field);
					if (phone.matches("\\+1[0-9]{10}")) {
						phone = phone.replace("+1", "");
						Log.v("CONTACT", "this regex" + phone);
					}
					createField(phone);
					//contactField.setText(phone);
					//usernames.add(phone);
				}
				break;
			}
		} else {
			Log.v("CONTACT", "ughh");
		}
	}
}
