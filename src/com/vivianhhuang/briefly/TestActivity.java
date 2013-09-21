package com.vivianhhuang.briefly;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
		RelativeLayout myButton = (RelativeLayout)findViewById(R.id.june);
		myButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//do something
			}
		});
		
	}
}
