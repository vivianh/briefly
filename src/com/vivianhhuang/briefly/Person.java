package com.vivianhhuang.briefly;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {

	int _id;
	String _name;
	String _phone;
	int _group_id;
	
	public Person() {
		
	}
	
	public Person(int id, String name, String phone, int group_id) {
		_id = id;
		_name = name;
		_phone = phone;
		_group_id = group_id;
	}
	
	public Person(String name, String phone) {
		_name = name;
		_phone = phone;
	}
	
	public int getId() {
		return _id;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getPhone() {
		return _phone;
	}
	
	public int getGroupId() {
		return _group_id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeString(_name);
		dest.writeString(_phone);
		dest.writeInt(_group_id);
	}
	
	public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
		public Person createFromParcel(Parcel in) {
			return new Person(in);
		}
		
		public Person[] newArray(int size) {
			return new Person[size];
		}
	};
	
	private Person(Parcel in) {
		_id = in.readInt();
		_name = in.readString();
		_phone = in.readString();
		_group_id = in.readInt();
	}
}
