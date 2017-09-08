package tests;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;


import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;


import dbapi.*;

public class TestingDB {
	@Mock
    private IMockDriverManager drv;
	
	
	@Mock
    private Connection conn;
    
    
    @Mock
    private PreparedStatement st;
    
    
    @Mock
    private ResultSet rs;

    Record[] initRecs=new Record[]{	new Record(1,"Alex","Bazin"),
    							   	new Record(2,"Anton","Gruzdev")};

    DBApi db=null;
    
    @BeforeClass
    public void init() {
    	MockitoAnnotations.initMocks(this);
    	
    }
    
    
    @BeforeMethod
    public void inserdData()  throws SQLException, IllegalArgumentException{
    	behaivorForInsertAndDeleteData();
    	db=new DBApi(drv);
    	db.insertRecord(initRecs[0]);
    	db.insertRecord(initRecs[1]);
    }
    
    
    private void behaivorForInsertAndDeleteData() throws SQLException{
    	when(drv.getConnection()).thenReturn(conn);
    	when(conn.prepareStatement(any(String.class))).thenReturn(st);
    	when(st.executeQuery()).thenReturn(rs);
    	when(st.executeUpdate()).thenReturn(1);
    }
    
    
    @AfterMethod
    public void deleteData() throws SQLException{
    	db.deleteInsertedRecord(initRecs[0].getId());
    	db.deleteInsertedRecord(initRecs[1].getId());
    	reset(drv);
    	reset(conn);
    	reset(st);
    	reset(rs);
    }
    
    

    @Test
    public void testingSearching()  throws SQLException, IllegalArgumentException{
    	behaivorForTestingSearching();
    	final String searchName=initRecs[0].getName();
    	Record searchedRec=db.searchRecordByName(searchName);
    	Assert.assertEquals(searchedRec.getName(), searchName);
    }
    
    
    private void behaivorForTestingSearching() throws SQLException{
    	when(drv.getConnection()).thenReturn(conn);
    	when(conn.prepareStatement(any(String.class))).thenReturn(st);
    	
    	when(st.executeQuery()).thenReturn(rs);
    	when(rs.first()).thenReturn(true);
    	
    	when(rs.getInt("id")).thenReturn(initRecs[0].getId());
    	when(rs.getString("name")).thenReturn(initRecs[0].getName());
    	when(rs.getString("lastname")).thenReturn(initRecs[0].getLastName());
    }
    
    @Test
    public void testingAlteringLastName() throws SQLException, IllegalArgumentException{
    	final String alteredName=initRecs[1].getName();
    	final String newLastName="Belov";
    	behaivorForTestingAlteringLastName(newLastName);
    	db.updateRecord(alteredName, newLastName);
    	
    	Record updRec=db.searchRecordByName(alteredName);
    	Assert.assertEquals(updRec.getLastName(), newLastName);
    }
    
    private void behaivorForTestingAlteringLastName(String newLastName) throws SQLException, IllegalArgumentException{
   	  	when(drv.getConnection()).thenReturn(conn);
    	when(conn.prepareStatement(any(String.class))).thenReturn(st);
    	when(st.executeUpdate()).thenReturn(1);
    	when(st.executeQuery()).thenReturn(rs);
    	when(rs.first()).thenReturn(true);
    	
    	when(rs.getInt("id")).thenReturn(initRecs[1].getId());
    	when(rs.getString("name")).thenReturn(initRecs[1].getName());
    	when(rs.getString("lastname")).thenReturn(newLastName);

    	
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testingArisingArgExc() throws SQLException, IllegalArgumentException{
    	db.insertRecord(null);
    }
}
