package com.venmo.scrum_timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DeleteGroupDialog extends DialogFragment {

	public interface DeleteGroupDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
	}
	
	// instance of interface
	DeleteGroupDialogListener mListener;
	
	public DeleteGroupDialog() {
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mListener = (DeleteGroupDialogListener) activity;		
		} catch (ClassCastException e) {
			throw new ClassCastException (activity.toString() + " ugh");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = "UGH";
		Dialog dialog = new AlertDialog.Builder(getActivity())
			.setTitle(title)
			.setPositiveButton("Delete", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onDialogPositiveClick(DeleteGroupDialog.this);					
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onDialogNegativeClick(DeleteGroupDialog.this);
				}
			})
			.create();
		
		return dialog;
	}
}
