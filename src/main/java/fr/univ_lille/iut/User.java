package fr.univ_lille.iut;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
	private String login;
	private String name;
	private String mail;

//Constructors
	public User(String login, String nom, String mail){
		this.login = login;
		this.name = name;
		this.mail = mail;
	}

	public User(){}


//Getters
	public String getLogin(){
		return this.login;
	}

	public String getName(){
		return this.name;
	}

	public String getMail(){
		return this.mail;
	}


//Setters
	public void setLogin(String login){
		this.login = login;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setMail(String mail){
		this.mail = mail;
	}


//Others
	public String toString(){
		return login + " " + name + " " + mail;
	}

	public boolean equals(Object u){
		return login.equals(((User) u).login) || name.equals(((User) u).name) || mail.equals(((User) u).mail);
	}
}
	
