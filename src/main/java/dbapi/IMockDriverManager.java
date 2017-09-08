package dbapi;

import java.sql.Connection;
/** Provides generating a mock object.
 * */
public interface IMockDriverManager {
	public Connection getConnection();

}
