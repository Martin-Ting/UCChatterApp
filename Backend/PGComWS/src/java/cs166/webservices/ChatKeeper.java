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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author mting005
 */
@WebService(serviceName = "ChatKeeper")
public class ChatKeeper {
//==============================================================================
//================================ HELPER METHOD ===============================
//==============================================================================
    /*
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
    }*/
    
//==============================================================================
//================================ HELPER METHOD ===============================
//==============================================================================
    private static void InsertUsersIntoNewChat(DBconnect esql, String currentUsr, String contacts, int chatID, boolean groupBool){
        List<String> contactList = new ArrayList<>();
        try{
            System.err.println("CreateChatList helper method was called!");
            
            //This adds currentUsr into chat
            String query_chatList = String.format("INSERT INTO CHAT_LIST(chat_id, member) VALUES (%s, '%s')", chatID, currentUsr);
            esql.executeUpdate(query_chatList);
            System.out.println(" - Query executed: " + query_chatList);
            if(!groupBool)
            {
                System.out.println(" - Inserting single chat now");
                query_chatList = String.format("INSERT INTO CHAT_LIST(chat_id, member) VALUES (%s, '%s')", chatID, contacts);
                System.out.println(" - Query executed: " + query_chatList);
                esql.executeUpdate(query_chatList);
            }
            else{
                System.out.println(" - Inserting Group chat now");
                contactList.addAll(Arrays.asList(contacts.split("-")));
                for(int i = 0; i < contactList.size(); ++i){
                    System.out.println(" - Inserting " + contactList.get(i)+"...");
                    query_chatList = String.format("INSERT INTO CHAT_LIST(chat_id, member) VALUES (%s, '%s')", chatID, contactList.get(i));
                    esql.executeUpdate(query_chatList);
                    System.out.println(" - Query executed: " + query_chatList);
                }
            }
            //NewMessage(esql, chatID);                       NEW CHAT IS CREATED BUT NO MESSAGE IS CREATED AAH! D:
        }
        catch(SQLException E){
            System.err.println(E);
            System.err.println("Error.");
        } finally{
            System.err.println("End of  InsertUsersIntoNewChat");
        }
    }
    
    
    @WebMethod(operationName = "CreateNewChat")
    public String CreateNewChat(@WebParam(name="currentUsr") String currentUsr, @WebParam(name="chatID") int chatID, @WebParam(name="msgID") int msgID, @WebParam(name="chatType") int chatType, @WebParam(name="contacts") String contacts) {
        try{
            System.err.println("CreateNewChat Called.");
            DBconnect esql = new DBconnect();
            boolean groupBool;
            String query_insrtChat;
            if(chatType == 1)
            {
                query_insrtChat = String.format("INSERT INTO CHAT(chat_type, init_sender) VALUES ('%s', '%s')", "Single", currentUsr);
                groupBool = false;
                System.out.println(" - Chat Type is: " + "Single");
            }	
            else if(chatType == 2)
            {
                query_insrtChat = String.format("INSERT INTO CHAT(chat_type, init_sender) VALUES ('%s', '%s')", "Group", currentUsr);
                groupBool = true;
                System.out.println(" - Chat Type is: " + "Group");
            }
            else
            {
                System.out.println("Error: Not an option");
                return "failed-Could not create chat.";
            }
            esql.executeUpdate(query_insrtChat);
            System.out.println(" - Query executed: " + query_insrtChat);
            
            chatID = esql.getCurrSeqVal("chat_chat_id_seq");
            System.out.println(" - Chat ID: " + chatID);
            
            System.out.println(" - Inserting users into chat...");
            //Enters all contact into chat.
            InsertUsersIntoNewChat(esql, currentUsr, contacts, chatID, groupBool);
            System.out.println(" - Done inserting users.");
            return "success-"+chatID;
        }
        catch(SQLException E){
            System.err.println(E);	
            System.err.println("Error in CreateNewChat");
            return "failed-Could not create new chat";
        }
        finally{
            System.err.println("End of CreateNewChat");
        }
    }

    @WebMethod(operationName = "ListAllChat")
    public String ListAllChats(@WebParam(name = "currentUsr") String currentUsr) {
        try{
            System.err.println("ListAllChats has been called.");
            DBconnect esql = new DBconnect();
            String query = String.format("SELECT DISTINCT chat_id FROM CHAT_LIST WHERE member='%s'", currentUsr);
            ResultSet RS = esql.executeQueryRS(query);
            System.out.println(" - Query executed: " + query);
            
            String ret = "success";
            while(RS.next())
            {
                ret += "-" + RS.getString("chat_id");
                //How to find members of a certain chat:
                //String query2 = String.format("SELECT member FROM CHAT_LIST WHERE chat_id=%s", RS.getString("chat_id"));
                //ResultSet RS2 = esql.executeQueryRS(query2);
                //while(RS2.next())
                //    System.out.println("Member: " + RS2.getString("member"));
            }
            //ListMessages(esql);
            return ret;
        }
        catch(SQLException e){
                System.err.println(e.getMessage());
                return "failed";
        }
        finally{
            System.err.println("End of ListAllChats");
        }
    }

}

