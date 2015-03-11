package fr.la7prod.server;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(UserMapper.class)
public interface UserDAO {
	
	@SqlUpdate("create table if not exists users (login text primary key, password text,"
			+ " firstname text, lastname text, birthday date, email text)")
	public void createTable();
	
	@SqlUpdate("insert into users values (:login, :password, :firstname, :lastname,"
			+ " :birthday, :email)")
	public void addUser(@BindBean User user);
	
	@SqlUpdate("delete from users where login = :login")
	public void removeUser(@Bind("login") String login);
	
	@SqlUpdate("delete from users where login = :login")
	public void removeUser(@BindBean User user);
	
	@SqlUpdate("update users set password = :password, firstname = :firstname, lastname = :lastname,"
			+ " birthday = :birthday, email = :email where login = :login")
	public void updateUser(@BindBean User user);
	
	@SqlQuery("select * from users where login = :login")
	public User findByLogin(@Bind("login") String login);
	
	@SqlQuery("select * from users")
	public List<User> getAllUsers();
	
	/**
	 * close with no args is used to close the connection
	 */
	public void close();

}
