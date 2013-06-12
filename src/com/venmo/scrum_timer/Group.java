package com.venmo.scrum_timer;

import android.os.Parcel;
import android.os.Parcelable;


public class Group implements Parcelable {
	
	int _id;
	String _name;
	String _time;
	String _amt;
	
	public Group() {
		
	}
	
	public Group(int id, String name, String time, String amt) {
		_id = id;
		_name = name;
		_time = time;
		_amt = amt;
	}
	
	public int getId() {
		return _id;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getTime() {
		return _time;
	}
	
	public String getAmt() {
		return _amt;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(_id);
		out.writeString(_name);
		out.writeString(_time);
		out.writeString(_amt);
	}
	
	public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
		public Group createFromParcel(Parcel in) {
			return new Group(in);
		}
	
		public Group[] newArray(int size) {
			return new Group[size];
		}
	};
	
	private Group(Parcel in) {
		_id = in.readInt();
		_name = in.readString();
		_time = in.readString();
		_amt = in.readString();
	}
}
