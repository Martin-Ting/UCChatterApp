/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cs166.webservices;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author mting005
 */
@WebService(serviceName = "MessageKeeper")
public class MessageKeeper {
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
    
    private static boolean MessageHasMedia(DBconnect esql, String msgID){
        try{	
            System.err.println("MessageHasMedia helper method called.");
            String urlQuery = String.format("SELECT * FROM MEDIA_ATTACHMENT WHERE msg_id ='%s'", msgID);
            int numRows = esql.executeQuery(urlQuery);
            System.out.println(" - Query executed: " + numRows);
            System.out.println(" - This message has exactly: " + numRows + " attachments.");
            return numRows > 0;
        }
        catch(SQLException e){
            System.err.println(e);
            System.out.println("Error!!");
            return false;
        }finally{
            System.err.println("End of MessageHasMedia."); 
        }
    }
    private static String ListMedia(DBconnect esql, String msgID){
        try{	
            System.err.println("ListMedia helper method called.");
            String urlQuery = String.format("SELECT * FROM MEDIA_ATTACHMENT WHERE msg_id ='%s'", msgID);
            ResultSet RS = esql.executeQueryRS(urlQuery);
            while(RS.next())
            {
                String result = String.format("Attachment[%s]: %s", RS.getString("media_type"), RS.getString("URL"));
                System.out.println(" - Retrieved Attachment: " + RS.getString("URL"));
                return result;
            }
            System.out.println(" - Retrieved Attachment: NOTHING! You've been errored our of existance!!! D:");
            return "Failed to retrieve attachment.";
        }
        catch(SQLException e){
            System.err.println(e);
            return "Failed to retrieve attachment.";
        }finally{
            System.err.println("End of ListMedia");
        }
    }	
    @WebMethod(operationName = "ListMessagesFromChat")
    public String ListMessagesFromChat(@WebParam(name = "ChatID") int chatID ) {
        System.err.println("ListMessagesFromChat Called. " + chatID);
        String ret = "success";
        try{
            DBconnect esql = new DBconnect();
            String query = String.format("SELECT * FROM MESSAGE WHERE chat_id ='%s'", chatID);
            ResultSet RS = esql.executeQueryRS(query);
            
            while(RS.next())
            {
                if( !MessageHasMedia(esql, RS.getString("msg_id")) ){
                    ret += "-"+RS.getString("sender_login") + "-" + RS.getString("msg_id")+"-"+ListMedia(esql, RS.getString("msg_id"));
                }
                else{
                    ret += "-" + RS.getString("sender_login")+"-"+ RS.getString("msg_id")+"-"+RS.getString("msg_text");
                }
                System.out.println(" - From: "+ RS.getString("sender_login"));
                System.out.println(" - Message: " + RS.getString("msg_text"));
                String msgID = String.format(" - Message ID: %s", RS.getString("msg_id"));
            }
            return ret;
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
            return "failed";
        }
        finally{
            System.err.println("End of ListMessagesFromChat.");
        }
        
    }
    private int CreateNewMessage(DBconnect esql, String currentUsr, int chatID, String contact, String msgTxt) throws SQLException{
        try{
            System.err.println("NewMessage Called. " + currentUsr + "|" + chatID + "|" + contact + "|" + msgTxt);
            int msgID;
            String beginTimeStamp, endTimeStamp, query_Msg;
            msgTxt = EscapeChar(msgTxt);
            
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd '@' hh:mm:ss a zzz");
            beginTimeStamp = ft.format(dNow);
            int monthTemp = (dNow.getMonth()+1)%13;
            if(monthTemp == 0)
                monthTemp = 1;
            dNow.setMonth(monthTemp);
            endTimeStamp = ft.format(dNow);
            
            query_Msg = String.format("INSERT INTO MESSAGE(msg_text, msg_timestamp, destr_timestamp, sender_login, chat_id) VALUES ('%s', '%s',  '%s', '%s', %s)", msgTxt, beginTimeStamp, endTimeStamp, currentUsr, chatID);				
            esql.executeUpdate(query_Msg);
            msgID = esql.getCurrSeqVal("message_msg_id_seq");
            return msgID;
            //AttachMedia(esql, msgID);
        }finally{
            System.err.println("End of helper method CreateNewMessage");
        }
    }
    
    @WebMethod(operationName = "NewMessage")
    public String NewMessage(@WebParam(name="currentUsr") String currentUsr, @WebParam(name="ChatID") int chatID, @WebParam(name="contact") String contact, 
                             @WebParam(name="msgTxt") String msgTxt) {
        try{
            DBconnect esql = new DBconnect();
            return "success-"+CreateNewMessage(esql, currentUsr, chatID, contact, msgTxt);
        }
        catch(SQLException E){
            System.err.println(E);
            return "failed";
        }
        finally{
            System.err.println("End of NewMessage.");
        }
    }
    
    
    @WebMethod(operationName = "NewMediaMessage")
    public String NewMediaMessage(@WebParam(name="currentUsr") String currentUsr, @WebParam(name="ChatID") int chatID, @WebParam(name="contact") String contact, 
                             @WebParam(name="msgTxt") String msgTxt, @WebParam(name="mediaType") String mediaType, @WebParam(name="URL") String URL) {
        try{
            DBconnect esql = new DBconnect();
            int msgID = CreateNewMessage(esql, currentUsr, chatID, contact, msgTxt);
            AttachMedia(esql, msgID, mediaType, URL);
            return "success-"+msgID;
        }
        catch(SQLException E){
            System.err.println(E);
            return "failed";
        }
        finally{
            System.err.println("End of NewMessage.");
        }
    }
    private static void AttachMedia(DBconnect esql, int msgID, String mediaType, String URL){
        try{
            String query = String.format("INSERT INTO MEDIA_ATTACHMENT(media_type, URL, msg_id) VALUES ('%s', '%s',  '%s')", mediaType, URL, msgID);				
            esql.executeUpdate(query);
        }
        catch(SQLException e){
            System.err.println(e);
        }				
    }
}
