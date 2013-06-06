package com.venmo.scrum_timer;

public class Person {

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
}
