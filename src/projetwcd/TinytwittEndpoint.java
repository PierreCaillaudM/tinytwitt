package projetwcd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import projetwcd.beans.Utilisateur;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.users.*;

@Api(name = "tinytwittAPI", namespace = @ApiNamespace(ownerDomain = "mycompany.com", ownerName = "mycompany.com", packagePath = "services"))
public class TinytwittEndpoint {

	/**
	 * This method is used for inserting a new user. If user already
	 * exists, an exception is thrown
	 *
	 * @param user user to insert
	 */
	@ApiMethod(name = "insertUser")
	public void insertUser(Utilisateur user) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		//UserService userService = UserServiceFactory.getUserService();
		Collection<Filter> filters = new ArrayList<Filter>();
		filters.add(new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, user.getLogin()));
		filters.add(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, user.getEmail()));
		Filter filter = new Query.CompositeFilter(CompositeFilterOperator.OR, filters);
		Query query = new Query("User").
				setAncestor(KeyFactory.createKey("Table", "tableUser")).
				setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity != null){
			throw new EntityExistsException("User already exists");
		}else{
			userEntity = new Entity("User",KeyFactory.createKey("Table", "tableUser"));;
			userEntity.setIndexedProperty("login", user.getLogin());
			userEntity.setProperty("email", user.getEmail());
			userEntity.setProperty("mdp", user.getMdp());
			userEntity.setProperty("prenom", user.getPrenom());
			userEntity.setProperty("nom", user.getNom());
			//userEntity.setProperty("followers", new ArrayList<Long>());
			ds.put(userEntity);
			Entity userFollowersEntity = new Entity("UserFollowers",userEntity.getKey());
			userFollowersEntity.setProperty("followers", new ArrayList<Long>());
			//ArrayList<Entity> entities = new ArrayList<Entity>();
			//entities.add(userEntity);
			//entities.add(userFollowersEntity);
			ds.put(userFollowersEntity);
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
				setAncestor(KeyFactory.createKey("Table", "tableUser")).
				setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity == null){
			throw new EntityNotFoundException("User not found");
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
		Query query = new Query("User").
				setAncestor(KeyFactory.createKey("Table", "tableUser")).
				setFilter(filter);
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
		Query query = new Query("User").
				setAncestor(KeyFactory.createKey("Table", "tableUser")).
				setFilter(filter);
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
		Query query = new Query("User").
				setAncestor(KeyFactory.createKey("Table", "tableUser"));
		List<Entity> userEntities = ds.prepare(query).asList(FetchOptions.Builder.withDefaults());
		for(Entity entity : userEntities){
			users.add(new Utilisateur(entity));	
		}
		return users;
	}

	/**
	 * This method is used for inserting a new tweet.
	 *
	 * @param author author of the tweet
	 * @param message content of the tweet
	 */
	@ApiMethod(name = "insertTweet")
	public void insertTweet(@Named("login") String login,@Named("message") String message) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, login);
		Query query = new Query("User").
				setAncestor(KeyFactory.createKey("Table", "tableUser")).
				setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity == null){throw new EntityNotFoundException("User not found");}
		
		Entity tweet = new Entity("Tweet");
		tweet.setProperty("author", userEntity.getKey().getId());
		tweet.setProperty("message", message);
		tweet.setProperty("date", new Date());
		tweet.setProperty("likes", 0);
		ds.put(tweet);

		query = new Query("UserFollowers").setAncestor(userEntity.getKey());
		Entity userFollowersEntity = ds.prepare(query).asSingleEntity();
		if (userFollowersEntity == null){throw new EntityNotFoundException("User followers not found");}
		@SuppressWarnings("unchecked")
		ArrayList<Long> followers = (ArrayList<Long>) userFollowersEntity.getProperty("followers");
		
		Entity tweetIndex = new Entity("TweetIndex", tweet.getKey());
		tweetIndex.setProperty("receivers", followers);
		ds.put(tweetIndex);		
	}

	/**
	 * This method is used for getting a list of tweets. If the user does not
	 * exist in the datastore, an exception is thrown.
	 *
	 * @param login login of the user
	 * @return The list of user's tweets
	 */
	/*@ApiMethod(name = "getTweetsOf")
	public List<Tweet> getTweetsOf(User author) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new Query.FilterPredicate("login", Query.FilterOperator.EQUAL, login);
		Query query = new Query("User").
				setAncestor(KeyFactory.createKey("Table", "tableUser")).
				setFilter(filter);
		Entity userEntity = ds.prepare(query).asSingleEntity();
		if (userEntity == null){
			throw new EntityNotFoundException("User not found");
		}
		User user = new User(userEntity);
		return user;
	}*/

}