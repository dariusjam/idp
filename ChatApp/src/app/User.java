package app;

import java.util.ArrayList;

public class User {

	private String id;
	private String name;
	private ArrayList<User> friends;
	
	public User(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
}
