package com.venmo.scrum_timer;

public class Group {
	
	int _id;
	String _name;
	String _time;
	String _amt;
	
	public Group() {
		
	}
	
	public Group(String name, String time, String amt) {
		//_id = id;
		_name = name;
		_time = time;
		_amt = amt;
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
}
