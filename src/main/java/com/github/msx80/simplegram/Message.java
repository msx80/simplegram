package com.github.msx80.simplegram;

public class Message {
	
	private Integer userId;
	private String username;
	private String displayName;
	private String text;
	private Bot bot;
	
	public Message(Bot bot, Integer userId, String username, String displayName, String text) {
		super();
		this.bot = bot;
		this.userId = userId;
		this.username = username;
		this.displayName = displayName;
		this.text = text;
	}
	
	
	
	public Bot getBot() {
		return bot;
	}



	public Integer getUserId() {
		return userId;
	}
	public String getUsername() {
		return username;
	}
	public String getDisplayName() {
		return displayName;
	}


	public String getText() {
		return text;
	}


	@Override
	public String toString() {
		return (username == null ? "" : "@"+username+" ")+displayName+"("+userId+") > "+text ;
	}
	
	
}
