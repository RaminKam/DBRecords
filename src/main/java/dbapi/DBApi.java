package dbapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
public class DBApi {
	private final static String JDBC_DRIVER="com.mysql.jdbc.Driver";
	private final static String URL="jdbc:mysql://localhost/users_rep";
	private final static String USER_NAME="root";
	private final static String PASSWORD="root";
	
	private final static String INSERT_RECORD_STRING="INSERT INTO users (id, name, lastname) VALUES (?, ?, ?)";
	private final static String UPDATE_RECORD_STRING="UPDATE users SET lastname = ? WHERE name = ?";
	private final static String SELECT_RECORD_STRING="SELECT id, name, lastname FROM users WHERE name = ?";
	private final static String DELETE_RECORD_STRING="DELETE FROM users WHERE id=?";
	
	private IMockDriverManager mockDrMng=null;
	
	public DBApi(){
		
	}
	public DBApi(IMockDriverManager mockDrMng){
		this.mockDrMng=mockDrMng;
	}
	


	/** Provides a choice between using a mock or real connection.
	 * */
	public Connection getConnection() throws SQLException{
		if(mockDrMng==null)
		{
			System.setProperty("jdbc.drivers",JDBC_DRIVER);
			return DriverManager.getConnection(URL, USER_NAME, PASSWORD);
		}else
		{
			return mockDrMng.getConnection();
		}
	}

	

	public void insertRecord(Record rec) throws SQLException, IllegalArgumentException{
		if(rec==null)
			throw new IllegalArgumentException("insertRecord - null argument");
		try(Connection conn=getConnection()){
			try(PreparedStatement st=conn.prepareStatement(INSERT_RECORD_STRING)){
				st.setInt(1, rec.getId());
				st.setString(2, rec.getName());
				st.setString(3, rec.getLastName());
				st.executeUpdate();
	
			}
		}

	}
	
	

	public void updateRecord(String name ,String newLastName) throws SQLException, IllegalArgumentException{
		if(name==null || newLastName==null)
			throw new IllegalArgumentException("updateRecord - null argument(s)");
		try(Connection conn=getConnection()){
			try(PreparedStatement st=conn.prepareStatement(UPDATE_RECORD_STRING)){
				st.setString(1, newLastName);
				st.setString(2, name);
				st.executeUpdate();
	
			}
		}
	}
	

	public Record searchRecordByName(String name) throws SQLException, IllegalArgumentException{
		if(name==null)
			throw new IllegalArgumentException("searchRecordByName - null argument");
		Record rec=null;;
		try(Connection conn=getConnection()){
			try(PreparedStatement st=conn.prepareStatement(SELECT_RECORD_STRING)){
				st.setString(1, name);
				try(ResultSet res= st.executeQuery()){
					if(res.first())
						rec=new Record(res.getInt("id"), res.getString("name"),res.getString("lastname"));					

				}

	
			}
		}
		return rec;
	}
	
	public int deleteInsertedRecord(int id) throws SQLException{
		int res=0;
		try(Connection conn=getConnection()){
			try(PreparedStatement st=conn.prepareStatement(DELETE_RECORD_STRING)){
				st.setInt(1, id);
				res = st.executeUpdate();
	
			}
		}
		return res;
	}


}
