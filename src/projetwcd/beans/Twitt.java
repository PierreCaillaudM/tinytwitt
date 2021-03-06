package projetwcd.beans;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Entity;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class Twitt implements Serializable, Comparable<Twitt>{
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	Long id;
	@Persistent
	//private Utilisateur author;
	private String author;
	@Persistent
	private String message;
	@Persistent
	private Date date;
	@Persistent
	private int likes;

	public Twitt() {}
	
	public Twitt(Entity e) {
		this.author = (String) e.getProperty("author");
		this.message = (String) e.getProperty("message");
		this.date = (Date) e.getProperty("date");
		//this.likes = (int) e.getProperty("likes");
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}
	
	  @Override
	  public int compareTo(Twitt o) {
	    return getDate().compareTo(o.getDate());
	  }

}