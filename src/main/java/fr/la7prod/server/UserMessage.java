package fr.la7prod.server;


public class UserMessage {
	
	private String direction;
	private String user;
	
	public UserMessage() {}
	
	public UserMessage(String direction, String user) {
		this.direction = direction;
		this.user = user;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return "direction: " + direction + ", user: " + user;
	}

}