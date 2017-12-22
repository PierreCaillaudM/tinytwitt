package projetwcd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import projetwcd.beans.Twitt;
import projetwcd.beans.Utilisateur;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

@Api(name = "tinytwittAPI", namespace = @ApiNamespace(ownerDomain = "mycompany.com", ownerName = "mycompany.com", packagePath = "services"))
public class TinytwittEndpoint {

	
	/**
	 * This method is used for creating a new user. If user already
	 * exists, an exception is thrown
	 *
	 * @param 
	 */
	@ApiMethod(name = "createUser")
	public void createUser(@Named("login") String login, @Named("mail") String mail, @Named("mdp") String mdp, @Named("prenom") String prenom, @Named("nom") String nom) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Collection<Filter> filters = new ArrayList<Filter>();
		filters.add(new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, login));
		filters.add(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, mail));
		Filter filter = new Query.CompositeFilter(CompositeFilterOperator.OR, filters);
		Query query = new Query("User").setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity != null){
			throw new EntityExistsException("User already exists : " + login);
		}else{
			userEntity = new Entity("User");
			userEntity.setIndexedProperty("login", login);
			userEntity.setProperty("email", mail);
			userEntity.setProperty("mdp", mdp);
			userEntity.setProperty("prenom", prenom);
			userEntity.setProperty("nom", nom);
			
			ds.put(userEntity);
			Entity userFollowersEntity = new Entity("UserFollowers",userEntity.getKey());
			userFollowersEntity.setProperty("followers", new ArrayList<Long>());

			ds.put(userFollowersEntity);
			
			addFollower(login,login);
		}
	}

	
	/**
	 * This method is used for getting an existing user. If the user does not
	 * exist in the datastore, an exception is thrown.
	 *
	 * @param login login of the user
	 * @return The user. null if not found
	 */
	@ApiMethod(name = "getUser")
	public Utilisateur getUser(@Named("login") String login) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, login);
		Query query = new Query("User").
				//setAncestor(KeyFactory.createKey("Table", "tableUser")).
				setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity == null){
			throw new EntityNotFoundException("User not found : " + login);
		}
		Utilisateur user = new Utilisateur(userEntity);
		return user;
	}
	
	
	/**
	 * This method is used for deleting an existing user. If the user does not
	 * exist in the datastore, an exception is thrown.
	 *
	 * @param Utilisateur user to delete
	 */
	@ApiMethod(name = "deleteUser")
	public void deleteUser(@Named("login") String login) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, login);
		Query query = new Query("User").setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity == null){
			throw new EntityExistsException("User doesn't exists");
		}else{
			ds.delete(userEntity.getKey());	
		}
	}

	
	/**
	 * This method is used for adding a follower to the user. If the user does not
	 * exist in the datastore, an exception is thrown.
	 *
	 * @param Utilisateur user to follow
	 * @param login follower
	 * 
	 */
	@ApiMethod(name = "addFollower")
	public void addFollower(@Named("loginFollowed") String loginFollowed, @Named("loginFollower") String loginFollower) {
		//On récupère le follower et le followed
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Collection<Filter> filters = new ArrayList<Filter>();
		filters.add(new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, loginFollowed));
		filters.add(new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, loginFollower));
		Filter filter = new Query.CompositeFilter(CompositeFilterOperator.OR, filters);
		Query query = new Query("User").setFilter(filter);
		List<Entity> entities = ds.prepare(query).asList(FetchOptions.Builder.withDefaults());
		if(entities == null){throw new EntityNotFoundException("Entities null");}
		Entity followed = null;
		Entity follower = null;
		for(Entity user : entities){
			String login = (String) user.getProperty("login");
			if(login == null){throw new EntityNotFoundException("Login null");}
			if(login.equals(loginFollowed)){followed = user;}
			else{follower = user;}
		}
		if (follower == null){
			throw new EntityNotFoundException("Follower user not found");
		}
		if(followed == null){
			throw new EntityNotFoundException("Followed user not found");
		}
		
		query = new Query("UserFollowers").setAncestor(followed.getKey());
		Entity userFollowers = ds.prepare(query).asSingleEntity();
		@SuppressWarnings("unchecked")
		ArrayList<Long> followers = (ArrayList<Long>) userFollowers.getProperty("followers");
		if(followers == null){followers = new ArrayList<Long>();}
		if(followers.contains((Long) follower.getKey().getId())){
			throw new EntityExistsException("Already following this user");
		}else{
			followers.add((Long) follower.getKey().getId());
			userFollowers.setProperty("followers", followers);
			ds.put(userFollowers);
		}
	}
	
	
	/**
	 * This method is used for getting all existing users. If no entity
	 * exists in the datastore, it return null
	 *
	 * @return A list of user. null if not found
	 */
	@ApiMethod(name = "getUserList")
	public List<Utilisateur> getUserList() {
		List<Utilisateur> users = new ArrayList<Utilisateur>();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("User");
		List<Entity> userEntities = ds.prepare(query).asList(FetchOptions.Builder.withDefaults());
		for(Entity entity : userEntities){
			users.add(new Utilisateur(entity));	
		}
		return users;
	}
	
	
	/**
	 * This method is used for getting all follower for a user. If no entity
	 * exists in the datastore, it return null
	 *
	 * @return A list of user. null if not found
	 */
	@ApiMethod(name = "getUserFollowerList")
	public List<Utilisateur> getUserFollowerList(@Named("login") String login) {
		List<Utilisateur> users = new ArrayList<Utilisateur>();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, login);
		Query query = new Query("User").setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity == null){throw new EntityNotFoundException("User not found");}
		
		return users;
	}

	
	/**
	 * This method is used for inserting a new twitt.
	 *
	 * @param author author of the twitt
	 * @param message content of the twitt
	 */
	@ApiMethod(name = "insertTwitt")
	public void insertTwitt(@Named("login") String login,@Named("message") String message) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, login);
		Query query = new Query("User").setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity == null){throw new EntityNotFoundException("User not found");}
		
		Entity twitt = new Entity("Twitt");
		twitt.setProperty("author", (String) userEntity.getProperty("login"));
		twitt.setProperty("message", message);
		twitt.setProperty("date", new Date());
		twitt.setProperty("likes", 0);
		ds.put(twitt);

		query = new Query("UserFollowers").setAncestor(userEntity.getKey());
		Entity userFollowersEntity = ds.prepare(query).asSingleEntity();
		if (userFollowersEntity == null){throw new EntityNotFoundException("User followers not found");}
		@SuppressWarnings("unchecked")
		ArrayList<Long> followers = (ArrayList<Long>) userFollowersEntity.getProperty("followers");
		
		Entity twittIndex = new Entity("TwittIndex", twitt.getKey());
		twittIndex.setProperty("receivers", followers);
		ds.put(twittIndex);		
	}
	
	
	/**
	 * This method is used for inserting a new twitt.
	 *
	 * @param login user
	 * @return List of twitts
	 */
	@ApiMethod(name = "getTimelineOf")
	public List<Twitt> getTimelineOf(@Named("login") String login) {
		//On recup l'user avec le login
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, login);
		Query query = new Query("User").setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity == null){throw new EntityNotFoundException("User not found");}
		//On recup son id
		Long id = userEntity.getKey().getId();
		
		//On recup les keys des twitts de la timeline
		filter = new Query.FilterPredicate( "receivers" , Query.FilterOperator.EQUAL , id);
		query = new Query("TwittIndex").
				setFilter(filter).setKeysOnly();
		List<Entity> twittKeysEntity = ds.prepare(query).asList(FetchOptions.Builder.withDefaults());
		if(twittKeysEntity == null){throw new EntityNotFoundException("No twitt found");}
		
		ArrayList<Key> keys = new ArrayList<Key>();
		for(Entity e : twittKeysEntity){
			Key k = e.getParent();
			keys.add(k);
		}
		
		Map<Key, Entity> map = ds.get(keys);
		List<Entity> list = new ArrayList<Entity>(map.values());
		
		List<Twitt> result = new ArrayList<Twitt>();
		for(Entity e : list){
			result.add(new Twitt(e));
		}
		
		return result;
	}
	
	@ApiMethod(name = "createNbUsers")
	public ArrayList<String> createNbUsers(@Named("nbUsers") int nbUsers) {
		
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		
		ArrayList<String> listLogin = new ArrayList<String>();
		
		String login = "";
		String email = "";
		String mdp = "";
		String prenom = "";
		String nom = "";
		for(int i = 1; i <= nbUsers; i++){
			
			String rand = "";
			
			for(int j = 1; j <= 10; j++){
				int k = (int)Math.floor(Math.random() * 62);
				rand += chars.charAt(k);
			}
			login =  "user" + rand;
			email = "mail" + rand + "@mymail.com";
			mdp = "password" + rand;
			prenom = "prenom" + rand;
			nom = "nom" + rand;
			createUser(login, email, mdp, prenom, nom);
			listLogin.add(login);
		}
		return listLogin;
	}
	

	@ApiMethod(name = "createNbFollowers")
	public void createNbFollowers(@Named("nbFollowers") int nbFollowers, @Named("followed") String followed) {
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, followed);
		Query query = new Query("User").
				setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity == null){
			throw new EntityNotFoundException("User not found");
		}
		
		ArrayList<String> listLogin = new ArrayList<String>();
		listLogin = createNbUsers(nbFollowers);
		
		for(int i = 0; i < listLogin.size(); i++){
			addFollower(followed, listLogin.get(i));
		}
	}
	
	@ApiMethod(name = "createNbFolloweds")
	public void createNbFolloweds(@Named("nbFollowed") int nbFolloweds, @Named("follower") String follower) {
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, follower);
		Query query = new Query("User").
				setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity == null){
			throw new EntityNotFoundException("User not found");
		}
		
		ArrayList<String> listLogin = new ArrayList<String>();
		listLogin = createNbUsers(nbFolloweds);
		
		for(int i = 0; i < listLogin.size(); i++){
			addFollower(listLogin.get(i), follower);
		}
	}
}