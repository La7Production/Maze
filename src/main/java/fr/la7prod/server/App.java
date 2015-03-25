package fr.la7prod.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.filter.LoggingFilter;
import org.skife.jdbi.v2.DBI;

@ApplicationPath("/")
public class App extends Application {
	
	public static DBI dbi = new DBI("jdbc:sqlite:l7p.db");
	
	public App() {
		UserDAO dao = dbi.open(UserDAO.class);
		dao.createTable();
		dao.close();
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(LoggingFilter.class);
		s.add(UserResource.class);
		s.add(UserDBIResource.class);
		return s;
	}

}
