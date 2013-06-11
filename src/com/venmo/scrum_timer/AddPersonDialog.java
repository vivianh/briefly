package com.venmo.scrum_timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AddPersonDialog extends DialogFragment {

	public interface AddPersonDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
	}
	
	// instance of interface
	AddPersonDialogListener mListener;
	
	public AddPersonDialog() {
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mListener = (AddPersonDialogListener) activity;		
		} catch (ClassCastException e) {
			throw new ClassCastException (activity.toString() + " ugh");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = "Add a contact";
		Dialog dialog = new AlertDialog.Builder(getActivity())
			.setTitle(title)
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onDialogPositiveClick(AddPersonDialog.this);					
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onDialogNegativeClick(AddPersonDialog.this);
				}
			})
			.create();
		
		return dialog;
	}
}