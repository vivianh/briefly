package com.vivianhhuang.briefly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class DeleteGroupActivity extends Activity {

	private int mId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_group);
		Intent intent = getIntent();
		mId = intent.getIntExtra(GroupActivity.GROUP_ID, -1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public void delete(View view) {
		Intent returnIntent = new Intent(this, GroupActivity.class);
		returnIntent.putExtra(GroupActivity.DELETE, true);
		returnIntent.putExtra(GroupActivity.GROUP_ID, mId);
		setResult(RESULT_OK, returnIntent);
		finish();
	}
	
	public void cancel(View view) {
		Intent cancelIntent = new Intent();
		cancelIntent.putExtra(GroupActivity.CANCEL, true);
		setResult(RESULT_OK, cancelIntent);
		finish();
	}
}
