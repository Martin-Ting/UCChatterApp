/*

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cs166.webservices;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author mting005
 */
@WebService(serviceName = "SocialServices")
public class SocialServices {
    
    private static String EscapeChar(String text)
    {
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(text);
        char character = iterator.current();
        while(character != CharacterIterator.DONE)
        {
            //any invalid character in the database
            if(character == '\''|| character == '\"' || character == '<' 
            || character == '>' || character == '|' || character == ',' 
            || character == '^' || character == '=' || character == '(' 
            || character == ')' || character == '~' || character == '-'
            || character == '*' || character == '/' || character == '!')
            {
                    result.append('\\');
            }
            result.append(character);
            character = iterator.next();
        }
        return result.toString();
    }

    @WebMethod(operationName = "AddToContact")
    public String AddToContact(@WebParam(name = "username") String username, @WebParam(name = "usernameToAdd") String usernameToAdd, @WebParam(name = "chatListID") int chatListID) {
        username = EscapeChar(username);
        usernameToAdd = EscapeChar(usernameToAdd); 
        System.err.println("AddToContact Called - " + username + "|" + usernameToAdd + "|" + chatListID);
        
        DBconnect esql = new DBconnect();
        try{
            //Checking if usertoAdd is actually a user
            String query = String.format("SELECT * FROM Usr WHERE login = '%s'", usernameToAdd);
			
            int userNum = esql.executeQuery(query); 
            System.out.println(" - Query executed: " + query + "\n - Returned: " + userNum);
            if(userNum > 0) // usernameToAdd is a user.
            {
                String query_insert = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES (%s, '%s')", chatListID, usernameToAdd);	
                esql.executeUpdate(query_insert);
                System.out.println(" - Query executed: " + query_insert );
                System.out.println(" - Added to list");
                return "success-"+usernameToAdd;
            }
            else{
                System.out.println("User " + username + " does not exist");
                return "failed-"+username+" is not a user of this system.";
            }
        }
        catch(SQLException e){
                System.err.println (e.getMessage());
                System.out.println(" - Error.");
                return "failed-"+e.getMessage();
        }
        finally{
            System.err.println(" - End of AddToContact.");
        }
    }
    
    // TODO Check if this works in database.
    @WebMethod(operationName = "DeleteFromContacts")
    public String DeleteFromContacts(@WebParam(name = "username") String username, @WebParam(name = "usernameToDelete") String usernameToDelete, @WebParam(name = "chatListID") int chatListID) {
            DBconnect esql = new DBconnect();
            username = EscapeChar(username);
            usernameToDelete = EscapeChar(usernameToDelete);
            System.err.println("DeleteFromContacts called - " + username + "|" + usernameToDelete+"|"+chatListID);
        try{
            //check if the user actually on the list being deleted from
            String query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_member = '%s' AND list_id = %s", usernameToDelete, chatListID);
            int userNum = esql.executeQuery(query); 
            System.out.println(" - Query executed: " + query);
            if(userNum > 0)
            {
                String query_delete = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_member = '%s' AND list_id = %s", username, chatListID);	
                esql.executeUpdate(query_delete);
                System.out.println(" - Query executed: " + query_delete);
                System.out.println(" - Deleted from list");
                return "success-"+usernameToDelete;
            }
            else{
                System.out.println(" - ERROR: User " + username + " does not exist");
                return "failed-User " + username + " does not exist";
            }
        } catch (SQLException ex) {
            Logger.getLogger(SocialServices.class.getName()).log(Level.SEVERE, null, ex);
            return "failed-" + ex.getMessage();
        }finally{
            System.err.println(" - End of DeleteFromContacts");
        }
    }
    // TODO ADD TO BLOCK  / DELETE FROM BLOCK!!!!
    
    @WebMethod(operationName = "ListAll")
    public String ListAll(@WebParam(name = "listID") int listID) {
        DBconnect esql = new DBconnect();
        
        System.err.println("ListAll Called.");
        String ret = "success";
        Boolean retFlag = false;
        try{
            String query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id = %s", listID);
            ResultSet RS = esql.executeQueryRS(query);
            System.out.println(" - Query executed: " + query);
            while(RS.next())
            {
                retFlag = true;
                ret += "-" + RS.getString("list_member");
                System.out.println(" - \t"+RS.getString("list_member"));
            }
            if(retFlag){
                return ret;
            }
            else{
                return "success"; //emptyList
            }
        }
        catch(SQLException e){
                System.err.println(e.getMessage());
                return "failed";
        }finally{
            System.err.println("End of ListAll.");
        }
    }


}
