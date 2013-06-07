package com.venmo.scrum_timer;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class DeleteGroupDialog extends DialogFragment {

//	private int signal;
	
	public DeleteGroupDialogListener mListener;
	
	public interface DeleteGroupDialogListener {
		void onFinishDeleteDialog(int signal, int view_id, int group_id);
	}
	
	public DeleteGroupDialog() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final int view_id = getArguments().getInt("VIEW_ID");
		final int group_id = getArguments().getInt("GROUP_ID");
		View view = inflater.inflate(R.layout.activity_delete_dialog, container);
		getDialog().setTitle("Confirm delete");
		Button delete = (Button) getDialog().findViewById(R.id.confirm_delete_yes);
		Button cancel = (Button) getDialog().findViewById(R.id.confirm_delete_no);
		
		if (delete == null) {
			Log.v("STRUGGLE", "delete is null");
		}
		
		delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mListener.onFinishDeleteDialog(0, view_id, group_id);
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		
		return view;
	}
//	
//	public void delete(View view) {
//		signal = 0;
//		int view_id = this.getArguments().getInt("VIEW_ID");
//		int group_id = this.getArguments().getInt("GROUP_ID");
//		this.mListener.onFinishDeleteDialog(signal, view_id, group_id);
//	}
//	
//	public void cancel(View view) {
//		signal = 1;
//		this.mListener.onFinishDeleteDialog(signal, -1, -1);
//	}
}
