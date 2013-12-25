package com.vivianhhuang.briefly;

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

	private final int CONTACT_PICKER_RESULT = 1001;
	private String mName, mNumber;
    private Context mContext;
	public final static String NAME = "com.vivianhhuang.briefly.extras.NAME";
	public final static String NUMBER = "com.vivianhhuang.briefly.extras.NUMBER";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_person);
        mContext = getApplicationContext();
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
					String id = result.getLastPathSegment();
					
					// gets phone #
					cursor = getContentResolver().query(
										Phone.CONTENT_URI, null,
										Phone.CONTACT_ID + "=?",
										new String[]{id}, null);
					int phoneIdx = cursor.getColumnIndex(Phone.DATA);
					if (cursor.moveToFirst()) {
						mNumber = cursor.getString(phoneIdx);
					} else {

					}

                    // gets contact name
					cursor = getContentResolver().query(result, null, null, null, null);
					if (cursor.moveToFirst()) {
						mName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					}
					
				} catch (Exception e) {
                    Log.e("CONTACT", "failed to get email data", e);
				} finally {
					if (cursor != null) {
						cursor.close();
					}
					if (mNumber.matches("\\+1[0-9]{10}")) {
						mNumber = mNumber.replace("+1", "");
					}
				}
				break;
			}
		} else {

		}
		
		if (mNumber.length() == 0) {
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, R.string.no_number_found, duration);
			toast.show();
		}
		
		getInfo(mName, mNumber);
	}

    // grabs current text from EditText fields
	private void getInfo(String str, String num) {
        EditText editName = (EditText) findViewById(R.id.add_person_name);
        EditText editNumber = (EditText) findViewById(R.id.add_person_number);

        if (editName.getText().toString().length() == 0) {
			editName.setText(str);
			editNumber.setText(num);
		}
		else {
			mName = editName.getText().toString();
			mNumber = editNumber.getText().toString();
		}
	}
	
	public void exitAddPerson(View view) {
		getInfo(mName, mNumber);
		View parent = (View) view.getParent();
		EditText nameEdit = (EditText) parent.findViewById(R.id.add_person_name);
		EditText numEdit = (EditText) parent.findViewById(R.id.add_person_number);
		if (mName.length() == 0) {
			nameEdit.setError(mContext.getText(R.string.name_prompt));
            return;
		} else if (!validNumber(mNumber)) {
			numEdit.setError(mContext.getText(R.string.number_prompt));
            return;
		}
		Intent returnPersonIntent = new Intent();
		returnPersonIntent.putExtra(NAME, mName);
		returnPersonIntent.putExtra(NUMBER, mNumber);

		setResult(RESULT_OK, returnPersonIntent);
		finish();
	}

    public void cancelAddPerson(View view) {
        Intent backIntent = new Intent();
        backIntent.putExtra(GroupActivity.CANCEL, true);
        setResult(RESULT_OK, backIntent);
        finish();
    }

    private boolean validNumber(String num) {
        if (num.length() == 0) return false;

        num = num.replaceAll("[^0-9]", "");
        if (num.length() == 11 && num.substring(0, 1) == "1") return true;
        if (num.length() == 10) return true;

        return false;
    }
}
