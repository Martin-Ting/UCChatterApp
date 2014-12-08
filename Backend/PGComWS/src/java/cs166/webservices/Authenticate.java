/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cs166.webservices;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author mting005
 */
@WebService(serviceName = "Authenticate")
public class Authenticate {
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

    @WebMethod(operationName = "CreateUser")
    public String CreateUser(@WebParam(name = "username") String login, @WebParam(name = "password") String password, @WebParam(name = "phone") String phone) {
        System.out.println("createUser --");
        try{
            //fix input
            login = EscapeChar(login);
            password = EscapeChar(password);
            phone = EscapeChar(phone);
            System.out.println(" - inputFixed");
            //Database
            DBconnect esql = new DBconnect();
            System.out.println(" - db connected");
            //Creating empty contact\block lists for a user
            esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
            int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
            System.out.println(" - db query: " + "INSERT INTO USER_LIST(list_type) VALUES ('block')");
            esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
            int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
            System.out.println(" - db query: " + "INSERT INTO USER_LIST(list_type) VALUES ('contact')");

            String query = String.format("INSERT INTO USR (phoneNum, login, password, block_list, contact_list) VALUES ('%s','%s','%s',%s,%s)", phone, login, password, block_id, contact_id);
            System.out.println(" - db query: " + query);
            esql.executeUpdate(query);
            //return "success-"+contact_id+"-"+block_id+"-"+login+"-"+password+"-"+phone;
            return "success-"+login;
        }catch(SQLException e){
            System.err.println (e.getMessage ());
            System.out.println(" - ERROR");
            return "failed";
        }
        finally{
            System.err.println(" - End of createUser");
        }
    }
    
    @WebMethod(operationName = "LogIn")
    public String LogIn(@WebParam(name = "username") String username, @WebParam(name = "password") String password) {
        int blockLstID , contactLstID;
        System.err.println("LogIn Called. " + username +"|" + password);
        try{
            username = EscapeChar(username);
            password = EscapeChar(password);
            System.out.println(" - inputFixed");

            //Database
            DBconnect esql = new DBconnect();
            System.out.println(" - db connected");

            String query = String.format("SELECT * FROM usr WHERE login = '%s' AND password = '%s'", username, password);
            System.out.println(" - db query: " + query);
            
            ResultSet UsrResult = esql.executeQueryRS(query);
            while(UsrResult.next())
            {
                blockLstID = UsrResult.getInt("block_list");
                contactLstID = UsrResult.getInt("contact_list");
                System.out.println(" - Returning : " + username+"-"+blockLstID +"-"+contactLstID);		
                return "success"+"-"+username+"-"+blockLstID +"-"+contactLstID;
            }
            /*
            esql.executeQueryAndPrintResult(query);
            int userNum = esql.executeQuery(query);
            System.out.println(" - db query for userNum: " + query); 
            if (userNum > 0){
                System.out.println(" - Returning : " + username+"-"+blockLstID +"-"+contactLstID);	
                return "success"+"-"+zusername+"-"+blockLstID +"-"+contactLstID;
            }
            */
            return "failed-0-0";
        }catch(SQLException e){
            System.err.println (e.getMessage ());
            System.out.println(" - ERROR");
            return "failed-0-0";
        }finally{
            System.err.println(" - End of Log in.");
        }
    }
}

