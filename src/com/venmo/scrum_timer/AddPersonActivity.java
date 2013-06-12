package com.venmo.scrum_timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddPersonActivity extends Activity {

	private final static int CONTACT_PICKER_RESULT = 1001;
	private static String name;
	private static String number;
	public final static String NAME = "NAME";
	public final static String NUMBER = "NUMBER";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_person);
		name = "";
		number = "";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
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
				try {
					Uri result = data.getData();
					Log.v("CONTACT", "got a contact " + result.toString());
					Log.v("CONTACT", "last segment " + result.getLastPathSegment());
					
					String id = result.getLastPathSegment();
					
					// gets phone #
					cursor = getContentResolver().query(
										Phone.CONTENT_URI, null,
										Phone.CONTACT_ID + "=?",
										new String[]{id}, null);
					int phoneIdx = cursor.getColumnIndex(Phone.DATA);
					if (cursor.moveToFirst()) {
						number = cursor.getString(phoneIdx);
						Log.v("CONTACT", "got # " + number);
					} else {
						Log.v("CONTACT", "didn't get #");
					}
					
					// gets name
					cursor = getContentResolver().query(result, null, null, null, null);
					if (cursor.moveToFirst()) {
						name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					}
					
				} catch (Exception e) {
					Log.e("CONTACT", "failed to get email data", e);
				} finally {
					if (cursor != null) {
						cursor.close();
					}
					if (number.matches("\\+1[0-9]{10}")) {
						number = number.replace("+1", "");
					}
					// number = phone;
					// name = "Vivian Huang";
				}
				break;
			}
		} else {
			Log.v("CONTACT", "ughh");
		}
		
		if (number.length() == 0) {
			Context context = getApplicationContext();
			CharSequence text = "No # associated";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		
		getInfo(name, number);
	}
	
	private void getInfo(String str, String num) {
		EditText editName = (EditText) findViewById(R.id.add_person_name);
		EditText editNumber = (EditText) findViewById(R.id.add_person_number);

		Log.v("UGH", "" + editName.getText().toString().length());
		Log.v("UGH", editName.getText().toString());
		
		if (editName.getText().toString().length() == 0) {
			editName.setText(str);
			editNumber.setText(num);
		}
		else {
			name = editName.getText().toString();
			number = editNumber.getText().toString();
		}
	}
	
	public void exitAddPerson(View view) {
		getInfo(name, number);
		View parent = (View) view.getParent();
		EditText _name = (EditText) parent.findViewById(R.id.add_person_name);
		EditText _num = (EditText) parent.findViewById(R.id.add_person_number);
		if (name.length() == 0) {
			_name.setError("");
			// some sort of error check here ugh
		} else if (number.length() == 0) {
			_num.setError("");
		}
		Intent returnPersonIntent = new Intent();
		returnPersonIntent.putExtra(NAME, name);
		returnPersonIntent.putExtra(NUMBER, number);
		Log.v("UGH", "name " + name);
		Log.v("UGH", "number " + number);
		setResult(RESULT_OK, returnPersonIntent);
		finish();
	}
}
