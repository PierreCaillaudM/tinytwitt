package projetwcd.beans;


import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Entity;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class Utilisateur implements Serializable{
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String login;
	@Persistent
	private String email;
	@Persistent
	private String mdp;
	@Persistent
	private String prenom;
	@Persistent
	private String nom;
	@Persistent
	private List<Long> followers;

	public Utilisateur() {}
	public Utilisateur(Entity e) {
		id = (Long) e.getKey().getId();
		login = (String) e.getProperty("login");
		email = (String) e.getProperty("email");
		mdp = (String) e.getProperty("mdp");
		prenom = (String) e.getProperty("prenom");
		nom = (String) e.getProperty("nom");
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMdp() {
		return mdp;
	}
	public void setMdp(String mdp) {
		this.mdp = mdp;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getNom() {
		return nom;
	}
	public void settNom(String nom) {
		this.nom = nom;
	}

	public List<Long> getFollowers() {
		return followers;
	}

	public void setFollowers(List<Long> followers) {
		this.followers = followers;
	}

}
