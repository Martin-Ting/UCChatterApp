/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cs166.webservices;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class DBconnect {
    //Database connect constants
    final private static String dbname = "postgres";
    final private static String dbport = "5432";
    final private static String user = "postgres";
    final private static String dburi = "localhost";//"138.23.221.174";
    final private static String password = "password166";
    
    //Construction 
    private Connection _connection = null;
    public DBconnect(){
        System.out.println("Messenger created.");
        try{
            System.out.println("Connecting to database...");
            String url = "jdbc:postgresql://" + dburi + ":" +dbport + "/" + dbname;
            System.out.println(" - database uri: " + url);
            Class.forName("org.postgresql.Driver");
            this._connection = DriverManager.getConnection(url, user, password);
            System.out.println(" - database connected.");
        }catch (ClassNotFoundException | SQLException e){
            System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
            System.out.println("Make sure you started postgres on this machine");
            //System.exit(-1);              // How to simulate this 
        }//end catch
    }
//==============================================================================
//==============================================================================
    private void close() {
        System.out.println("Closing up Messenger.");
        try{
           if (this._connection != null){
                this._connection.close ();
                System.out.println(" - Connection closed.");
            }//end if
        }catch (SQLException e){
            System.out.println(" - [Error] when connection closed.");
	}//end try
    }
    
    /* Method to execute an update SQL statement.  Update SQL instructions
     * includes CREATE, INSERT, UPDATE, DELETE, and DROP. */
    public void executeUpdate (String sql) throws SQLException {
        Statement stmt;
        stmt = this._connection.createStatement ();
        stmt.executeUpdate (sql);
        
        stmt.close ();
    }
    /* Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and outputs the results to
     * standard out.*/
    public int executeQueryAndPrintResult (String query) throws SQLException {
        Statement stmt;
        stmt = this._connection.createStatement ();
        ResultSet rs = stmt.executeQuery (query);
        /*
         ** obtains the metadata object for the returned result set.  The metadata
         ** contains row and column info.
         */
        ResultSetMetaData rsmd = rs.getMetaData ();
        int numCol = rsmd.getColumnCount ();
        int rowCount = 0;

        boolean outputHeader = true;
        while (rs.next()){
            if(outputHeader){
                for(int i = 1; i <= numCol; i++){
                    System.out.print(rsmd.getColumnName(i) + "\t");
                }
                System.out.println();
                outputHeader = false;
            }
            for (int i=1; i<=numCol; ++i)
                System.out.print (rs.getString (i) + "\t");
            System.out.println ();
            ++rowCount;
        }//end while
        stmt.close ();
        return rowCount;
    }//end executeQuery
    /*
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the number of results */
    public int executeQuery (String query) throws SQLException {
            Statement stmt;
            stmt= this._connection.createStatement ();
            ResultSet rs = stmt.executeQuery (query);
            int rowCount = 0;
            while(rs.next()){
                    rowCount++;
            }//end while
            stmt.close ();
            return rowCount;
	}
    public ResultSet executeQueryRS (String query) throws SQLException {
        Statement stmt = this._connection.createStatement ();
        ResultSet rs = stmt.executeQuery (query);
        return rs;
    }
    public int getCurrSeqVal(String sequence) throws SQLException {
        Statement stmt = this._connection.createStatement ();

        ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
        if (rs.next())
            return rs.getInt(1);
        return -1;
    }
//==============================================================================
//==============================================================================
//==============================================================================
//==============================================================================
//==============================================================================
//==============================================================================
//==============================================================================
//==============================================================================
//==============================================================================
//==============================================================================
    
    
}//end Messenger

